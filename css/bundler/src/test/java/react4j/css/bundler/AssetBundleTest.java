package react4j.css.bundler;

import static org.testng.Assert.*;

import com.google.debugging.sourcemap.SourceMapConsumerV3;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helger.css.reader.CSSReader;
import com.helger.css.reader.CSSReaderSettings;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.testng.annotations.Test;
import react4j.css.internal.AssetResource;
import react4j.css.internal.CssLink;
import react4j.css.internal.SourceMap;

public final class AssetBundleTest {
    private static Path input(boolean optimized, String kind) {
        return Path.of(Objects.requireNonNull(
                System.getProperty("asset." + (optimized ? "optimized" : "development") + "." + kind)));
    }

    private static List<String> args(Path directory, boolean optimized) throws IOException {
        return args(directory, optimized, input(optimized, "catalogue"));
    }

    private static List<String> args(Path directory, boolean optimized, Path catalogue) throws IOException {
        var options = new ArrayList<>(List.of(
                "--directory",
                directory.resolve("web").toString(),
                "--manifest",
                directory.resolve("manifest.json").toString(),
                "--output-js",
                directory.resolve("web/app.js").toString(),
                "--output-map",
                directory.resolve("web/app.js.map").toString(),
                "--javascript",
                input(optimized, "javascript").toString(),
                "--map",
                input(optimized, "map").toString(),
                "--link-config",
                input(optimized, "config").toString(),
                "--liveness",
                input(optimized, "report").toString(),
                "--link",
                "//css/integration-tests/src/test/js/react4j/css/integration:asset_"
                        + (optimized ? "optimized" : "development"),
                "--input",
                input(optimized, "tree").toString(),
                "--asset-catalogue",
                catalogue.toString()));
        int index = 0;
        for (var entry : AssetResource.catalogue(List.of(catalogue)).entrySet()) {
            var metadata = directory.resolve("prepared" + index++ + ".json");
            Files.writeString(
                    metadata,
                    AssetContent.prepare(entry.getKey(), Path.of(entry.getValue()), null)
                            .json()
                            .toString());
            options.addAll(List.of("--prepared", metadata.toString()));
        }
        return options;
    }

    private static List<String> change(List<String> args, String key, Path value) {
        var changed = new ArrayList<>(args);
        changed.set(changed.indexOf(key) + 1, value.toString());
        return changed;
    }

