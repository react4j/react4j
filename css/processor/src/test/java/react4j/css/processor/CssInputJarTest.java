package react4j.css.processor;

import static org.testng.Assert.*;

import java.nio.file.Files;
import java.util.Comparator;
import java.util.List;
import java.util.zip.ZipFile;
import org.testng.annotations.Test;

public final class CssInputJarTest {
    @Test
    public void packagesExplicitInputsReproduciblyAndRejectsDuplicateOrInvalidRequests() throws Exception {
        var directory = Files.createTempDirectory("css-input-jar-");
        try {
            var source = directory.resolve("source.css");
            Files.writeString(source, ".flex {display:flex;}");
            var first = directory.resolve("first.jar");
            var second = directory.resolve("second.jar");
            CssInputJar.main(new String[] {first.toString(), "demo/theme-1/utilities.css", source.toString()});
            CssInputJar.main(new String[] {second.toString(), "demo/theme-1/utilities.css", source.toString()});
            assertEquals(Files.readAllBytes(first), Files.readAllBytes(second));
            try (var jar = new ZipFile(first.toFile())) {
                assertEquals(jar.size(), 2);
            }
            for (var args : List.of(
                    new String[0],
                    new String[] {"out"},
                    new String[] {"out", "name"},
                    new String[] {first.toString(), "../bad.css", source.toString()},
                    new String[] {first.toString(), "demo/a.css", source.toString(), "demo/a.css", source.toString()
                    })) {
                expectThrows(IllegalArgumentException.class, () -> CssInputJar.main(args));
            }
            Files.writeString(source, ".flex { color red; }");
            var invalid = expectThrows(
                    IllegalArgumentException.class,
                    () -> CssInputJar.main(new String[] {first.toString(), "demo/bad.css", source.toString()}));
            assertTrue(String.valueOf(invalid.getMessage()).contains("demo/bad.css:1:"), invalid.getMessage());
        } finally {
            try (var files = Files.walk(directory)) {
                for (var file : files.sorted(Comparator.reverseOrder()).toList()) {
                    Files.delete(file);
                }
            }
        }
    }
}
