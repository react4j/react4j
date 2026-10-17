package react4j.css.processor;

import static org.testng.Assert.*;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import javax.annotation.processing.Processor;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;
import org.jspecify.annotations.NullMarked;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import react4j.css.Css;
import react4j.testing.StrictGeneratedCode;

@NullMarked
public final class CssProcessorTest {
    private record Result(boolean success, String diagnostics, Map<String, String> files, Map<String, String> values) {}

    private static Path processorJar() {
        return Path.of(Objects.requireNonNull(System.getProperty("css.processor.jar")));
    }

    @Test
    public void artifactIsStandaloneAndRelocated() throws Exception {
        try (var jar = new JarFile(processorJar().toFile())) {
            var names = jar.stream().map(entry -> entry.getName()).toList();
            assertTrue(names.contains("react4j/css/processor/vendor/helger/css/CSSSourceLocation.class"));
            assertTrue(names.contains("react4j/css/processor/vendor/slf4j/Logger.class"));
            assertFalse(
                    names.stream()
                            .anyMatch(name -> name.endsWith(".class") && !name.startsWith("react4j/css/processor/")),
                    names.toString());
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
        }
    }

    private static String applicationClasspath() throws Exception {
        return Path.of(Css.class
                        .getProtectionDomain()
                        .getCodeSource()
                        .getLocation()
                        .toURI())
                + File.pathSeparator
                + Path.of(NullMarked.class
                        .getProtectionDomain()
                        .getCodeSource()
                        .getLocation()
                        .toURI());
    }

    private static Path fixtureDir() {
        return Path.of(System.getProperty("react4j.fixture_dir", "css/processor/src/test/fixtures"));
    }

    @DataProvider(name = "fixtures")
    public Object[][] fixtures() throws Exception {
        var cases = new ArrayList<Object[]>();
        for (var family : List.of("input", "bad_input")) {
            try (var directories = Files.list(fixtureDir().resolve(family))) {
                for (var directory :
                        directories.filter(Files::isDirectory).sorted().toList()) {
                    cases.add(new Object[] {family, directory.getFileName().toString()});
                }
            }
        }
        assertFalse(cases.isEmpty(), "No CSS fixtures found");
        var names = cases.stream().map(value -> (String) value[1]).collect(Collectors.toCollection(TreeSet::new));
        assertEquals(names.size(), cases.size(), "Fixture names must be unique across families");
        if (!Boolean.getBoolean("react4j.output_fixture_data")) {
            try (var expected = Files.list(fixtureDir().resolve("expected"))) {
                assertEquals(
                        expected.filter(Files::isDirectory)
                                .map(path -> path.getFileName().toString())
                                .collect(Collectors.toCollection(TreeSet::new)),
                        names,
                        "Complete fixture case set differs");
            }
        }
        return cases.toArray(Object[][]::new);
    }

    @Test(dataProvider = "fixtures")
    public void goldenFixtures(String family, String name) throws Exception {
        Path input = fixtureDir().resolve(family).resolve(name);
        var sources = new TreeMap<String, String>();
        try (var files = Files.walk(input)) {
            for (var file : files.filter(path -> path.toString().endsWith(".java"))
                    .sorted()
                    .toList()) {
                sources.put(input.relativize(file).toString(), Files.readString(file));
            }
        }
        assertFalse(sources.isEmpty(), name);
        var result = compile(sources);
        assertEquals(result.success(), "input".equals(family), name + ": " + result.diagnostics());
        if (result.success()) {
            assertEquals(result.diagnostics(), "", name);
        }
        var actual = new TreeMap<String, String>();
        for (var file : result.files().entrySet()) {
            if (!file.getKey().endsWith(".class")) {
                actual.put(
                        file.getKey(), new String(Base64.getDecoder().decode(file.getValue()), StandardCharsets.UTF_8));
            }
        }
        actual.put("diagnostics.txt", result.diagnostics());
        Path expected = fixtureDir().resolve("expected").resolve(name);
        if (Boolean.getBoolean("react4j.output_fixture_data")) {
            if (Files.exists(expected)) {
                delete(expected);
            }
            for (var entry : actual.entrySet()) {
                Path target = expected.resolve(entry.getKey());
                Files.createDirectories(target.getParent());
                Files.writeString(target, entry.getValue());
            }
        } else {
            var maintained = new TreeMap<String, String>();
            try (var files = Files.walk(expected)) {
                for (var file : files.filter(Files::isRegularFile).sorted().toList()) {
                    maintained.put(expected.relativize(file).toString(), Files.readString(file));
                }
            }
            assertEquals(actual.keySet(), maintained.keySet(), name + ": Complete generated output set differs");
            for (var entry : actual.entrySet()) {
                assertEquals(entry.getValue(), maintained.get(entry.getKey()), name + "/" + entry.getKey());
            }
        }
    }