    @Test
    public void realClosureLinksProduceOnlyRequiredAssetsAndPreserveJavaOrigins() throws Exception {
        for (boolean optimized : List.of(false, true)) {
            var directory = Files.createTempDirectory("asset-bundle");
            try {
                AssetBundle.run(args(directory, optimized));
                var inventory = JsonParser.parseString(Files.readString(directory.resolve("manifest.json")))
                        .getAsJsonObject();
                var assets = inventory.getAsJsonArray("assets");
                assertEquals(assets.size(), optimized ? 1 : 2);
                assertEquals(inventory.getAsJsonArray("pruned").size(), optimized ? 1 : 0);
                var first = assets.get(0).getAsJsonObject();
                String url = first.get("url").getAsString();
                assertTrue(url.matches("assets/[A-Za-z0-9_-]{43}\\.svg"), url);
                assertEquals(
                        first.get("sha256").getAsString(),
                        CssLink.digest(directory.resolve("web").resolve(url)));
                assertEquals(first.get("mediaType").getAsString(), "image/svg+xml");
                assertEquals(
                        first.getAsJsonArray("accessors").get(0).getAsString(),
                        "react4j.css.integration.AssetFixtures$Media#icon");
                assertEquals(
                        first.getAsJsonArray("retainedBy").get(0).getAsString(),
                        "java:react4j.css.integration.AssetFixtures$Media#icon");
                assertEquals(
                        first.getAsJsonObject("representations").get("identity").getAsString(), url);
                String js = Files.readString(directory.resolve("web/app.js"));
                assertTrue(
                        js.contains("globalThis.document.currentScript.src"),
                        js.substring(0, Math.min(200, js.length())));
                assertFalse(js.contains("react4j-asset:"));
                // This authored string shares the asset's spelling but is not a generated accessor.
                assertEquals(js.split("asset:react4j/css/integration/asset.svg", -1).length - 1, 1);
                assertFalse(js.contains("asset:react4j/css/integration/unused.svg"));
                var map = JsonParser.parseString(Files.readString(directory.resolve("web/app.js.map")))
                        .getAsJsonObject();
                var consumer = new SourceMapConsumerV3();
                consumer.parse(map.toString());
                int index = js.indexOf(url);
                int line = (int) js.substring(0, index)
                                .chars()
                                .filter(c -> '\n' == c)
                                .count()
                        + 1;
                int column = index - js.lastIndexOf('\n', index - 1);
                var origin = Objects.requireNonNull(consumer.getMappingForLine(line, column));
                assertTrue(origin.getOriginalFile().endsWith("AssetFixtures_Assets_Media.java"), origin.toString());
                assertTrue(hasCompleteJava(
                        map,
                        Files.readString(input(optimized, "tree")
                                .resolve("react4j/css/integration/AssetFixtures_Assets_Media.java"))));
                var repeat = directory.resolve("again");
                Files.createDirectory(repeat);
                AssetBundle.run(args(repeat, optimized));
                assertEquals(Files.readString(repeat.resolve("manifest.json")), inventory + "\n");
                assertEquals(Files.readString(repeat.resolve("web/app.js")), js);
                assertEquals(Files.readString(repeat.resolve("web/app.js.map")), map + "\n");
                expectThrows(IllegalArgumentException.class, () -> AssetBundle.run(args(directory, optimized)));
            } finally {
                delete(directory);
            }
        }
    }

    private static boolean hasCompleteJava(JsonObject map, String expected) {
        if (map.has("sections")) {
            return map.getAsJsonArray("sections").asList().stream()
                    .anyMatch(
                            section -> hasCompleteJava(section.getAsJsonObject().getAsJsonObject("map"), expected));
        }
        for (int i = 0; i < map.getAsJsonArray("sources").size(); i++) {
            if (map.getAsJsonArray("sources").get(i).getAsString().endsWith("AssetFixtures_Assets_Media.java")) {
                return map.getAsJsonArray("sourcesContent").get(i).getAsString().equals(expected);
            }
        }
        return false;
    }

