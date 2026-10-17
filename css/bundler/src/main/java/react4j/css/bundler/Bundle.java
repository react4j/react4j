package react4j.css.bundler;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.TreeMap;
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
        var outputs = new HashMap<String, Path>();
        for (int i = 0; i < args.size(); i += 2) {
            if (i + 1 == args.size()) {
                throw new IllegalArgumentException("Missing value for " + args.get(i));
            }
            String option = args.get(i);
            var path = Path.of(args.get(i + 1));
            switch (option) {
                case "--input" -> trees.add(path);
                case "--shared" -> shared.add(path);
                case "--css", "--map" -> {
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
        var result = assemble(trees, shared, css.getFileName().toString());
        Files.writeString(map, result.map());
        String reference = Objects.requireNonNull(css.toAbsolutePath().getParent())
                .relativize(map.toAbsolutePath())
                .toString()
                .replace('\\', '/');
        Files.writeString(css, result.css() + "/*# sourceMappingURL=" + reference + " */\n");
    }

    record Result(String css, String map) {}

    static Result assemble(List<Path> trees, List<Path> shared, String outputName) throws IOException {
        var resources = new TreeMap<String, Path>();
        for (var tree : trees.stream().distinct().toList()) {
            try (var files = Files.walk(tree)) {
                for (var path : files.filter(Files::isRegularFile).sorted().toList()) {
                    if (path.toString().endsWith(".css") || path.toString().endsWith(".css.map")) {
                        String name = tree.relativize(path).toString().replace('\\', '/');
                        if (null != resources.putIfAbsent(name, path)) {
                            throw new IllegalArgumentException("Duplicate CSS resource: " + name);
                        }
                    }
                }
            }
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
        if (sharedOpen) {
            writer.append("}\n");
            line++;
        }
        var components = resources.entrySet().stream()
                .filter(entry -> entry.getKey().endsWith(".css"))
                .toList();
        boolean componentsOpen = false;
        for (var entry : components) {
            Path path = entry.getValue();
            Path map = resources.get(entry.getKey() + ".map");
            if (null == map) {
                throw new IllegalArgumentException("Missing source map: " + entry.getKey());
            }
            String file = path.getFileName().toString();
            String css = Files.readString(path);
            String reference = SourceMap.reference(file);
            if (!css.endsWith(reference)) {
                throw new IllegalArgumentException("Missing component sourceMappingURL: " + entry.getKey());
            }
            css = css.substring(0, css.length() - reference.length());
            var parsed = readMap(map, file);
            if (css.isEmpty()) {
                continue;
            }
            if (!componentsOpen) {
                writer.append("@layer components{\n");
                line++;
                componentsOpen = true;
            }
            sections.add(section(line, parsed));
            writer.append(css).append('\n');
            line += (int) css.chars().filter(c -> '\n' == c).count() + 1;
        }
        if (componentsOpen) {
            writer.append("}\n");
        }
        for (var name : resources.keySet()) {
            if (name.endsWith(".css.map") && !resources.containsKey(name.substring(0, name.length() - 4))) {
                throw new IllegalArgumentException("Source map without component CSS: " + name);
            }
        }
        var map = new JsonObject();
        map.addProperty("version", 3);
        map.addProperty("file", outputName);
        map.add("sections", sections);
        return new Result(writer.toString(), map + "\n");
    }

    private static JsonObject readMap(Path path, String cssName) throws IOException {
        try {
            var map = JsonParser.parseString(Files.readString(path)).getAsJsonObject();
            if (!map.has("version")
                    || 3 != map.get("version").getAsInt()
                    || !map.has("file")
                    || !cssName.equals(map.get("file").getAsString())
                    || !map.has("mappings")
                    || !map.has("sources")
                    || !map.has("sourcesContent")) {
                throw new IllegalArgumentException("Invalid component source map: " + path);
            }
            return map;
        } catch (JsonParseException | IllegalStateException e) {
            throw new IllegalArgumentException("Invalid component source map: " + path, e);
        }
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
