package react4j.css.bundler;

import static org.testng.Assert.*;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import org.jspecify.annotations.NullMarked;
import org.testng.annotations.Test;
import react4j.css.internal.CssLink;

@NullMarked
public final class ApplicationBundleTest {
    private static Path input(String kind) {
        return Path.of(Objects.requireNonNull(System.getProperty("application." + kind)));
    }

    private static Path prepare(Path directory, String name, Path source) throws Exception {
        var metadata = directory.resolve(name.replace('/', '_') + ".prepared.json");
        Files.writeString(metadata, AssetContent.prepare(name, source).json().toString());
        return metadata;
    }

    private static List<String> options(Path directory) throws Exception {
        var html = directory.resolve("index.html");
        Files.writeString(html, "<!doctype html><title>Application</title>");
        return new ArrayList<>(List.of(
                "--directory", directory.resolve("web").toString(),
                "--manifest", directory.resolve("manifest.json").toString(),
                "--assets-directory", input("assets").toString(),
                "--assets-manifest", input("manifest").toString(),
                "--prepared", prepare(directory, "index.html", html).toString()));
    }

    private static List<String> change(List<String> args, String key, Path value) {
        var changed = new ArrayList<>(args);
        changed.set(changed.indexOf(key) + 1, value.toString());
        return changed;
    }

    private static JsonObject inventory() throws Exception {
        return JsonParser.parseString(Files.readString(input("manifest"))).getAsJsonObject();
    }

    @Test
    public void retainedAssetsAndExplicitFilesExactlyMatchTheCompleteManifest() throws Exception {
        var directory = Files.createTempDirectory("application-bundle");
        try {
            var original = inventory();
            assertEquals(original.getAsJsonArray("assets").size(), 1);
            var options = options(directory);
            ApplicationBundle.run(options);
            var manifest = JsonParser.parseString(Files.readString(directory.resolve("manifest.json")))
                    .getAsJsonObject();
            assertEquals(manifest.keySet(), Set.of("version", "assets", "pruned"));
            assertEquals(manifest.getAsJsonArray("pruned"), original.getAsJsonArray("pruned"));
            assertEquals(manifest.getAsJsonArray("assets").size(), 2);
            var paths = new TreeSet<String>();
            for (var item : manifest.getAsJsonArray("assets")) {
                var entry = item.getAsJsonObject();
                String location = entry.get("url").getAsString();
                paths.add(location);
                assertEquals(
                        CssLink.digest(directory.resolve("web").resolve(location)),
                        entry.get("sha256").getAsString());
                assertEquals(
                        Files.size(directory.resolve("web").resolve(location)),
                        entry.get("size").getAsLong());
                assertFalse(entry.get("mediaType").getAsString().isBlank());
                assertEquals(
                        entry.keySet(),
                        Set.of(
                                "logicalPath",
                                "source",
                                "sha256",
                                "size",
                                "url",
                                "mediaType",
                                "accessors",
                                "retainedBy"));
            }
            try (var files = Files.walk(directory.resolve("web"))) {
                assertEquals(
                        files.filter(Files::isRegularFile)
                                .map(path -> directory
                                        .resolve("web")
                                        .relativize(path)
                                        .toString()
                                        .replace('\\', '/'))
                                .collect(Collectors.toSet()),
                        paths);
            }
            var repeated = change(options, "--directory", directory.resolve("repeat"));
            repeated = change(repeated, "--manifest", directory.resolve("repeat.json"));
            var arguments = directory.resolve("arguments");
            Files.write(arguments, repeated);
            ApplicationBundle.main(new String[] {"@" + arguments});
            assertEquals(Files.readString(directory.resolve("repeat.json")), manifest + "\n");
            expectThrows(IllegalArgumentException.class, () -> ApplicationBundle.run(options));
        } finally {
            AssetContentTest.delete(directory);
        }
    }

    @Test
    public void compatibleManagedAliasesShareOneFileAndEmptyAssetSetsAreSupported() throws Exception {
        var directory = Files.createTempDirectory("application-aliases");
        try {
            var json = inventory();
            var alias = json.getAsJsonArray("assets").get(0).getAsJsonObject().deepCopy();
            alias.addProperty("logicalPath", "demo/alias.svg");
            json.getAsJsonArray("assets").add(alias);
            var manifest = directory.resolve("aliases.json");
            Files.writeString(manifest, json.toString());
            ApplicationBundle.run(change(options(directory), "--assets-manifest", manifest));
            try (var files = Files.walk(directory.resolve("web"))) {
                assertEquals(files.filter(Files::isRegularFile).count(), 2L);
            }
            var output = JsonParser.parseString(Files.readString(directory.resolve("manifest.json")))
                    .getAsJsonObject();
            assertEquals(output.getAsJsonArray("assets").size(), 3);
            var empty = Files.createDirectory(directory.resolve("empty"));
            var assets = Files.createDirectory(empty.resolve("assets"));
            json.getAsJsonArray("assets").asList().clear();
            json.getAsJsonArray("pruned").asList().clear();
            Files.writeString(manifest, json.toString());
            var args = change(options(empty), "--assets-manifest", manifest);
            args = change(args, "--assets-directory", assets);
            ApplicationBundle.run(args);
            assertTrue(Files.isRegularFile(empty.resolve("web/index.html")));
            try (var files = Files.list(empty.resolve("web"))) {
                assertEquals(files.count(), 1L);
            }
        } finally {
            AssetContentTest.delete(directory);
        }
    }