    @Test
    public void cssVariantsRetainOnlySurvivingFilesAndFamiliesAndKeepStylesheetRelativeUrls() throws Exception {
        for (boolean optimized : List.of(false, true)) {
            var directory = Files.createTempDirectory("asset-css");
            try {
                var style = new JsonObject();
                style.addProperty("input", input(optimized, "css").toString());
                style.addProperty("map", input(optimized, "cssmap").toString());
                style.addProperty("output", directory.resolve("final.css").toString());
                style.addProperty(
                        "outputMap", directory.resolve("final.css.map").toString());
                style.addProperty("name", "styles/app.css");
                var options = args(directory, optimized);
                options.addAll(List.of("--style", style.toString()));
                AssetBundle.run(options);
                String css = Files.readString(directory.resolve("final.css"));
                var sheet = Objects.requireNonNull(CSSReader.readFromStringReader(css, new CSSReaderSettings()));
                assertEquals(
                        sheet.getAllLayerRules().stream()
                                .mapToInt(layer -> layer.getFontFaceRuleCount())
                                .sum(),
                        optimized ? 2 : 3);
                assertFalse(css.contains("asset:"));
                assertTrue(css.contains("../assets/"), css);
                assertTrue(css.contains("?variant=1#shape"), css);
                var inventory = JsonParser.parseString(Files.readString(directory.resolve("manifest.json")))
                        .getAsJsonObject();
                assertEquals(inventory.getAsJsonArray("assets").size(), optimized ? 1 : 2);
                assertTrue(inventory.toString().contains("css:styles/app.css"));
                var map = JsonParser.parseString(Files.readString(directory.resolve("final.css.map")))
                        .getAsJsonObject();
                var consumer = new SourceMapConsumerV3();
                consumer.parse(map.toString());
                int index = css.indexOf("../assets/");
                int line = (int) css.substring(0, index)
                                .chars()
                                .filter(c -> '\n' == c)
                                .count()
                        + 1;
                int column = index - css.lastIndexOf('\n', index - 1);
                assertTrue(Objects.requireNonNull(consumer.getMappingForLine(line, column))
                        .getOriginalFile()
                        .endsWith("CssAssetStyles.java"));
                assertTrue(map.toString().contains("class CssAssetStyles"));
                if (optimized) {
                    var alternate = style.deepCopy();
                    alternate.addProperty("input", input(true, "alternate").toString());
                    alternate.addProperty("map", input(true, "alternatemap").toString());
                    alternate.addProperty(
                            "output", directory.resolve("alternate.css").toString());
                    alternate.addProperty(
                            "outputMap", directory.resolve("alternate.css.map").toString());
                    alternate.addProperty("name", "alternate.css");
                    var union = directory.resolve("union");
                    Files.createDirectory(union);
                    var combined = args(union, true);
                    combined.addAll(List.of("--style", style.toString(), "--style", alternate.toString()));
                    AssetBundle.run(combined);
                    var manifest = JsonParser.parseString(Files.readString(union.resolve("manifest.json")))
                            .getAsJsonObject();
                    assertEquals(manifest.getAsJsonArray("assets").size(), 2);
                    assertTrue(manifest.getAsJsonArray("pruned").isEmpty());
                    assertTrue(manifest.toString().contains("css:alternate.css"));
                    var cssOnly = manifest.getAsJsonArray("assets").asList().stream()
                            .map(value -> value.getAsJsonObject())
                            .filter(value ->
                                    value.get("logicalPath").getAsString().endsWith("unused.svg"))
                            .findFirst()
                            .orElseThrow();
                    assertEquals(
                            cssOnly.getAsJsonArray("accessors").get(0).getAsString(),
                            "react4j.css.integration.AssetFixtures$Media#unused");
                    assertEquals(cssOnly.getAsJsonArray("retainedBy").size(), 1);
                    assertEquals(cssOnly.getAsJsonArray("retainedBy").get(0).getAsString(), "css:alternate.css");
                    assertFalse(
                            Files.readString(directory.resolve("alternate.css")).contains("../assets/"));
                }
            } finally {
                delete(directory);
            }
        }
    }

    @Test
    public void identicalCompatibleFilesDeduplicateAndUsefulMediaTypesAreDeterministic() throws Exception {
        var directory = Files.createTempDirectory("asset-dedup");
        try {
            var first = directory.resolve("one.svg");
            var original = AssetResource.catalogue(List.of(input(false, "catalogue")));
            Files.copy(Path.of(original.values().iterator().next()), first);
            var second = directory.resolve("two.svg");
            Files.copy(first, second);
            var files = new JsonObject();
            files.addProperty("react4j/css/integration/asset.svg", first.toString());
            files.addProperty("react4j/css/integration/unused.svg", second.toString());
            var manifest = directory.resolve("files.json");
            Files.writeString(manifest, files.toString());
            AssetBundle.run(args(directory, false, manifest));
            var inventory = JsonParser.parseString(Files.readString(directory.resolve("manifest.json")))
                    .getAsJsonObject();
            assertEquals(inventory.getAsJsonArray("assets").size(), 2);
            assertEquals(
                    inventory.getAsJsonArray("assets").get(0).getAsJsonObject().get("url"),
                    inventory.getAsJsonArray("assets").get(1).getAsJsonObject().get("url"));
            try (var paths = Files.list(directory.resolve("web/assets"))) {
                assertEquals(paths.count(), 1L);
            }
        } finally {
            delete(directory);
        }
        assertEquals(AssetContent.media("photo.JPEG"), new AssetContent.Media("jpg", "image/jpeg"));
        assertEquals(AssetContent.media("data.map"), new AssetContent.Media("json", "application/json"));
        for (var extension : List.of(
                "svg", "png", "jpg", "gif", "webp", "avif", "ico", "woff", "woff2", "ttf", "otf", "css", "js", "json",
                "txt", "xml", "html", "wav", "mp3", "ogg", "wasm", "pdf")) {
            assertEquals(AssetContent.media("file." + extension).extension(), extension);
            assertFalse(AssetContent.media("file." + extension).type().isBlank());
        }
        assertEquals(AssetContent.media("unknown"), new AssetContent.Media("bin", "application/octet-stream"));
    }