    @Test
    public void strictCheckerRejectsInvalidGeneratedBody() throws Exception {
        var compiled = compile(Map.of("Button.java", source("demo", "    color: red;", "")));
        assertTrue(compiled.success(), compiled.diagnostics());
        Path directory = Files.createTempDirectory("css-checker-control-");
        try {
            for (var entry : compiled.files().entrySet()) {
                Path path = directory.resolve(entry.getKey());
                Files.createDirectories(path.getParent());
                Files.write(path, Base64.getDecoder().decode(entry.getValue()));
            }
            Path source = directory.resolve("generated/demo/Styled_Button.java");
            String text = Files.readString(source);
            int end = text.lastIndexOf('}');
            Files.writeString(
                    source,
                    text.substring(0, end) + "static java.lang.String invalidBody() { return null; }\n"
                            + text.substring(end));
            var failure = expectThrows(
                    AssertionError.class,
                    () -> StrictGeneratedCode.check(
                            directory.resolve("generated"), directory.resolve("classes"), applicationClasspath()));
            String message = failure.getMessage();
            assertNotNull(message);
            assert null != message;
            assertTrue(message.contains("[NullAway] returning @Nullable expression"), message);
        } finally {
            delete(directory);
        }
    }

    private static void delete(Path directory) throws Exception {
        try (var files = Files.walk(directory)) {
            for (var file : files.sorted(Comparator.reverseOrder()).toList()) {
                Files.delete(file);
            }
        }
    }

    private static String source(String packageName, String css, String methods) {
        return "package " + packageName + ";\nimport react4j.css.Css;\n@Css(\"\"\"\n"
                + css + "\n    \"\"\")\nabstract class Button {\n"
                + "static Button of() { return new Styled_Button(); }\n"
                + "abstract String style();\n" + methods + "\n}\n";
    }

    private static Result compile(Map<String, String> sources, String... inspect) throws Exception {
        Path directory = Files.createTempDirectory("css-test-");
        try {
            Path generated = Files.createDirectory(directory.resolve("generated"));
            Path classes = Files.createDirectory(directory.resolve("classes"));
            var paths = new ArrayList<Path>();
            for (var entry : sources.entrySet()) {
                Path path = directory.resolve(entry.getKey());
                Files.createDirectories(path.getParent());
                Files.writeString(path, entry.getValue());
                paths.add(path);
            }
            var compiler = ToolProvider.getSystemJavaCompiler();
            var diagnostics = new DiagnosticCollector<JavaFileObject>();
            boolean success;
            try (var manager = compiler.getStandardFileManager(diagnostics, null, null)) {
                var task = compiler.getTask(
                        null,
                        manager,
                        diagnostics,
                        List.of(
                                "--release",
                                "17",
                                "-classpath",
                                applicationClasspath(),
                                "--processor-path",
                                processorJar().toString(),
                                "-d",
                                classes.toString(),
                                "-s",
                                generated.toString()),
                        null,
                        manager.getJavaFileObjectsFromPaths(paths));
                success = task.call();
            }
            if (success) {
                StrictGeneratedCode.check(generated, classes, applicationClasspath());
            }
            var files = new LinkedHashMap<String, String>();
            for (var root : List.of(generated, classes)) {
                try (var stream = Files.walk(root)) {
                    for (var file : stream.filter(Files::isRegularFile).sorted().toList()) {
                        files.put(
                                root.getFileName() + "/" + root.relativize(file),
                                Base64.getEncoder().encodeToString(Files.readAllBytes(file)));
                    }
                }
            }
            var values = new LinkedHashMap<String, String>();
            if (success) {
                try (var loader = new URLClassLoader(
                        new URL[] {classes.toUri().toURL()}, CssProcessorTest.class.getClassLoader())) {
                    for (var name : inspect) {
                        var type = loader.loadClass(name);
                        var factory = type.getDeclaredMethod("of");
                        factory.setAccessible(true);
                        var instance = factory.invoke(null);
                        for (var method : instance.getClass().getDeclaredMethods()) {
                            method.setAccessible(true);
                            values.put(name + "." + method.getName(), (String) method.invoke(instance));
                        }
                    }
                }
            }
            String messages = diagnostics.getDiagnostics().stream()
                    .map(diagnostic -> {
                        var source = diagnostic.getSource();
                        String location = null == source
                                ? ""
                                : source.getName().replace(directory + "/", "") + ":" + diagnostic.getLineNumber() + ":"
                                        + diagnostic.getColumnNumber() + ": ";
                        return diagnostic.getKind() + " " + location
                                + diagnostic.getMessage(Locale.ROOT).replace(directory + "/", "") + "\n";
                    })
                    .collect(Collectors.joining());
            return new Result(success, messages, files, values);
        } finally {
            delete(directory);
        }
    }

    @Test
    public void processorDependenciesAreNotApplicationDependencies() throws Exception {
        String text = source("demo", "    color: red;", "")
                .replace(
                        "abstract String style();",
                        "abstract String style(); com.helger.css.CSSSourceLocation parser;");
        var result = compile(Map.of("Button.java", text));
        assertFalse(result.success());
        assertTrue(result.diagnostics().contains("package com.helger.css does not exist"), result.diagnostics());
    }

