package react4j.css.processor;

import static org.testng.Assert.*;

import com.google.debugging.sourcemap.SourceMapConsumerV3;
import com.google.gson.JsonParser;
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
                                "-Xlint:deprecation,removal",
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
    public void animationDiagnosticsMapMultilineListsThroughJavaEscapesAndCrLf() throws Exception {
        String text = source(
                        "demo",
                        "    @keyframes pulse {}\n    animation: pu\\\n    lse 1s;\n"
                                + "    animation-name:\n      pulse,\n      typo;",
                        "")
                .replace("\n", "\r\n");
        var result = compile(Map.of("Button.java", text));
        assertFalse(result.success());
        assertTrue(
                result.diagnostics().contains("Button.java:9:7: Unknown local animation name: typo"),
                result.diagnostics());
        assertTrue(result.files().isEmpty(), result.files().keySet().toString());
    }

    @Test
    public void authorErrorsCannotBeSuppressedAndRemainLocalToTheirOwner() throws Exception {
        String peer = source("demo", "    @keyframes pulse {}", "").replace("Button", "Peer");
        for (var name : List.of("pulse", "jcss-demo-Peer--keyframes-pulse")) {
            String bad = source("demo", "    animation-name: " + name + ";", "")
                    .replace("@Css(", "@SuppressWarnings(\"all\") @Css(");
            var result = compile(Map.of("Button.java", bad, "Peer.java", peer));
            assertFalse(result.success());
            assertEquals(
                    result.diagnostics()
                            .lines()
                            .filter(line -> line.startsWith("ERROR "))
                            .count(),
                    1L,
                    result.diagnostics());
            assertFalse(result.diagnostics().contains("WARNING "), result.diagnostics());
            assertTrue(result.diagnostics().contains("Unknown local animation name: " + name), result.diagnostics());
            assertTrue(result.files().containsKey("classes/demo/Peer.css"));
            assertTrue(result.files().containsKey("classes/demo/Peer.css.map"));
            assertTrue(result.files().containsKey("generated/demo/Styled_Peer.java"));
            assertFalse(
                    result.files().keySet().stream().anyMatch(path -> path.contains("Button")),
                    result.files().keySet().toString());
        }
    }

    @Test
    public void sourceMapsFollowJavaEscapesContinuedLinesAndNestedRules() throws Exception {
        String text = source(
                        "demo",
                        "    color: r" + "\\" + "u0065d;\\\n    background-color: white;\n"
                                + "    @media (color) {\n      &.success { color: green; }\n    }",
                        "abstract String success();")
                .replace("\n", "\r\n");
        var result = compile(Map.of("Button.java", text));
        assertTrue(result.success(), result.diagnostics());
        var css = new String(
                Base64.getDecoder().decode(result.files().get("classes/demo/Button.css")), StandardCharsets.UTF_8);
        var map = new String(
                Base64.getDecoder().decode(result.files().get("classes/demo/Button.css.map")), StandardCharsets.UTF_8);
        var parsed = JsonParser.parseString(map).getAsJsonObject();
        assertEquals(parsed.getAsJsonArray("sourcesContent").get(0).getAsString(), text);
        var consumer = new SourceMapConsumerV3();
        consumer.parse(map);
        for (var pair : Map.of(
                        "background-color:white", "background-color: white",
                        "@media", "@media",
                        ".jcss-demo-Button.jcss-demo-Button--success", "&.success",
                        "color:green", "color: green")
                .entrySet()) {
            int generated = css.indexOf(pair.getKey());
            int original = text.indexOf(pair.getValue());
            assertTrue(generated >= 0, css);
            var location = Objects.requireNonNull(consumer.getMappingForLine(1, generated + 1));
            assertEquals(location.getOriginalFile(), "demo/Button.java");
            assertEquals(
                    location.getLineNumber(),
                    (int) text.substring(0, original)
                                    .chars()
                                    .filter(c -> '\n' == c)
                                    .count()
                            + 1);
            assertEquals(location.getColumnPosition(), original - text.lastIndexOf('\n', original - 1));
        }
    }

    @Test
    public void keyframeSourceMapsIncludeDefinitionsFramesAndRenamedReferences() throws Exception {
        String text = source("demo", """
            @keyframes pulse {
              from { opacity: 0; }
              to { opacity: 1; }
            }
            &:hover { animation: pulse 1s ease; }
            """, "");
        var result = compile(Map.of("Button.java", text));
        assertTrue(result.success(), result.diagnostics());
        var css = new String(
                Base64.getDecoder().decode(result.files().get("classes/demo/Button.css")), StandardCharsets.UTF_8);
        var map = new String(
                Base64.getDecoder().decode(result.files().get("classes/demo/Button.css.map")), StandardCharsets.UTF_8);
        var consumer = new SourceMapConsumerV3();
        consumer.parse(map);
        for (var pair : Map.of(
                        "@keyframes jcss-demo-Button--keyframes-pulse", "@keyframes pulse",
                        "from{", "from",
                        "to{", "to",
                        "opacity:0", "opacity: 0",
                        "animation:jcss-demo-Button--keyframes-pulse", "animation: pulse")
                .entrySet()) {
            int generated = css.indexOf(pair.getKey());
            int original = text.indexOf(pair.getValue());
            assertTrue(generated >= 0, css);
            var location = Objects.requireNonNull(consumer.getMappingForLine(1, generated + 1));
            assertEquals(location.getOriginalFile(), "demo/Button.java");
            assertEquals(
                    location.getLineNumber(),
                    (int) text.substring(0, original)
                                    .chars()
                                    .filter(c -> '\n' == c)
                                    .count()
                            + 1);
            assertEquals(location.getColumnPosition(), original - text.lastIndexOf('\n', original - 1));
        }
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

    @DataProvider
    public Object[][] invalidJavaContracts() {
        String base =
                source("demo", "    color: red;", "").replace("static Button of() { return new Styled_Button(); }", "");
        String owner =
                "@Css requires an accessible abstract, non-generic style class extending Object; nested classes must"
                        + " be static";
        String constructor = "@Css style classes require an accessible no-argument constructor without throws";
        String accessor =
                "CSS accessor style must take no arguments and return String without type parameters or throws";
        var cases = new ArrayList<Object[]>();
        for (var declaration : List.of(
                "class Button",
                "abstract class Button<T>",
                "abstract class Button implements Runnable",
                "interface Button",
                "@interface Button",
                "enum Button")) {
            String text = base.replace("abstract class Button", declaration);
            if ("enum Button".equals(declaration)) {
                text = text.replace("enum Button {", "enum Button {;");
            }
            cases.add(new Object[] {declaration, text, owner});
        }
        cases.add(new Object[] {
            "subclass",
            base.replace("abstract class Button", "abstract class Button extends Parent") + "class Parent {}",
            owner
        });
        cases.add(new Object[] {
            "record",
            base.replace("abstract class Button", "record Button()").replace("abstract String style();", ""),
            owner
        });
        cases.add(new Object[] {
            "sealed",
            base.replace("abstract class Button", "sealed abstract class Button permits Child")
                    + "final class Child extends Button { String style() { return \"\"; } }",
            "@Css style classes must not be sealed"
        });
        String nested = base.replace("@Css(", "class Outer { @Css(") + "}";
        cases.add(new Object[] {"nonstatic nested", nested, owner});
        cases.add(new Object[] {
            "private nested", nested.replace("abstract class Button", "private abstract static class Button"), owner
        });
        cases.add(new Object[] {
            "private enclosing",
            nested.replace("@Css(", "private static class Hidden { @Css(")
                            .replace("abstract class Button", "abstract static class Button")
                    + "}",
            "@Css style classes must be accessible from their package"
        });
        for (var declaration : List.of(
                "private Button() {}",
                "Button(int value) {}",
                "Button(String... values) {}",
                "Button() throws Exception {}",
                "Button() throws RuntimeException {}",
                "private Button() {} Button(int value) {}")) {
            cases.add(new Object[] {
                declaration,
                base.replace("abstract String style();", declaration + " abstract String style();"),
                constructor
            });
        }
        for (var method : List.of(
                "abstract int style();",
                "abstract String[] style();",
                "abstract Object style();",
                "abstract CharSequence style();",
                "abstract String style(int value);",
                "abstract String style(String... values);",
                "abstract <T> String style();",
                "abstract String style() throws Exception;",
                "abstract String style() throws RuntimeException;")) {
            cases.add(new Object[] {method, base.replace("abstract String style();", method), accessor});
        }
        for (var method : List.of("", "String style() { return \"\"; }", "static String style() { return \"\"; }")) {
            cases.add(new Object[] {
                "nonabstract root " + method,
                base.replace("abstract String style();", method),
                "@Css style classes require abstract String style()"
            });
        }
        return cases.toArray(Object[][]::new);
    }

    @Test(dataProvider = "invalidJavaContracts")
    public void invalidJavaContractsReportOneAuthorErrorWithoutGeneratingFiles(
            String label, String text, String message) throws Exception {
        var result = compile(Map.of("Button.java", text));
        assertFalse(result.success(), label);
        assertEquals(
                result.diagnostics()
                        .lines()
                        .filter(line -> line.startsWith("ERROR "))
                        .count(),
                1L,
                result.diagnostics());
        assertTrue(result.diagnostics().contains(message), label + ": " + result.diagnostics());
        assertFalse(result.diagnostics().contains("WARNING "), result.diagnostics());
        assertFalse(result.diagnostics().contains("generated/"), result.diagnostics());
        assertTrue(result.files().isEmpty(), label + ": " + result.files().keySet());
    }

    @DataProvider
    public Object[][] validJavaContracts() {
        var cases = new ArrayList<Object[]>();
        for (var constructor : List.of("", "public ", "protected ")) {
            for (var accessor : List.of("", "public ", "protected ")) {
                String text = source("demo", "    color: red;", "")
                        .replace(
                                "abstract String style();",
                                constructor + "Button() {} " + accessor + "abstract String style();");
                cases.add(new Object[] {constructor + "/" + accessor, text});
            }
        }
        String base = source("demo", "    color: red;", "");
        cases.add(new Object[] {
            "generic constructor", base.replace("abstract String style();", "<T> Button() {} abstract String style();")
        });
        cases.add(new Object[] {"public owner", base.replace("abstract class Button", "public abstract class Button")});
        cases.add(new Object[] {
            "constructor overload",
            base.replace(
                    "abstract String style();", "Button() {} private Button(int value) {} abstract String style();")
        });
        cases.add(new Object[] {
            "concrete helpers",
            base.replace(
                    "abstract String style();",
                    "abstract String style(); static String helper() { return \"\"; } final int size() { return 0; }")
        });
        cases.add(new Object[] {
            "nullable accessor",
            base.replace(
                            "import react4j.css.Css;",
                            "import react4j.css.Css;\nimport org.jspecify.annotations.Nullable;")
                    .replace("abstract String style();", "abstract @Nullable String style();")
        });
        for (var annotation : List.of("@Deprecated", "@Deprecated(forRemoval = true)")) {
            cases.add(new Object[] {annotation + " owner", base.replace("@Css(", annotation + " @Css(")});
            cases.add(new Object[] {
                annotation + " default package",
                base.replace("package demo;\n", "").replace("@Css(", annotation + " @Css(")
            });
            cases.add(new Object[] {
                annotation + " accessor",
                base.replace("abstract String style();", annotation + " abstract String style();")
            });
            cases.add(new Object[] {
                annotation + " constructor",
                base.replace("abstract String style();", annotation + " Button() {} abstract String style();")
            });
            cases.add(new Object[] {
                annotation + " enclosing",
                base.replace("@Css(", annotation + " class Outer { @Css(")
                                .replace("abstract class Button", "abstract static class Button")
                                .replace("Styled_Button", "Styled_Outer_0024_Button")
                        + "}"
            });
        }
        cases.add(new Object[] {
            "generic enclosing",
            base.replace("@Css(", "class Outer<T> { @Css(")
                            .replace("abstract class Button", "abstract static class Button")
                            .replace("Styled_Button", "Styled_Outer_0024_Button")
                    + "}"
        });
        cases.add(new Object[] {
            "protected enclosing",
            base.replace("@Css(", "class Outer { protected static class Middle { @Css(")
                            .replace("abstract class Button", "abstract static class Button")
                            .replace("Styled_Button", "Styled_Outer_0024_Middle_0024_Button")
                    + "}}"
        });
        return cases.toArray(Object[][]::new);
    }

    @Test(dataProvider = "validJavaContracts")
    public void validJavaContractsCompileWithoutDiagnostics(String label, String text) throws Exception {
        var result = compile(Map.of(text.contains("class Outer") ? "Outer.java" : "Button.java", text));
        assertTrue(result.success(), label + ": " + result.diagnostics());
        assertEquals(result.diagnostics(), "", label);
        assertFalse(result.files().isEmpty(), label);
    }

    @Test
    public void generatedSuppressionsPreserveDeprecationWarningsAndSuppressionInAuthoredClients() throws Exception {
        for (var annotation : List.of("@Deprecated", "@Deprecated(forRemoval = true)")) {
            for (var owner : List.of(false, true)) {
                String text = source("demo", "    color: red;", "");
                text = owner
                        ? text.replace("@Css(", annotation + " @Css(")
                        : text.replace("abstract String style();", annotation + " abstract String style();");
                String client = "package demo; class Client { String style() { return Button.of().style(); } }";
                var result = compile(Map.of("Button.java", text, "Client.java", client));
                assertTrue(result.success(), result.diagnostics());
                assertEquals(result.diagnostics().lines().count(), 1L, result.diagnostics());
                assertTrue(result.diagnostics().contains("WARNING Client.java:"), result.diagnostics());
                assertTrue(result.diagnostics().contains(owner ? "demo.Button" : "style()"), result.diagnostics());
                var suppressed = compile(Map.of(
                        "Button.java",
                        text,
                        "Client.java",
                        client.replace(
                                "class Client", "@SuppressWarnings({\"deprecation\", \"removal\"}) class Client")));
                assertTrue(suppressed.success(), suppressed.diagnostics());
                assertEquals(suppressed.diagnostics(), "");
            }
        }
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
