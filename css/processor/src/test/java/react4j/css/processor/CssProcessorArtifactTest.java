package react4j.css.processor;

import static org.testng.Assert.*;
import static react4j.css.processor.CssProcessorTest.applicationClasspath;
import static react4j.css.processor.CssProcessorTest.delete;
import static react4j.css.processor.CssProcessorTest.processorJar;
import static react4j.css.processor.CssProcessorTest.source;

import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarFile;
import javax.annotation.processing.Processor;
import org.jspecify.annotations.NullMarked;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

@NullMarked
public final class CssProcessorArtifactTest {
    @Test
    public void artifactIsStandaloneAndRelocated() throws Exception {
        try (var jar = new JarFile(processorJar().toFile())) {
            var names = jar.stream().map(entry -> entry.getName()).toList();
            assertTrue(names.contains("react4j/css/processor/vendor/helger/css/CSSSourceLocation.class"));
            assertTrue(names.contains("react4j/css/processor/vendor/slf4j/Logger.class"));
            assertTrue(names.contains("react4j/css/processor/vendor/javapoet/TypeSpec.class"));
            assertTrue(names.contains("react4j/css/processor/vendor/proton/AbstractStandardProcessor.class"));
            assertTrue(names.contains("react4j/css/processor/vendor/proton/vendor/javaformat/java/Formatter.class"));
            assertFalse(
                    names.stream()
                            .anyMatch(name -> name.endsWith(".class") && !name.startsWith("react4j/css/processor/")),
                    names.toString());
            for (var name : names) {
                if (name.startsWith("META-INF/services/")) {
                    try (var stream = jar.getInputStream(jar.getJarEntry(name))) {
                        for (var provider : new String(stream.readAllBytes(), StandardCharsets.UTF_8)
                                .lines()
                                .toList()) {
                            String type = provider.split("#", 2)[0].trim();
                            assertTrue(
                                    type.isEmpty() || names.contains(type.replace('.', '/') + ".class"),
                                    name + ": " + type);
                        }
                    }
                }
            }
            var service = jar.getJarEntry("META-INF/services/javax.annotation.processing.Processor");
            assertNotNull(service);
            assert null != service;
            try (var stream = jar.getInputStream(service)) {
                assertEquals(
                        new String(stream.readAllBytes(), StandardCharsets.UTF_8),
                        "react4j.css.processor.CssProcessor\n");
            }
        }
        try (var loader =
                new URLClassLoader(new URL[] {processorJar().toUri().toURL()}, ClassLoader.getPlatformClassLoader())) {
            var processor = loader.loadClass("react4j.css.processor.CssProcessor");
            assertTrue(processor.getConstructor().newInstance() instanceof Processor);
            expectThrows(ClassNotFoundException.class, () -> loader.loadClass("react4j.css.Css"));
            expectThrows(ClassNotFoundException.class, () -> loader.loadClass("com.helger.css.CSSSourceLocation"));
            expectThrows(ClassNotFoundException.class, () -> loader.loadClass("org.slf4j.Logger"));
            expectThrows(ClassNotFoundException.class, () -> loader.loadClass("com.palantir.javapoet.TypeSpec"));
            expectThrows(
                    ClassNotFoundException.class,
                    () -> loader.loadClass("org.realityforge.proton.AbstractStandardProcessor"));
            expectThrows(
                    ClassNotFoundException.class, () -> loader.loadClass("com.palantir.javaformat.java.Formatter"));
        }
    }

    @DataProvider(name = "formattingOptions")
    public Object[][] formattingOptions() {
        return new Object[][] {
            {List.of(), true},
            {List.of("-Areact4j.css.format_generated_source=true"), true},
            {List.of("-Areact4j.css.format_generated_source=false"), false}
        };
    }

    @Test(dataProvider = "formattingOptions")
    public void formattingRequiresJdkExportsOnlyWhenEnabled(List<String> options, boolean formatting) throws Exception {
        Path directory = Files.createTempDirectory("css-format-no-exports-");
        try {
            Path input = directory.resolve("Button.java");
            Files.writeString(input, source("demo", "    color: red;", ""));
            var command = new ArrayList<>(List.of(
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
                    directory.resolve("generated").toString()));
            command.addAll(options);
            command.add(input.toString());
            var process = new ProcessBuilder(command).redirectErrorStream(true).start();
            var output = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            int exitCode = process.waitFor();
            if (formatting) {
                assertNotEquals(exitCode, 0, output);
                assertTrue(output.contains("react4j.css.format_generated_source=true"), output);
                for (var export : List.of("api", "code", "file", "parser", "tree", "util")) {
                    assertTrue(
                            output.contains(
                                    "--add-exports=jdk.compiler/com.sun.tools.javac." + export + "=ALL-UNNAMED"),
                            output);
                }
            } else {
                assertEquals(exitCode, 0, output);
                assertFalse(output.contains("warning:"), output);
                assertTrue(Files.exists(directory.resolve("classes/demo/Styled_Button.class")));
                assertTrue(Files.exists(directory.resolve("classes/demo/Button.css.map")));
            }
        } finally {
            delete(directory);
        }
    }
}