    @Test
    public void bindingsAndIndependentCompilationsAreReproducible() throws Exception {
        String a =
                source("com.biz_ui", "    color: red;\n    &.success { color: green; }", "abstract String success();");
        String b = source("com_biz.ui", "    color: blue;", "");
        var first = compile(Map.of("a/Button.java", a), "com.biz_ui.Button");
        assertTrue(first.success(), first.diagnostics());
        assertEquals(first.values().get("com.biz_ui.Button.style"), "jcss-com-biz_005f_ui-Button");
        assertEquals(
                first.values().get("com.biz_ui.Button.success"),
                "jcss-com-biz_005f_ui-Button jcss-com-biz_005f_ui-Button--success");
        var second = compile(Map.of("b/Button.java", b), "com_biz.ui.Button");
        assertTrue(second.success(), second.diagnostics());
        assertNotEquals(
                first.values().get("com.biz_ui.Button.style"), second.values().get("com_biz.ui.Button.style"));
        var combined = new LinkedHashMap<String, String>();
        combined.put("a/Button.java", a);
        combined.put("b/Button.java", b);
        var together = compile(combined);
        assertTrue(together.success(), together.diagnostics());
        var expected = new LinkedHashMap<>(first.files());
        expected.putAll(second.files());
        assertEquals(together.files(), expected);
        var reverse = new LinkedHashMap<String, String>();
        reverse.put("b/Button.java", b);
        reverse.put("a/Button.java", a);
        assertEquals(compile(reverse).files(), together.files());
        assertEquals(compile(combined).files(), together.files());
    }

    @Test
    public void parserRejectsMismatchedAndUnownedSelectorsBeforeGeneration() throws Exception {
        for (var css : List.of(
                "&.sucess { color: red; }",
                "/* &.success {} */ color: red;",
                "content: \"&.success\";",
                "& .success { color: red; }",
                "color red;")) {
            var result = compile(Map.of("Button.java", source("demo", "    " + css, "abstract String success();")));
            assertFalse(result.success(), css);
            assertTrue(result.files().isEmpty(), result.files().toString());
            assertTrue(result.diagnostics().contains("Button.java:"), result.diagnostics());
        }
    }

    @Test
    public void diagnosticsFollowJavaEscapesAndContinuedLines() throws Exception {
        String text = source(
                "demo",
                "    color: red;\\\n    background-color: white;\n    &.sucess { color: blue; }",
                "abstract String success();");
        var result = compile(Map.of("Button.java", text));
        assertFalse(result.success());
        assertTrue(result.diagnostics().contains("Button.java:6:6: Local selector .sucess"), result.diagnostics());
    }

    @Test
    public void invalidJavaContractsFailClearly() throws Exception {
        for (var method : List.of(
                "abstract int success();", "abstract String success(int value);", "abstract <T> String success();")) {
            var result = compile(Map.of("Button.java", source("demo", "    &.success { color: red; }", method)));
            assertFalse(result.success());
            assertTrue(
                    result.diagnostics().contains("CSS accessor success must take no arguments and return String"),
                    result.diagnostics());
        }
        var expression = compile(Map.of(
                "Button.java",
                source("demo", "    color: red;", "")
                        .replace("@Css(\"\"\"\n    color: red;\n    \"\"\")", "@Css(\"color: \" + \"red;\")")));
        assertFalse(expression.success());
        assertTrue(expression.diagnostics().contains("literal Java text block"), expression.diagnostics());
    }

    @Test
    public void nestedTypesUseTheirBinaryOwnerIdentity() throws Exception {
        String nested = "package demo;\nimport react4j.css.Css;\nclass Outer {\n"
                + "@Css(\"\"\"\ncolor: red;\n\"\"\")\nabstract static class Button {\n"
                + "static Button of() { return new Styled_Outer_0024_Button(); }\nabstract String style();\n}\n}\n";
        var result = compile(Map.of("Outer.java", nested), "demo.Outer$Button");
        assertTrue(result.success(), result.diagnostics());
        assertEquals(result.values().get("demo.Outer$Button.style"), "jcss-demo-Outer_0024_Button");
    }

    @Test
    public void bindingsDoNotShadowJavaTypes() throws Exception {
        for (var name : List.of("String", "Override")) {
            String text = source("demo", "    color: red;", "")
                    .replace("class Button", "class " + name)
                    .replace("static Button of()", "static " + name + " of()")
                    .replace("Styled_Button", "Styled_" + name)
                    .replace("abstract String style()", "abstract java.lang.String style()");
            var result = compile(Map.of(name + ".java", text), "demo." + name);
            assertTrue(result.success(), result.diagnostics());
            assertEquals(result.values().get("demo." + name + ".style"), "jcss-demo-" + name);
        }
    }

    @Test
    public void stringAccessorsRetainTheirNominalTypeWithTypeAnnotations() throws Exception {
        String text = source("demo", "    color: red;", "")
                .replace("abstract String style()", "abstract @org.jspecify.annotations.NonNull String style()");
        var result = compile(Map.of("Button.java", text), "demo.Button");
        assertTrue(result.success(), result.diagnostics());
        assertEquals(result.values().get("demo.Button.style"), "jcss-demo-Button");
    }
}