    @Test
    public void explicitFilesPreserveNamesAndNestedDestinations() throws Exception {
        var directory = Files.createTempDirectory("application-files");
        try {
            var options = options(directory);
            for (var name : List.of("precache.json", "on-demand.json", "nested/page.html")) {
                var source = directory.resolve(name.replace('/', '_'));
                Files.writeString(source, "application supplied " + name);
                options.addAll(
                        List.of("--prepared", prepare(directory, name, source).toString()));
            }
            ApplicationBundle.main(options.toArray(String[]::new));
            for (var name : List.of("precache.json", "on-demand.json", "nested/page.html")) {
                assertEquals(Files.readString(directory.resolve("web").resolve(name)), "application supplied " + name);
            }
        } finally {
            AssetContentTest.delete(directory);
        }
    }

    @Test
    public void malformedInventoriesAndConflictingDestinationsFail() throws Exception {
        var directory = Files.createTempDirectory("application-errors");
        try {
            for (var invalid : List.of(
                    List.<String>of(),
                    List.of("--directory"),
                    List.of("--unknown", "a"),
                    List.of("--directory", "a", "--directory", "b"))) {
                expectThrows(IllegalArgumentException.class, () -> ApplicationBundle.run(invalid));
            }
            expectThrows(IllegalArgumentException.class, () -> ApplicationBundle.main(new String[0]));
            for (var field : List.of("version", "url", "mediaType", "sha256", "size", "logicalPath", "source")) {
                var json = inventory();
                if ("version".equals(field)) {
                    json.addProperty("version", 100);
                } else {
                    var asset = json.getAsJsonArray("assets").get(0).getAsJsonObject();
                    if ("size".equals(field)) {
                        asset.addProperty(field, 1);
                    } else {
                        asset.addProperty(field, "source".equals(field) ? "" : "wrong");
                    }
                }
                assertInvalidInventory(directory, json);
            }
            var json = inventory();
            json.getAsJsonArray("pruned").add("../escape");
            assertInvalidInventory(directory, json);
            json = inventory();
            json.getAsJsonArray("pruned").add(json.getAsJsonArray("pruned").get(0));
            assertInvalidInventory(directory, json);
            json = inventory();
            json.getAsJsonArray("pruned")
                    .add(json.getAsJsonArray("assets").get(0).getAsJsonObject().get("logicalPath"));
            assertInvalidInventory(directory, json);
            json = inventory();
            json.getAsJsonArray("assets")
                    .add(json.getAsJsonArray("assets").get(0).deepCopy());
            assertInvalidInventory(directory, json);
            for (var name : List.of(
                    "index.html",
                    inventory()
                            .getAsJsonArray("assets")
                            .get(0)
                            .getAsJsonObject()
                            .get("url")
                            .getAsString())) {
                var run = Files.createTempDirectory(directory, "collision");
                var args = options(run);
                args.addAll(List.of(
                        "--prepared", prepare(run, name, input("manifest")).toString()));
                expectThrows(IllegalArgumentException.class, () -> ApplicationBundle.run(args));
            }
            var empty = Files.createDirectory(directory.resolve("empty-assets"));
            var mismatch = Files.createDirectory(directory.resolve("mismatch"));
            expectThrows(
                    IOException.class,
                    () -> ApplicationBundle.run(change(options(mismatch), "--assets-directory", empty)));
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
                    () -> ApplicationBundle.run(change(options(extraRun), "--assets-directory", extra)));
        } finally {
            AssetContentTest.delete(directory);
        }
    }

    private static void assertInvalidInventory(Path directory, JsonObject json) throws Exception {
        var run = Files.createTempDirectory(directory, "invalid");
        var manifest = run.resolve("bad.json");
        Files.writeString(manifest, json.toString());
        expectThrows(
                IllegalArgumentException.class,
                () -> ApplicationBundle.run(change(options(run), "--assets-manifest", manifest)));
    }
}
