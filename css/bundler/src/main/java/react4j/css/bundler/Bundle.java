package react4j.css.bundler;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import org.jspecify.annotations.Nullable;
import react4j.css.internal.CssCatalogue;
import react4j.css.internal.CssCatalogue.Resource;
import react4j.css.internal.CssLink;
import react4j.css.internal.CssRenamer;
import react4j.css.internal.CssResource;
import react4j.css.internal.CssResource.Kind;
import react4j.css.internal.CssSyntax;
import react4j.css.internal.SourceMap;

/** Assembles processor resources from declared J2CL output trees. */
public final class Bundle {
    private Bundle() {}

    public static void main(String[] args) {
        try {
            if (1 != args.length || !args[0].startsWith("@")) {
                throw new IllegalArgumentException("Usage: bundler @argument-file");
            }
            run(Files.readAllLines(Path.of(args[0].substring(1))));
        } catch (IOException | IllegalArgumentException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    static void run(List<String> args) throws IOException {
        var trees = new ArrayList<Path>();
        var shared = new ArrayList<Path>();
        var globals = new ArrayList<String>();
        var outputs = new HashMap<String, Path>();
        String linkName = null;
        for (int i = 0; i < args.size(); i += 2) {
            if (i + 1 == args.size()) {
                throw new IllegalArgumentException("Missing value for " + args.get(i));
            }
            String option = args.get(i);
            var path = Path.of(args.get(i + 1));
            switch (option) {
                case "--input" -> trees.add(path);
                case "--shared" -> shared.add(path);
                case "--global" -> globals.add(args.get(i + 1));
                case "--link" -> {
                    if (null != linkName) {
                        throw new IllegalArgumentException("Repeated --link");
                    }
                    linkName = args.get(i + 1);
                }
                case "--css", "--map", "--link-config", "--liveness", "--javascript" -> {
                    if (null != outputs.putIfAbsent(option, path)) {
                        throw new IllegalArgumentException("Repeated output " + option);
                    }
                }
                default -> throw new IllegalArgumentException("Unknown option " + option);
            }
        }
        if (!outputs.containsKey("--css") || !outputs.containsKey("--map")) {
            throw new IllegalArgumentException("Expected --css and --map outputs");
        }
        Path css = outputs.get("--css");
        Path map = outputs.get("--map");
        if (css.equals(map)) {
            throw new IllegalArgumentException("CSS and source-map outputs must differ");
        }
        var catalogue = CssCatalogue.read(trees);
        CssLink link = null;
        Set<String> live = Set.of();
        if (outputs.containsKey("--link-config")
                || outputs.containsKey("--liveness")
                || outputs.containsKey("--javascript")
                || null != linkName) {
            if (!outputs.keySet().containsAll(Set.of("--link-config", "--liveness", "--javascript"))
                    || null == linkName) {
                throw new IllegalArgumentException(
                        "Expected --link-config, --liveness, --javascript and --link together");
            }
            link = CssLink.read(Files.readString(outputs.get("--link-config")));
            var expected = CssLink.create(
                    catalogue, linkName, link.optimized(), link.names().policy());
            if (!expected.equals(link)) {
                throw new IllegalArgumentException("CSS link configuration does not match this link and catalogue");
            }
            live = link.readReport(
                    Files.readString(outputs.get("--liveness")),
                    CssLink.digest(Objects.requireNonNull(outputs.get("--javascript"))));
        }
        var result = assemble(catalogue, shared, globals, css.getFileName().toString(), link, live);
        Files.writeString(map, result.map());
        String reference = Objects.requireNonNull(css.toAbsolutePath().getParent())
                .relativize(map.toAbsolutePath())
                .toString()
                .replace('\\', '/');
        Files.writeString(css, result.css() + "/*# sourceMappingURL=" + reference + " */\n");
    }

    record Result(String css, String map) {}

    static Result assemble(List<Path> trees, List<Path> shared, List<String> globals, String outputName)
            throws IOException {
        return assemble(CssCatalogue.read(trees), shared, globals, outputName, null, Set.of());
    }

    static Result assemble(
            CssCatalogue catalogue,
            List<Path> shared,
            List<String> globals,
            String outputName,
            @Nullable CssLink link,
            Set<String> live)
            throws IOException {
        boolean optimized = null != link && link.optimized();
        for (var owner : globals) {
            var definition = catalogue.resources().get(owner);
            if (null == definition || Kind.GLOBAL != definition.metadata().kind()) {
                throw new IllegalArgumentException(
                        "Global CSS root must reference an available @GlobalCss definition: " + owner);
            }
        }
        var definitions = optimized
                ? CssReachability.prune(catalogue, Objects.requireNonNull(link), live, globals)
                : catalogue.resources();
        var retained = new HashSet<String>();
        if (optimized) {
            retained.addAll(definitions.keySet());
        } else {
            for (var owner : globals) {
                retain(owner, definitions, retained);
            }
            for (var definition : definitions.values()) {
                if (Kind.COMPONENT == definition.metadata().kind()
                        || !definition.metadata().bindings().isEmpty()) {
                    retain(definition.metadata().owner(), definitions, retained);
                }
            }
        }
        Function<CssResource, List<String>> dependencies = metadata ->
                metadata.dependencies().stream().filter(retained::contains).toList();
        var ordered = new ArrayList<Resource>();
        var visited = new HashSet<String>();
        var visiting = new LinkedHashSet<String>();
        for (var owner : globals) {
            checkInclusions(owner, definitions, visited, visiting);
        }
        for (var owner : definitions.keySet()) {
            if (retained.contains(owner)) {
                checkInclusions(owner, definitions, visited, visiting);
            }
        }
        visited.clear();
        for (var owner : globals) {
            orderGlobal(owner, definitions, visited, visiting, ordered, dependencies);
        }
        for (var definition : definitions.values()) {
            if (retained.contains(definition.metadata().owner())) {
                for (var dependency : dependencies.apply(definition.metadata())) {
                    if (Kind.GLOBAL
                            == Objects.requireNonNull(definitions.get(dependency))
                                    .metadata()
                                    .kind()) {
                        orderGlobal(dependency, definitions, visited, visiting, ordered, dependencies);
                    }
                }
            }
        }
        for (var definition : definitions.values()) {
            if (retained.contains(definition.metadata().owner())
                    && Kind.GLOBAL == definition.metadata().kind()) {
                orderGlobal(definition.metadata().owner(), definitions, visited, visiting, ordered, dependencies);
            }
        }
        // Cyclic property dependencies must still respect the acyclic inclusion constraints.
        var constrained = new ArrayList<Resource>();
        visited.clear();
        for (var definition : ordered) {
            orderGlobal(
                    definition.metadata().owner(), definitions, visited, visiting, constrained, CssResource::includes);
        }
        ordered = constrained;
        definitions.values().stream()
                .filter(d -> retained.contains(d.metadata().owner())
                        && Kind.GLOBAL != d.metadata().kind())
                .forEach(ordered::add);
        if (null != link) {
            var renamer = new CssRenamer(link.names());
            ordered = new ArrayList<>(ordered.stream().map(renamer::rename).toList());
        }
        var writer = new StringBuilder("@layer shared,components;\n");
        var sections = new JsonArray();
        int line = 1;
        boolean sharedOpen = false;
        for (var path : shared) {
            String original = Files.readString(path);
            String css = original.replace("\r\n", "\n").replace('\r', '\n');
            try {
                if (0 == new CssSyntax(css, 0).parse(false).getRuleCount()) {
                    continue;
                }
            } catch (CssSyntax.InvalidCss e) {
                throw new IllegalArgumentException(path + ": " + e.getMessage(), e);
            }
            var mapped = new SourceMap.Writer();
            var lines = css.split("\n", -1);
            for (int i = 0; i < lines.length; i++) {
                mapped.append(lines[i], new SourceMap.Location(path.toString().replace('\\', '/'), original, i, 0));
                if (i + 1 < lines.length) {
                    mapped.append("\n");
                }
            }
            if (!sharedOpen) {
                writer.append("@layer shared{\n");
                line++;
                sharedOpen = true;
            }
            sections.add(section(
                    line,
                    JsonParser.parseString(mapped.map(path.getFileName().toString()))
                            .getAsJsonObject()));
            writer.append(css).append('\n');
            line += (int) css.chars().filter(c -> '\n' == c).count() + 1;
        }
        for (var definition : ordered) {
            if (Kind.GLOBAL != definition.metadata().kind() || definition.css().isEmpty()) {
                continue;
            }
            if (!sharedOpen) {
                writer.append("@layer shared{\n");
                line++;
                sharedOpen = true;
            }
            sections.add(section(line, definition.map()));
            writer.append(definition.css()).append('\n');
            line += (int) definition.css().chars().filter(c -> '\n' == c).count() + 1;
        }
        if (sharedOpen) {
            writer.append("}\n");
            line++;
        }
        boolean componentsOpen = false;
        for (var definition : ordered) {
            if (Kind.GLOBAL == definition.metadata().kind()) {
                continue;
            }
            String css = definition.css();
            if (css.isEmpty()) {
                continue;
            }
            if (!componentsOpen) {
                writer.append("@layer components{\n");
                line++;
                componentsOpen = true;
            }
            sections.add(section(line, definition.map()));
            writer.append(css).append('\n');
            line += (int) css.chars().filter(c -> '\n' == c).count() + 1;
        }
        if (componentsOpen) {
            writer.append("}\n");
        }
        var map = new JsonObject();
        map.addProperty("version", 3);
        map.addProperty("file", outputName);
        map.add("sections", sections);
        return new Result(writer.toString(), map + "\n");
    }

    private static void retain(String owner, Map<String, Resource> definitions, Set<String> retained) {
        if (!retained.add(owner)) {
            return;
        }
        var definition = definitions.get(owner);
        if (null == definition) {
            throw new IllegalArgumentException("Missing CSS dependency: " + owner);
        }
        for (var dependency : definition.metadata().dependencies()) {
            retain(dependency, definitions, retained);
        }
    }

    private static void checkInclusions(
            String owner, Map<String, Resource> definitions, Set<String> visited, Set<String> visiting) {
        if (visited.contains(owner)) {
            return;
        }
        if (!visiting.add(owner)) {
            throw new IllegalArgumentException(
                    "Cyclic global CSS inclusion: " + String.join(" -> ", visiting) + " -> " + owner);
        }
        var definition = Objects.requireNonNull(definitions.get(owner));
        for (var included : definition.metadata().includes()) {
            if (Kind.GLOBAL
                    == Objects.requireNonNull(definitions.get(included))
                            .metadata()
                            .kind()) {
                checkInclusions(included, definitions, visited, visiting);
            }
        }
        visiting.remove(owner);
        visited.add(owner);
    }

    private static void orderGlobal(
            String owner,
            Map<String, Resource> definitions,
            Set<String> visited,
            Set<String> visiting,
            List<Resource> ordered,
            Function<CssResource, List<String>> edges) {
        if (visited.contains(owner) || !visiting.add(owner)) {
            return;
        }
        var definition = Objects.requireNonNull(definitions.get(owner));
        for (var dependency : edges.apply(definition.metadata())) {
            if (Kind.GLOBAL
                    == Objects.requireNonNull(definitions.get(dependency))
                            .metadata()
                            .kind()) {
                orderGlobal(dependency, definitions, visited, visiting, ordered, edges);
            }
        }
        visiting.remove(owner);
        visited.add(owner);
        ordered.add(definition);
    }

    private static JsonObject section(int line, JsonObject map) {
        var offset = new JsonObject();
        offset.addProperty("line", line);
        offset.addProperty("column", 0);
        var section = new JsonObject();
        section.add("offset", offset);
        section.add("map", map);
        return section;
    }
}
