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

/** Assembles retained files and their useful representations into the deployable application tree. */
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
        var lists = new TreeMap<String, AssetContent>();
        var required = Set.of("--directory", "--manifest", "--assets-directory", "--plan");
        for (int i = 0; i < args.size(); i += 2) {
            if (i + 1 == args.size()) {
                throw new IllegalArgumentException("Missing application bundle option value");
            }
            String option = args.get(i);
            String value = args.get(i + 1);
            if ("--inventory".equals(option)) {
                var content = AssetContent.read(Path.of(value));
                if (null != lists.putIfAbsent(content.logicalPath(), content)) {
                    throw new IllegalArgumentException("Repeated application inventory: " + content.logicalPath());
                }
            } else if (!required.contains(option) || null != options.putIfAbsent(option, Path.of(value))) {
                throw new IllegalArgumentException("Unknown or repeated application option: " + option);
            }
        }
        if (!options.keySet().equals(required) || !lists.keySet().equals(Set.of("precache.json", "on-demand.json"))) {
            throw new IllegalArgumentException(
                    "Expected application outputs, retained asset inputs and prepared offline lists");
        }
        var plan = JsonParser.parseString(Files.readString(Objects.requireNonNull(options.get("--plan"))))
                .getAsJsonObject();
        if (!"1".equals(plan.get("version").toString())) {
            throw new IllegalArgumentException("Unsupported application plan");
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
        for (var item : plan.getAsJsonArray("assets")) {
            var entry = item.getAsJsonObject();
            var content = AssetContent.managed(entry);
            var staged = new AssetContent(
                    content.logicalPath(),
                    assetsDirectory.resolve(content.url()),
                    content.sha256(),
                    content.size(),
                    null == content.compressed() ? null : assetsDirectory.resolve(content.compressed()),
                    content.compressedSize());
            staged.copy(directory, content.url());
            expected.add(content.url());
            if (null != content.compressed()) {
                expected.add(content.url() + ".br");
            }
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
        for (var item : plan.getAsJsonArray("roots")) {
            var content = AssetContent.read(item.getAsJsonObject());
            content.copy(directory, content.logicalPath());
            entries.add(content.entry(content.logicalPath(), List.of(), List.of("application")));
        }
        for (var entry : lists.entrySet()) {
            var content = entry.getValue();
            String key = "precache.json".equals(entry.getKey()) ? "precache" : "onDemand";
            if (!Files.readString(content.source()).equals(plan.get(key) + "\n")) {
                throw new IllegalArgumentException("Offline list differs from its application plan: " + entry.getKey());
            }
            content.copy(directory, entry.getKey());
            var metadata = content.entry(entry.getKey(), List.of(), List.of("application:inventory"));
            metadata.addProperty("source", "generated:" + entry.getKey());
            entries.add(metadata);
        }
        entries.sort(Comparator.comparing(
                        (JsonObject entry) -> entry.get("logicalPath").getAsString())
                .thenComparing(entry -> entry.get("url").getAsString()));
        var assets = new JsonArray();
        entries.forEach(assets::add);
        plan.remove("roots");
        plan.add("assets", assets);
        Files.writeString(Objects.requireNonNull(options.get("--manifest")), plan + "\n");
    }
}