    @Test
    public void staleMissingAndMalformedEvidenceCannotProduceAnEmptyAssetSet() throws Exception {
        var directory = Files.createTempDirectory("asset-evidence");
        try {
            for (var invalid : List.of(
                    List.<String>of(),
                    List.of("--input"),
                    List.of("--unknown", "x"),
                    List.of("--link", "a", "--link", "b"))) {
                expectThrows(IllegalArgumentException.class, () -> AssetBundle.run(invalid));
            }
            var config = directory.resolve("config.json");
            Files.writeString(config, Files.readString(input(true, "config")).replace("\"link\":", "\"oldLink\":"));
            expectThrows(
                    IllegalArgumentException.class,
                    () -> AssetBundle.run(change(args(directory, true), "--link-config", config)));
            var wrongLink = args(directory, true);
            wrongLink.set(wrongLink.indexOf("--link") + 1, "//:other");
            expectThrows(IllegalArgumentException.class, () -> AssetBundle.run(wrongLink));
            var report = directory.resolve("report.json");
            Files.writeString(report, "{}");
            expectThrows(
                    IllegalArgumentException.class,
                    () -> AssetBundle.run(change(args(directory, true), "--liveness", report)));
            var catalogue = directory.resolve("files.json");
            Files.writeString(catalogue, "{}");
            expectThrows(
                    IllegalArgumentException.class,
                    () -> AssetBundle.run(change(args(directory, true), "--asset-catalogue", catalogue)));
            var map = directory.resolve("empty.map");
            Files.writeString(map, "");
            expectThrows(
                    IllegalArgumentException.class, () -> AssetBundle.run(change(args(directory, true), "--map", map)));
        } finally {
            delete(directory);
        }
    }

    @Test
    public void bootstrapPreservesStrictDirectivesAndAvoidsEveryExistingIdentifier() throws Exception {
        var link = CssLink.read(Files.readString(input(true, "config")));
        var asset = link.assets().get(0);
        String code = "'use strict';\nvar $r4jAssetBase='authored';window.result=\"" + asset.token() + "\";";
        var map = JsonParser.parseString(new SourceMap.Writer()
                        .append(code, new SourceMap.Location("Binding.java", "complete java", 1, 0))
                        .map("app.js"))
                .getAsJsonObject();
        var result = AssetJavaScript.rewrite(code, map, link, Map.of(asset.token(), "assets/icon.svg"));
        assertTrue(result.text().indexOf("'use strict'") < result.text().indexOf("var $r4jAssetBase_"), result.text());
        assertTrue(result.text().contains("($r4jAssetBase_+\"assets/icon.svg\")"), result.text());
        assertTrue(result.text().contains("var $r4jAssetBase='authored'"), result.text());
        var empty = JsonParser.parseString(new SourceMap.Writer().map("app.js")).getAsJsonObject();
        expectThrows(IllegalArgumentException.class, () -> AssetJavaScript.rewrite("var x=;", empty, link, Map.of()));
        expectThrows(
                IllegalArgumentException.class,
                () -> AssetJavaScript.rewrite("window.result='react4j-asset:unknown';", empty, link, Map.of()));
        expectThrows(
                IllegalArgumentException.class,
                () -> AssetJavaScript.rewrite(
                        "window.result='ordinary';", empty, link, Map.of(asset.token(), "assets/icon.svg")));
        expectThrows(
                IllegalArgumentException.class,
                () -> AssetJavaScript.rewrite("window.result=1;", new JsonObject(), link, Map.of()));
    }

