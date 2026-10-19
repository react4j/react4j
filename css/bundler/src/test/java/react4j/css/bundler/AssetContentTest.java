package react4j.css.bundler;

import static org.testng.Assert.*;

import com.google.gson.JsonParser;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import org.jspecify.annotations.NullMarked;
import org.testng.annotations.Test;

@NullMarked
public final class AssetContentTest {
    static Path brotli() {
        return Path.of(Objects.requireNonNull(System.getProperty("brotli"))).toAbsolutePath();
    }

    static void compress(Path input, Path output) throws Exception {
        var process = new ProcessBuilder(
                        brotli().toString(), "-f", "-q", "11", "-o", output.toString(), input.toString())
                .redirectErrorStream(true)
                .start();
        var log = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        assertEquals(process.waitFor(), 0, log);
    }

    static void delete(Path directory) throws Exception {
        try (var paths = Files.walk(directory)) {
            for (var file : paths.sorted(Comparator.reverseOrder()).toList()) {
                Files.delete(file);
            }
        }
    }

    @Test(timeOut = 60000)
    public void pinnedBrotliRoundTripsAndOnlyUsefulRepresentationsAreRetained() throws Exception {
        var directory = Files.createTempDirectory("prepared-asset");
        try {
            var source = directory.resolve("icon.svg");
            Files.writeString(source, "<svg><path d='M0 0L10 10'/></svg>".repeat(1000));
            var br = directory.resolve("icon.svg.br");
            compress(source, br);
            var content = AssetContent.prepare("demo/icon.svg", source, br);
            assertEquals(content.compressed(), br);
            assertEquals(content.compressedSize(), Files.size(br));
            var metadata = directory.resolve("metadata.json");
            AssetContent.main(new String[] {"demo/icon.svg", source.toString(), br.toString(), metadata.toString()});
            assertEquals(AssetContent.read(metadata), content);
            var decoded = directory.resolve("decoded.svg");
            var process = new ProcessBuilder(brotli().toString(), "-d", "-f", "-o", decoded.toString(), br.toString())
                    .start();
            assertEquals(process.waitFor(), 0);
            assertEquals(Files.mismatch(decoded, source), -1L);
            var output = directory.resolve("web");
            content.copy(output, content.url());
            content.copy(output, content.url());
            assertEquals(Files.mismatch(source, output.resolve(content.url())), -1L);
            assertEquals(Files.mismatch(br, output.resolve(content.url() + ".br")), -1L);
            var entry = content.entry(content.url(), List.of("demo.Media#icon"), List.of("java:demo.Media#icon"));
            assertFalse(entry.has("version"));
            assertFalse(entry.has("brotli"));
            assertEquals(entry.getAsJsonObject("representations").get("br").getAsString(), content.url() + ".br");
            assertEquals(entry.get("brotliSize").getAsLong(), Files.size(br));
            var random = new byte[1000];
            new Random(42).nextBytes(random);
            Files.write(source, random);
            compress(source, br);
            var incompressible = AssetContent.prepare("demo/photo.png", source, br);
            assertNull(incompressible.compressed());
            assertEquals(incompressible.compressedSize(), 0L);
            assertFalse(incompressible
                    .entry(incompressible.url(), List.of(), List.of())
                    .has("brotliSize"));
            expectThrows(IllegalArgumentException.class, () -> content.copy(output, content.url()));
            Files.write(source, new byte[0]);
            assertEquals(AssetContent.prepare("empty", source, null).size(), 0L);
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
            var br = directory.resolve("source.br");
            Files.writeString(br, "small");
            var content = AssetContent.prepare("demo/photo.JPEG", source, br);
            assertEquals(
                    content.url(),
                    AssetContent.prepare("demo/photo.jpg", source, br).url());
            var metadata = directory.resolve("content.json");
            for (var key : List.of("logicalPath", "source", "sha256", "size", "version", "brotli", "brotliSize")) {
                var json = content.json();
                json.remove(key);
                // Brotli metadata may be entirely omitted, but either remaining half is invalid.
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
                expectThrows(IllegalArgumentException.class, () -> AssetContent.prepare(value, source, null));
            }
            expectThrows(
                    IllegalArgumentException.class,
                    () -> new AssetContent("a", Path.of(""), "1".repeat(64), 1, null, 0));
            expectThrows(IllegalArgumentException.class, () -> new AssetContent("a", source, "bad", 1, null, 0));
            expectThrows(
                    IllegalArgumentException.class, () -> new AssetContent("a", source, "1".repeat(64), -1, null, 0));
            expectThrows(
                    IllegalArgumentException.class, () -> new AssetContent("a", source, "1".repeat(64), 1, null, 1));
            for (long size : List.of(0L, -1L, 2L)) {
                expectThrows(
                        IllegalArgumentException.class,
                        () -> new AssetContent("a", source, "1".repeat(64), 2, br, size));
            }
            expectThrows(
                    IllegalArgumentException.class,
                    () -> new AssetContent("a", source, "1".repeat(64), 2, Path.of(""), 1));
            expectThrows(IllegalArgumentException.class, () -> AssetContent.main(new String[0]));
            var output = Files.createDirectory(directory.resolve("output"));
            Files.writeString(output.resolve("icon.jpg"), "conflicting");
            expectThrows(IllegalArgumentException.class, () -> content.copy(output, "icon.jpg"));
            expectThrows(IllegalArgumentException.class, () -> content.copy(output, "../escape"));
            Files.writeString(br, "changed-size");
            expectThrows(IllegalArgumentException.class, () -> content.copy(output, "another.jpg"));
        } finally {
            delete(directory);
        }
    }
}
