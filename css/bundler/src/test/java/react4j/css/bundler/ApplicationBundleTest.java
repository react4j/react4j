package react4j.css.bundler;

import static org.testng.Assert.*;

import com.google.gson.JsonParser;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.TreeSet;
import org.jspecify.annotations.NullMarked;
import org.testng.annotations.Test;
import react4j.css.internal.CssLink;

@NullMarked
public final class ApplicationBundleTest {
    private static Path input(String kind) {
        return Path.of(Objects.requireNonNull(System.getProperty("application." + kind)));
    }

    private static List<String> options(Path directory) throws Exception {
        var html = directory.resolve("index.html");
        Files.writeString(html, "<!doctype html><title>Application</title>");
        var metadata = directory.resolve("html.json");
        Files.writeString(
                metadata, AssetContent.prepare("index.html", html, null).json().toString());
        return new ArrayList<>(List.of(
                "--directory",
                directory.resolve("web").toString(),
                "--manifest",
                directory.resolve("manifest.json").toString(),
                "--assets-directory",
                input("assets").toString(),
                "--assets-manifest",
                input("manifest").toString(),
                "--prepared",
                metadata.toString()));
    }

    private static List<String> prepare(List<String> args) throws Exception {
        var directory = Objects.requireNonNull(
                Path.of(args.get(args.indexOf("--directory") + 1)).getParent());
        var plan = directory.resolve("application-plan.json");
        var precache = directory.resolve("precache.json");
        var onDemand = directory.resolve("on-demand.json");
        var planner = new ArrayList<>(List.of(
                "--output",
                plan.toString(),
                "--precache-output",
                precache.toString(),
                "--on-demand-output",
                onDemand.toString()));
        var assembly = new ArrayList<String>();
        for (int i = 0; i < args.size(); i += 2) {
            String key = args.get(i);
            String value = args.get(i + 1);
            if ("--assets-manifest".equals(key)) {
                planner.addAll(List.of("--manifest", value));
            } else if ("--prepared".equals(key) || "--precache".equals(key)) {
                planner.addAll(List.of(key, value));
            } else {
                assembly.addAll(List.of(key, value));
            }
        }
        var arguments = directory.resolve("plan.args");
        Files.write(arguments, planner);
        ApplicationPlan.main(new String[] {"@" + arguments});
        assembly.addAll(List.of("--plan", plan.toString()));
        for (var file : List.of(precache, onDemand)) {
            var metadata = directory.resolve(file.getFileName() + ".prepared.json");
            Files.writeString(
                    metadata,
                    AssetContent.prepare(file.getFileName().toString(), file, null)
                            .json()
                            .toString());
            assembly.addAll(List.of("--inventory", metadata.toString()));
        }
        return assembly;
    }

    private static List<String> change(List<String> args, String key, Path value) {
        var changed = new ArrayList<>(args);
        changed.set(changed.indexOf(key) + 1, value.toString());
        return changed;
    }

    @Test
    public void realRetainedAssetsDrivePackagingAndPolicyNeverRootsDeadCandidates() throws Exception {
        var directory = Files.createTempDirectory("application-policy");
        try {
            var original =
                    JsonParser.parseString(Files.readString(input("manifest"))).getAsJsonObject();
            assertEquals(original.getAsJsonArray("assets").size(), 1);
            var asset = original.getAsJsonArray("assets").get(0).getAsJsonObject();
            String logical = asset.get("logicalPath").getAsString();
            String url = asset.get("url").getAsString();
            String dead = original.getAsJsonArray("pruned").get(0).getAsString();
            var options = options(directory);
            options.addAll(List.of("--precache", "index.html", "--precache", logical, "--precache", dead));
            ApplicationBundle.run(prepare(options));
            var manifest = JsonParser.parseString(Files.readString(directory.resolve("manifest.json")))
                    .getAsJsonObject();
            assertEquals(manifest.getAsJsonArray("pruned"), original.getAsJsonArray("pruned"));
            assertEquals(manifest.getAsJsonArray("assets").size(), 4);
            assertEquals(
                    manifest.getAsJsonArray("precache").asList().stream()
                            .map(value -> value.getAsString())
                            .toList(),
                    List.of(url, "index.html"));
            assertTrue(manifest.getAsJsonArray("onDemand").isEmpty());
            assertEquals(Files.readString(directory.resolve("web/precache.json")), manifest.get("precache") + "\n");
            assertEquals(Files.readString(directory.resolve("web/on-demand.json")), "[]\n");
            assertFalse(Files.exists(directory.resolve("web").resolve(dead)));
            var paths = new TreeSet<String>();
            for (var item : manifest.getAsJsonArray("assets")) {
                var entry = item.getAsJsonObject();
                String location = entry.get("url").getAsString();
                assertEquals(
                        CssLink.digest(directory.resolve("web").resolve(location)),
                        entry.get("sha256").getAsString());
                assertEquals(
                        Files.size(directory.resolve("web").resolve(location)),
                        entry.get("size").getAsLong());
                entry.getAsJsonObject("representations")
                        .entrySet()
                        .forEach(representation ->
                                paths.add(representation.getValue().getAsString()));
            }
            try (var files = Files.walk(directory.resolve("web"))) {
                assertEquals(files.filter(Files::isRegularFile).count(), (long) paths.size());
            }
            var repeated = change(options, "--directory", directory.resolve("repeat"));
            repeated = change(repeated, "--manifest", directory.resolve("repeat.json"));
            ApplicationBundle.run(prepare(repeated));
            assertEquals(Files.readString(directory.resolve("repeat.json")), manifest + "\n");
            var demand = options(directory);
            demand = change(demand, "--directory", directory.resolve("demand"));
            demand = change(demand, "--manifest", directory.resolve("demand.json"));
            demand.addAll(List.of("--precache", dead));
            var arguments = directory.resolve("arguments");
            Files.write(arguments, prepare(demand));
            ApplicationBundle.main(new String[] {"@" + arguments});
            var onDemand = JsonParser.parseString(Files.readString(directory.resolve("demand.json")))
                    .getAsJsonObject();
            assertTrue(onDemand.getAsJsonArray("precache").isEmpty());
            assertEquals(onDemand.getAsJsonArray("onDemand").size(), 2);
            expectThrows(IllegalArgumentException.class, () -> ApplicationBundle.run(prepare(options)));
        } finally {
            AssetContentTest.delete(directory);
        }
    }