    @Test
    public void bootstrapStaysInsideAnImmediatelyInvokedOutputWrapper() throws Exception {
        var link = CssLink.read(Files.readString(input(true, "config")));
        var asset = link.assets().get(0);
        for (String wrapper : List.of(
                "(function(){%s})();",
                "(function(){%s}).call(globalThis);",
                "!function(){%s}();",
                "void function(){%s}();",
                "(function(){%s}).apply(globalThis,[]);",
                "(()=>{%s})();")) {
            String code = wrapper.formatted("'use strict';window.result=\"" + asset.token() + "\";");
            var map = JsonParser.parseString(new SourceMap.Writer()
                            .append(code, new SourceMap.Location("Binding.java", "complete java", 1, 0))
                            .map("app.js"))
                    .getAsJsonObject();
            var result = AssetJavaScript.rewrite(code, map, link, Map.of(asset.token(), "assets/icon.svg"));
            assertTrue(result.text().startsWith(code.substring(0, code.indexOf("window.result"))), result.text());
            assertTrue(
                    result.text().indexOf("'use strict'") < result.text().indexOf("var $r4jAssetBase"), result.text());
            assertTrue(
                    result.text().indexOf("var $r4jAssetBase") < result.text().indexOf("window.result"), result.text());
        }
        for (String code : List.of(
                "(function(value){window.result=value;})(\"" + asset.token() + "\");",
                "window.getAsset=function(){return \"" + asset.token() + "\";};",
                "(function(){window.result=\"" + asset.token() + "\";})();window.other=1;")) {
            var map =
                    JsonParser.parseString(new SourceMap.Writer().map("app.js")).getAsJsonObject();
            var result = AssetJavaScript.rewrite(code, map, link, Map.of(asset.token(), "assets/icon.svg"));
            assertTrue(result.text().startsWith("var $r4jAssetBase="), result.text());
        }
    }

    @Test
    public void developmentMapsRejectMissingAndDuplicatedBindingInputs() throws Exception {
        var directory = Files.createTempDirectory("asset-development-map");
        try {
            var link = CssLink.read(Files.readString(input(false, "config")));
            String code = Files.readString(input(false, "javascript"));
            var empty = directory.resolve("empty.map");
            Files.writeString(empty, "");
            expectThrows(
                    IllegalArgumentException.class,
                    () -> AssetJavaScript.mapping(code, empty, link, List.of(directory)));
            var name = link.assets().get(0).source();
            var duplicate = directory.resolve(name);
            Files.createDirectories(Objects.requireNonNull(duplicate.getParent()));
            Files.copy(input(false, "tree").resolve(name), duplicate);
            expectThrows(
                    IllegalArgumentException.class,
                    () -> AssetJavaScript.mapping(code, empty, link, List.of(directory, input(false, "tree"))));
            String original = Files.readString(duplicate);
            expectThrows(
                    IllegalArgumentException.class,
                    () -> AssetJavaScript.mapping(code + original, empty, link, List.of(input(false, "tree"))));
            assertTrue(AssetJavaScript.mapping("", empty, link, List.of(input(false, "tree")))
                    .getAsJsonArray("sections")
                    .isEmpty());
        } finally {
            delete(directory);
        }
    }

    private static void delete(Path directory) throws Exception {
        try (var paths = Files.walk(directory)) {
            for (var path : paths.sorted(Comparator.reverseOrder()).toList()) {
                Files.delete(path);
            }
        }
    }
}
