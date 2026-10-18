package react4j.css.processor;

import static org.testng.Assert.*;
import static react4j.css.processor.CssProcessorTest.applicationClasspath;
import static react4j.css.processor.CssProcessorTest.delete;
import static react4j.css.processor.CssProcessorTest.processorJar;
import static react4j.css.processor.CssProcessorTest.source;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.jar.JarFile;
import org.jspecify.annotations.NullMarked;
import org.testng.annotations.Test;

@NullMarked
public final class CssProcessorArtifactTest {
    @Test
    public void bundledClassesUseProcessorNamespace() throws Exception {
        try (var jar = new JarFile(processorJar().toFile())) {
            var unexpectedClasses = jar.stream()
                    .map(entry -> entry.getName())
                    .filter(name -> name.endsWith(".class") && !name.startsWith("react4j/css/processor/"))
                    .toList();
            assertTrue(unexpectedClasses.isEmpty(), unexpectedClasses.toString());
        }
    }

    @Test
    public void formattingCanBeDisabledWithoutJdkExports() throws Exception {
        Path directory = Files.createTempDirectory("css-format-no-exports-");
        try {
            Path input = directory.resolve("Button.java");
            Files.writeString(input, source("demo", "    color: red;", ""));
            var command = List.of(
                    Path.of(System.getProperty("java.home"), "bin", "javac").toString(),
                    "--release",
                    "17",
                    "-classpath",
                    applicationClasspath(),
                    "--processor-path",
                    processorJar().toString(),
                    "-d",
                    directory.resolve("classes").toString(),
                    "-s",
                    directory.resolve("generated").toString(),
                    "-Areact4j.css.format_generated_source=false",
                    input.toString());
            var process = new ProcessBuilder(command).redirectErrorStream(true).start();
            var output = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            int exitCode = process.waitFor();
            assertEquals(exitCode, 0, output);
            assertFalse(output.contains("warning:"), output);
            assertTrue(Files.exists(directory.resolve("classes/demo/Styled_Button.class")));
            assertTrue(Files.exists(directory.resolve("classes/demo/Button.css.map")));
        } finally {
            delete(directory);
        }
    }
}
