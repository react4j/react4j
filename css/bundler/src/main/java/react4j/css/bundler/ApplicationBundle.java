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
        var roots = new ArrayList<AssetContent>();
        var required = Set.of("--directory", "--manifest", "--assets-directory", "--assets-manifest");
        for (int i = 0; i < args.size(); i += 2) {
            if (i + 1 == args.size()) {
                throw new IllegalArgumentException("Missing application bundle option value");
            }
            String option = args.get(i);
            String value = args.get(i + 1);
            if ("--prepared".equals(option)) {
                roots.add(AssetContent.read(Path.of(value)));
            } else if (!required.contains(option) || null != options.putIfAbsent(option, Path.of(value))) {
                throw new IllegalArgumentException("Unknown or repeated application option: " + option);
            }
        }
        if (!options.keySet().equals(required)) {
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
        for (var content : roots) {
            String destination = content.logicalPath();
            if (!expected.add(destination)) {
                throw new IllegalArgumentException("Duplicate application destination: " + destination);
            }
            content.copy(directory, destination);
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
