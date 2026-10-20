package react4j.css.bundler;

import static org.testng.Assert.*;

import com.google.gson.JsonParser;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import org.jspecify.annotations.NullMarked;
import org.testng.annotations.Test;
import react4j.css.internal.CssLink;

@NullMarked
public final class AssetContentTest {
    static void delete(Path directory) throws Exception {
        try (var paths = Files.walk(directory)) {
            for (var file : paths.sorted(Comparator.reverseOrder()).toList()) {
                Files.delete(file);
            }
        }
    }

    @Test
    public void preparationPreservesContentIdentityAndCopiesOnlyCanonicalBytes() throws Exception {
        var directory = Files.createTempDirectory("prepared-asset");
        try {
            var source = directory.resolve("icon.svg");
            Files.writeString(source, "<svg><path d='M0 0L10 10'/></svg>");
            var content = AssetContent.prepare("demo/icon.svg", source);
            assertEquals(content.sha256(), CssLink.digest(source));
            assertEquals(content.size(), Files.size(source));
            var metadata = directory.resolve("metadata.json");
            AssetContent.main(new String[] {"demo/icon.svg", source.toString(), metadata.toString()});
            assertEquals(AssetContent.read(metadata), content);
            var output = directory.resolve("web");
            content.copy(output, content.url());
            content.copy(output, content.url());
            assertEquals(Files.mismatch(source, output.resolve(content.url())), -1L);
            try (var files = Files.walk(output)) {
                assertEquals(files.filter(Files::isRegularFile).count(), 1L);
            }
            var entry = content.entry(content.url(), List.of("demo.Media#icon"), List.of("java:demo.Media#icon"));
            assertEquals(
                    entry.keySet(),
                    Set.of("logicalPath", "source", "sha256", "size", "url", "mediaType", "accessors", "retainedBy"));
            assertEquals(AssetContent.managed(entry), content);
            Files.writeString(source, "changed bytes");
            expectThrows(IllegalArgumentException.class, () -> content.copy(output, content.url()));
            Files.write(source, new byte[0]);
            assertEquals(AssetContent.prepare("empty", source).size(), 0L);
        } finally {
            delete(directory);
        }
    }

    @Test
    public void metadataAndCopyConflictsFailRatherThanMisnameBytes() throws Exception {
        var directory = Files.createTempDirectory("asset-content-errors");
        try {
            var source = directory.resolve("source");
            Files.writeString(source, "content".repeat(100));
            var content = AssetContent.prepare("demo/photo.JPEG", source);
            assertEquals(
                    content.url(),
                    AssetContent.prepare("demo/photo.jpg", source).url());
            var metadata = directory.resolve("content.json");
            for (var key : List.of("logicalPath", "source", "sha256", "size", "version")) {
                var json = content.json();
                json.remove(key);
                Files.writeString(metadata, json.toString());
                expectThrows(IllegalArgumentException.class, () -> AssetContent.read(metadata));
                for (var value : List.of("null", "{}", "true", "-1", "1.5", "99999999999999999999999")) {
                    json = content.json();
                    json.add(key, JsonParser.parseString(value));
                    Files.writeString(metadata, json.toString());
                    expectThrows(IllegalArgumentException.class, () -> AssetContent.read(metadata));
                }
            }
            for (var value : List.of("../escape", "", "/absolute", "a?x")) {
                expectThrows(IllegalArgumentException.class, () -> AssetContent.prepare(value, source));
            }
            expectThrows(IllegalArgumentException.class, () -> new AssetContent("a", Path.of(""), "1".repeat(64), 1));
            expectThrows(IllegalArgumentException.class, () -> new AssetContent("a", source, "bad", 1));
            expectThrows(IllegalArgumentException.class, () -> new AssetContent("a", source, "1".repeat(64), -1));
            expectThrows(IllegalArgumentException.class, () -> AssetContent.main(new String[0]));
            var output = Files.createDirectory(directory.resolve("output"));
            Files.writeString(output.resolve("icon.jpg"), "conflicting");
            expectThrows(IllegalArgumentException.class, () -> content.copy(output, "icon.jpg"));
            expectThrows(IllegalArgumentException.class, () -> content.copy(output, "../escape"));
            // A same-length mutation must also fail the digest check.
            Files.writeString(source, "CONTENT".repeat(100));
            expectThrows(IllegalArgumentException.class, () -> content.copy(output, "another.jpg"));
        } finally {
            delete(directory);
        }
    }
}
