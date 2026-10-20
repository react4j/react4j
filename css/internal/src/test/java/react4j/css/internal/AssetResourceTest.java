package react4j.css.internal;

import static org.testng.Assert.*;

import com.google.gson.JsonParser;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.testng.annotations.Test;

public final class AssetResourceTest {
    private static AssetResource resource() {
        return new AssetResource(
                "demo.Media", "demo.Assets_Media", List.of(new AssetResource.Binding("icon", "demo/icon.svg")));
    }

    @Test
    public void pathsResolveAgainstPackagesIncludingNestedAndDefaultOwners() {
        assertEquals(AssetResource.path("com.biz.Outer$Media"), "com/biz/Outer$Media.assets.json");
        assertEquals(AssetResource.resolve("com.biz.Outer$Media", "card/icon.svg"), "com/biz/card/icon.svg");
        assertEquals(AssetResource.resolve("Media", "icon.svg"), "icon.svg");
        assertEquals(AssetResource.resolve("a.Media", "é/with spaces (2).svg"), "a/é/with spaces (2).svg");
        for (var path : List.of(
                "",
                "/a",
                "a/",
                "a//b",
                ".",
                "..",
                "a/../b",
                "a/./b",
                "a\\b",
                "a:b",
                "x?y",
                "x#y",
                "x%2fy",
                "a\nb",
                "a\tb",
                "a\u0000b")) {
            expectThrows(IllegalArgumentException.class, () -> AssetResource.validatePath(path));
            expectThrows(IllegalArgumentException.class, () -> AssetResource.resolve("demo.Media", path));
        }
        expectThrows(IllegalArgumentException.class, () -> AssetResource.path("/Media"));
        expectThrows(IllegalArgumentException.class, () -> AssetResource.resolve("/Media", "icon.svg"));
        for (var method : List.of("", "1icon", "a.b", "icon()")) {
            expectThrows(IllegalArgumentException.class, () -> new AssetResource.Binding(method, "icon.svg"));
        }
        assertEquals(resource().bindings().get(0).value(), "asset:demo/icon.svg");
    }

    @Test
    public void metadataIsVersionedValidatedAndImmutable() {
        var expected = resource();
        assertEquals(AssetResource.read(expected.json(), expected.owner()), expected);
        var bindings = new ArrayList<>(expected.bindings());
        var copied = new AssetResource(expected.owner(), expected.implementation(), bindings);
        bindings.clear();
        assertEquals(copied, expected);
        expectThrows(
                UnsupportedOperationException.class, () -> copied.bindings().clear());
        expectThrows(IllegalArgumentException.class, () -> new AssetResource("Owner", "Implementation", List.of()));
        expectThrows(
                IllegalArgumentException.class,
                () -> new AssetResource("Owner", "bad/Implementation", expected.bindings()));
        expectThrows(
                IllegalArgumentException.class,
                () -> new AssetResource(
                        "Owner",
                        "Implementation",
                        List.of(expected.bindings().get(0), expected.bindings().get(0))));
        expectThrows(IllegalArgumentException.class, () -> AssetResource.read(expected.json(), "Other"));
        for (var source : List.of("null", "[]", "{}", "{", expected.json().replace("\"version\":1", "\"version\":2"))) {
            expectThrows(IllegalArgumentException.class, () -> AssetResource.read(source, expected.owner()));
        }
        for (var key : List.of("version", "owner", "implementation", "bindings")) {
            var json = JsonParser.parseString(expected.json()).getAsJsonObject();
            json.remove(key);
            expectThrows(IllegalArgumentException.class, () -> AssetResource.read(json.toString(), expected.owner()));
        }
        for (var key : List.of("owner", "implementation")) {
            for (var value : List.of("null", "1", "true", "[]", "{}")) {
                var json = JsonParser.parseString(expected.json()).getAsJsonObject();
                json.add(key, JsonParser.parseString(value));
                expectThrows(
                        IllegalArgumentException.class, () -> AssetResource.read(json.toString(), expected.owner()));
            }
        }
        for (var key : List.of("method", "path")) {
            var json = JsonParser.parseString(expected.json()).getAsJsonObject();
            json.getAsJsonArray("bindings").get(0).getAsJsonObject().remove(key);
            expectThrows(IllegalArgumentException.class, () -> AssetResource.read(json.toString(), expected.owner()));
        }
    }

    @Test
    public void treeCollectionChecksIdentityPathsAndDuplicateOwners() throws Exception {
        var root = Files.createTempDirectory("asset-resource");
        try {
            var first = Files.createDirectories(root.resolve("one/demo"));
            Files.writeString(first.resolve("Media.assets.json"), resource().json());
            Files.writeString(first.resolve("ignore.css"), "not asset metadata");
            Files.createDirectories(first.resolve("directory.assets.json"));
            var found = AssetResource.readTrees(List.of(first.getParent(), first.getParent()));
            assertEquals(found, Map.of(resource().owner(), resource()));
            expectThrows(UnsupportedOperationException.class, () -> found.clear());
            var second = Files.createDirectories(root.resolve("two/demo"));
            Files.writeString(second.resolve("Media.assets.json"), resource().json());
            expectThrows(
                    IllegalArgumentException.class,
                    () -> AssetResource.readTrees(List.of(first.getParent(), second.getParent())));
            Files.writeString(second.resolve("Other.assets.json"), resource().json());
            expectThrows(IllegalArgumentException.class, () -> AssetResource.readTrees(List.of(second.getParent())));
        } finally {
            delete(root);
        }
    }

    @Test
    public void declaredCataloguesValidateEveryReferenceWithoutReadingBytes() throws Exception {
        var root = Files.createTempDirectory("asset-catalogue");
        try {
            var one = root.resolve("one.json");
            var two = root.resolve("two.json");
            Files.writeString(one, "{\"demo/icon.svg\":\"not-an-existing-input.svg\"}");
            Files.writeString(two, "{\"demo/icon.svg\":\"not-an-existing-input.svg\",\"unused.svg\":\"other\"}");
            var catalogue = AssetResource.catalogue(List.of(one, one, two));
            assertEquals(catalogue.size(), 2);
            expectThrows(UnsupportedOperationException.class, () -> catalogue.clear());
            AssetResource.validate(Map.of(resource().owner(), resource()), catalogue);
            var missing = expectThrows(
                    IllegalArgumentException.class,
                    () -> AssetResource.validate(Map.of(resource().owner(), resource()), Map.of()));
            assertTrue(Objects.requireNonNull(missing.getMessage()).contains("demo.Media#icon"));
            Files.writeString(two, "{\"demo/icon.svg\":\"different\"}");
            expectThrows(IllegalArgumentException.class, () -> AssetResource.catalogue(List.of(one, two)));
            for (var source : List.of(
                    "{\"/escape\":\"file\"}", "{\"icon.svg\":\" \"}", "{\"icon.svg\":null}", "{\"icon.svg\":1}")) {
                Files.writeString(two, source);
                expectThrows(IllegalArgumentException.class, () -> AssetResource.catalogue(List.of(two)));
            }
        } finally {
            delete(root);
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