    @Test
    public void malformedPoliciesInventoriesAndConflictingOutputsFail() throws Exception {
        var directory = Files.createTempDirectory("application-errors");
        try {
            for (var invalid : List.of(
                    List.<String>of(),
                    List.of("--directory"),
                    List.of("--unknown", "a"),
                    List.of("--directory", "a", "--directory", "b"),
                    List.of("--precache", "../escape"),
                    List.of("--precache", "same", "--precache", "same"))) {
                expectThrows(IllegalArgumentException.class, () -> ApplicationBundle.run(invalid));
                expectThrows(IllegalArgumentException.class, () -> ApplicationPlan.run(invalid));
            }
            expectThrows(IllegalArgumentException.class, () -> ApplicationBundle.main(new String[0]));
            expectThrows(IllegalArgumentException.class, () -> ApplicationPlan.main(new String[0]));
            int index = 0;
            for (var field :
                    List.of("version", "url", "mediaType", "sha256", "size", "logicalPath", "representations")) {
                var json = JsonParser.parseString(Files.readString(input("manifest")))
                        .getAsJsonObject();
                if ("version".equals(field)) {
                    json.addProperty("version", 100);
                } else {
                    var asset = json.getAsJsonArray("assets").get(0).getAsJsonObject();
                    if ("size".equals(field)) {
                        asset.addProperty(field, 1);
                    } else if ("representations".equals(field)) {
                        asset.getAsJsonObject(field).addProperty("identity", "different");
                    } else {
                        asset.addProperty(field, "wrong");
                    }
                }
                var manifest = directory.resolve("bad" + index++ + ".json");
                Files.writeString(manifest, json.toString());
                var run = Files.createDirectory(directory.resolve("run" + index));
                expectThrows(
                        IllegalArgumentException.class,
                        () -> ApplicationBundle.run(prepare(change(options(run), "--assets-manifest", manifest))));
            }
            var unknown = Files.createDirectory(directory.resolve("unknown"));
            var options = options(unknown);
            options.addAll(List.of("--precache", "absent.svg"));
            expectThrows(IllegalArgumentException.class, () -> ApplicationBundle.run(prepare(options)));
            for (var name : List.of("precache.json", "on-demand.json", "index.html")) {
                var run = Files.createTempDirectory(directory, "collision");
                var metadata = run.resolve("root.json");
                var content = AssetContent.prepare(name, input("manifest"), null);
                Files.writeString(metadata, content.json().toString());
                var args = options(run);
                args.addAll(List.of("--prepared", metadata.toString()));
                expectThrows(IllegalArgumentException.class, () -> ApplicationBundle.run(prepare(args)));
            }
            var empty = Files.createDirectory(directory.resolve("empty-assets"));
            var mismatch = Files.createDirectory(directory.resolve("mismatch"));
            expectThrows(
                    IOException.class,
                    () -> ApplicationBundle.run(prepare(change(options(mismatch), "--assets-directory", empty))));
            var extra = Files.createDirectory(directory.resolve("extra-assets"));
            try (var paths = Files.walk(input("assets"))) {
                for (var file : paths.filter(Files::isRegularFile).toList()) {
                    var target = extra.resolve(input("assets").relativize(file));
                    Files.createDirectories(Objects.requireNonNull(target.getParent()));
                    Files.copy(file, target);
                }
            }
            Files.writeString(extra.resolve("undeclared"), "not in inventory");
            var extraRun = Files.createDirectory(directory.resolve("extra-run"));
            expectThrows(
                    IllegalArgumentException.class,
                    () -> ApplicationBundle.run(prepare(change(options(extraRun), "--assets-directory", extra))));
        } finally {
            AssetContentTest.delete(directory);
        }
    }
}
