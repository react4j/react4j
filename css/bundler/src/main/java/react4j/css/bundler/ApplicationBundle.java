package react4j.css.bundler;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import react4j.css.internal.AssetResource;

/** Assembles retained assets and explicit application files into the deployable tree. */
final class ApplicationBundle {
    private ApplicationBundle() {}

    public static void main(String[] args) throws IOException {
        run(
                1 == args.length && args[0].startsWith("@")
                        ? Files.readAllLines(Path.of(args[0].substring(1)))
                        : Arrays.asList(args));
    }

    static void run(List<String> args) throws IOException {
        var options = new TreeMap<String, Path>();
        var roots = new TreeMap<String, Path>();
        var definitions = new ArrayList<String>();
        var stripMaps = new TreeSet<String>();
        var optional = Set.of("--html", "--configuration");
        var required = Set.of("--directory", "--manifest", "--assets-directory", "--assets-manifest");
        for (int i = 0; i < args.size(); i += 2) {
            if (i + 1 == args.size()) {
                throw new IllegalArgumentException("Missing application bundle option value");
            }
            String option = args.get(i);
            String value = args.get(i + 1);
            if ("--root".equals(option)) {
                var root = JsonParser.parseString(value).getAsJsonObject();
                String name = root.get("name").getAsString();
                AssetResource.validatePath(name);
                if (null != roots.putIfAbsent(name, Path.of(root.get("source").getAsString()))) {
                    throw new IllegalArgumentException("Duplicate application destination: " + name);
                }
            } else if ("--define".equals(option)) {
                definitions.add(value);
            } else if ("--strip-map".equals(option)) {
                stripMaps.add(value);
            } else if (!(required.contains(option) || optional.contains(option))
                    || null != options.putIfAbsent(option, Path.of(value))) {
                throw new IllegalArgumentException("Unknown or repeated application option: " + option);
            }
        }
        if (!options.keySet().containsAll(required)) {
            throw new IllegalArgumentException("Expected application outputs and retained asset inputs");
        }
        var inventory = JsonParser.parseString(
                        Files.readString(Objects.requireNonNull(options.get("--assets-manifest"))))
                .getAsJsonObject();
        if (!"1".equals(inventory.get("version").toString())) {
            throw new IllegalArgumentException("Unsupported linked asset inventory");
        }
        var candidates = new TreeSet<String>();
        for (var item : inventory.getAsJsonArray("pruned")) {
            String path = item.getAsString();
            AssetResource.validatePath(path);
            if (!candidates.add(path)) {
                throw new IllegalArgumentException("Repeated pruned asset: " + path);
            }
        }
        var directory = Objects.requireNonNull(options.get("--directory"));
        Files.createDirectories(directory);
        try (var paths = Files.list(directory)) {
            if (paths.findAny().isPresent()) {
                throw new IllegalArgumentException("Application output directory must be empty");
            }
        }
        var assetsDirectory = Objects.requireNonNull(options.get("--assets-directory"));
        var entries = new ArrayList<JsonObject>();
        var expected = new TreeSet<String>();
        for (var item : inventory.getAsJsonArray("assets")) {
            var entry = item.getAsJsonObject();
            var content = AssetContent.managed(entry);
            if (!candidates.add(content.logicalPath())) {
                throw new IllegalArgumentException("Repeated linked asset: " + content.logicalPath());
            }
            var staged = new AssetContent(
                    content.logicalPath(), assetsDirectory.resolve(content.url()), content.sha256(), content.size());
            staged.copy(directory, content.url());
            expected.add(content.url());
            entries.add(entry.deepCopy());
        }
        try (var paths = Files.walk(assetsDirectory)) {
            var actual = new TreeSet<String>();
            paths.filter(Files::isRegularFile)
                    .forEach(path -> actual.add(
                            assetsDirectory.relativize(path).toString().replace('\\', '/')));
            if (!actual.equals(expected)) {
                throw new IllegalArgumentException("Linked asset directory differs from its inventory");
            }
        }
        String defines = ApplicationConfiguration.defines(definitions);
        if (options.containsKey("--html")) {
            if (null != roots.putIfAbsent("index.html", Objects.requireNonNull(options.get("--html")))) {
                throw new IllegalArgumentException("Duplicate application destination: index.html");
            }
        }
        if (options.containsKey("--configuration")) {
            String destination =
                    Objects.requireNonNull(options.get("--configuration")).toString();
            AssetResource.validatePath(destination);
            if (roots.containsKey(destination) || !expected.add(destination)) {
                throw new IllegalArgumentException("Duplicate application destination: " + destination);
            }
            var target = directory.resolve(destination);
            Files.createDirectories(Objects.requireNonNull(target.getParent()));
            Files.writeString(target, "var CLOSURE_DEFINES = " + defines + ";\n");
            entries.add(
                    AssetContent.prepare(destination, target).entry(destination, List.of(), List.of("application")));
        }
        for (var root : roots.entrySet()) {
            String destination = root.getKey();
            if (!expected.add(destination)) {
                throw new IllegalArgumentException("Duplicate application destination: " + destination);
            }
            var source = root.getValue();
            var target = directory.resolve(destination);
            Files.createDirectories(Objects.requireNonNull(target.getParent()));
            if ("index.html".equals(destination) && options.containsKey("--html")) {
                Files.writeString(target, Files.readString(source).replace("@@DEFINES@@", defines));
            } else if (stripMaps.contains(destination)) {
                Files.writeString(target, ApplicationConfiguration.withoutSourceMap(Files.readString(source)));
            } else {
                Files.copy(source, target);
            }
            var delivered = AssetContent.prepare(destination, target);
            var content = new AssetContent(destination, source, delivered.sha256(), delivered.size());
            entries.add(content.entry(destination, List.of(), List.of("application")));
        }
        entries.sort(Comparator.comparing(
                        (JsonObject entry) -> entry.get("logicalPath").getAsString())
                .thenComparing(entry -> entry.get("url").getAsString()));
        var assets = new JsonArray();
        entries.forEach(assets::add);
        inventory.add("assets", assets);
        Files.writeString(Objects.requireNonNull(options.get("--manifest")), inventory + "\n");
    }
}
