package react4j.css.internal;

import static org.testng.Assert.*;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.nio.file.Files;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.testng.annotations.Test;

public final class AssetLinkTest {
    private static AssetResource resource(String path) {
        return new AssetResource("demo.Media", "demo.Assets_Media", List.of(new AssetResource.Binding("icon", path)));
    }

    @Test
    public void assetIdentityIsSeparateFromCssNamesAndBoundToReports() {
        for (boolean optimized : List.of(false, true)) {
            var resource = resource("demo/icon.svg");
            var catalogue = new CssCatalogue(Map.of(), "1".repeat(64), Map.of(resource.owner(), resource));
            var link = CssLink.create(catalogue, "//:app", optimized);
            assertTrue(link.values().isEmpty());
            assertTrue(link.names().entries().isEmpty());
            var asset = link.assets().get(0);
            assertEquals(asset.source(), "demo/Assets_Media.impl.java.js");
            assertEquals(asset.id(), "demo.Media#icon");
            assertEquals(asset.token(), "react4j-asset:demo.Media#icon");
            assertEquals(CssLink.read(link.json()), link);
            var live = Set.of(asset.id());
            var report = link.report(live, "2".repeat(64));
            assertEquals(link.readReport(report, "2".repeat(64)), live);
            for (var field : List.of("owner", "implementation", "method", "path")) {
                var changed = JsonParser.parseString(report).getAsJsonObject();
                changed.getAsJsonArray("assets").get(0).getAsJsonObject().addProperty(field, "changed");
                expectThrows(IllegalArgumentException.class, () -> link.readReport(changed.toString(), "2".repeat(64)));
                var config = JsonParser.parseString(link.json()).getAsJsonObject();
                config.getAsJsonArray("assets").get(0).getAsJsonObject().remove(field);
                expectThrows(IllegalArgumentException.class, () -> CssLink.read(config.toString()));
            }
            var changed = JsonParser.parseString(link.json()).getAsJsonObject();
            changed.remove("assets");
            expectThrows(IllegalArgumentException.class, () -> CssLink.read(changed.toString()));
            expectThrows(
                    IllegalArgumentException.class,
                    () -> new CssLink(
                            link.link(), link.digest(), optimized, List.of(), link.names(), List.of(asset, asset)));
            expectThrows(
                    UnsupportedOperationException.class,
                    () -> catalogue.assets().clear());
            expectThrows(
                    UnsupportedOperationException.class, () -> link.assets().clear());
            expectThrows(
                    IllegalArgumentException.class,
                    () -> new CssCatalogue(Map.of(), "1".repeat(64), Map.of("different.Owner", resource)));
        }
    }

    @Test
    public void catalogueChecksDeadDeclarationsAndDigestExcludesFileBytes() throws Exception {
        var directory = Files.createTempDirectory("asset-link");
        try {
            var tree = Files.createDirectories(directory.resolve("tree/demo"));
            var metadata = tree.resolve("Media.assets.json");
            Files.writeString(metadata, resource("demo/icon.svg").json());
            var image = directory.resolve("icon.svg");
            Files.writeString(image, "<svg xmlns=\"http://www.w3.org/2000/svg\"/>");
            var manifest = directory.resolve("files.json");
            var files = new JsonObject();
            files.addProperty("demo/icon.svg", image.toString());
            Files.writeString(manifest, files.toString());
            var output = directory.resolve("config.json");
            String[] args = {
                "--link",
                "//:app",
                "--mode",
                "optimized",
                "--output",
                output.toString(),
                "--input",
                Objects.requireNonNull(tree.getParent()).toString(),
                "--asset-catalogue",
                manifest.toString()
            };
            CssLink.main(args);
            String original = Files.readString(output);
            Files.writeString(image, "<svg xmlns=\"http://www.w3.org/2000/svg\" viewBox=\"0 0 2 2\"/>");
            CssLink.main(args);
            assertEquals(Files.readString(output), original);
            Files.writeString(metadata, resource("demo/absent.svg").json());
            var changed = CssCatalogue.read(List.of(Objects.requireNonNull(tree.getParent())));
            assertNotEquals(changed.digest(), CssLink.read(original).digest());
            expectThrows(IllegalArgumentException.class, () -> CssLink.main(args));
            var duplicate = Files.createDirectories(directory.resolve("duplicate/demo"));
            Files.copy(metadata, duplicate.resolve("Media.assets.json"));
            expectThrows(
                    IllegalArgumentException.class,
                    () -> CssCatalogue.read(List.of(
                            Objects.requireNonNull(tree.getParent()), Objects.requireNonNull(duplicate.getParent()))));
        } finally {
            try (var paths = Files.walk(directory)) {
                for (var path : paths.sorted(Comparator.reverseOrder()).toList()) {
                    Files.delete(path);
                }
            }
        }
    }
}
