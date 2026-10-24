package react4j.css.integration;

import static org.testng.Assert.*;

import com.google.debugging.sourcemap.SourceMapConsumerV3;
import com.google.gson.JsonParser;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.jar.JarFile;
import org.testng.annotations.Test;

public final class CssInputIntegrationTest {
    private static Path input(String name) {
        return Path.of(Objects.requireNonNull(System.getProperty("css.input." + name)));
    }

    @Test
    public void actualHeaderJarChecksValidAndUnusedInvalidListsUsingExplicitCatalogue() throws Exception {
        try (var jar = new JarFile(input("headers").toFile())) {
            assertNotNull(jar.getEntry("react4j/css/integration/UtilityLibrary.class"));
            assertTrue(jar.stream().noneMatch(entry -> entry.getName().contains(".css")));
        }
        assertFalse(System.getProperty("java.class.path").contains("libutility_dependency_java.jar"));
        var directory = Files.createTempDirectory("css-header-consumer-");
        try {
            for (var name : List.of("flex", "unused-typo")) {
                var source = directory.resolve("HeaderCheck.java");
                Files.writeString(
                        source,
                        "package react4j.css.integration;\nimport react4j.css.*;\n"
                                + "@UtilityStyles(UtilityLibrary.class) abstract class HeaderCheck {"
                                + "@UtilityCss(\"" + name + "\") abstract String style(); }");
                var command = new ArrayList<>(List.of(
                        Path.of(System.getProperty("java.home"), "bin", "javac").toString(),
                        "--release",
                        "21",
                        "-classpath",
                        input("headers")
                                + File.pathSeparator
                                + input("jar")
                                + File.pathSeparator
                                + System.getProperty("java.class.path"),
                        "--processor-path",
                        input("processor").toString(),
                        "-Areact4j.css.format_generated_source=false",
                        "-d",
                        directory.resolve(name).toString(),
                        source.toString()));
                var process =
                        new ProcessBuilder(command).redirectErrorStream(true).start();
                var output = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
                int status = process.waitFor();
                if ("flex".equals(name)) {
                    assertEquals(status, 0, output);
                    assertTrue(Files.isRegularFile(
                            directory.resolve(name + "/react4j/css/integration/Styled_HeaderCheck.class")));
                } else {
                    assertNotEquals(status, 0, output);
                    assertTrue(output.contains("Unknown managed utility class: unused-typo"), output);
                }
            }
        } finally {
            try (var files = Files.walk(directory)) {
                for (var file : files.sorted(Comparator.reverseOrder()).toList()) {
                    Files.delete(file);
                }
            }
        }
    }

    @Test
    public void fullJ2clResourcesMapUtilityAndGlobalRulesToAuthoredCssFiles() throws Exception {
        String root = "react4j/css/integration/";
        String source = Files.readString(input("source"));
        for (var owner : List.of("UtilityLibrary", "FileDefaults")) {
            var fragment = input("tree").resolve(root + owner + ".css");
            assertTrue(Files.isRegularFile(fragment));
            assertTrue(Files.isRegularFile(Path.of(fragment + ".json")));
            var map = JsonParser.parseString(Files.readString(Path.of(fragment + ".map")))
                    .getAsJsonObject();
            assertEquals(
                    map.getAsJsonArray("sources").get(0).getAsString(),
                    root + ("UtilityLibrary".equals(owner) ? "utilities.css" : "defaults.css"));
            if ("FileDefaults".equals(owner)) {
                assertTrue(
                        Files.readString(fragment).contains("--jcss-react4j-css-integration-UtilityLibrary--gap:11px"));
            }
            if ("UtilityLibrary".equals(owner)) {
                assertEquals(map.getAsJsonArray("sourcesContent").get(0).getAsString(), source);
                var consumer = new SourceMapConsumerV3();
                consumer.parse(map.toString());
                String css = Files.readString(fragment);
                int at = css.indexOf("display:flex");
                assertTrue(at >= 0, css);
                int line = (int) css.substring(0, at)
                                .chars()
                                .filter(c -> '\n' == c)
                                .count()
                        + 1;
                int column = at - css.lastIndexOf('\n', at - 1);
                var location = Objects.requireNonNull(consumer.getMappingForLine(line, column));
                assertEquals(location.getLineNumber(), 2);
                assertEquals(
                        location.getColumnPosition(),
                        source.indexOf("display:flex") - source.lastIndexOf('\n', source.indexOf("display:flex")));
            }
        }
    }
}
