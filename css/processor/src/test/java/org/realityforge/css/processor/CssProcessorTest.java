package org.realityforge.css.processor;

import static org.testng.Assert.*;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;
import org.jspecify.annotations.NullMarked;
import org.realityforge.css.Css;
import org.testng.annotations.Test;

public final class CssProcessorTest {
    private record Result(boolean success, String diagnostics, Map<String, String> files, Map<String, String> values) {}

    private static String source(String packageName, String css, String methods) {
        return "package " + packageName + ";\nimport org.realityforge.css.Css;\n@Css(\"\"\"\n"
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
                                Path.of(Css.class
                                                .getProtectionDomain()
                                                .getCodeSource()
                                                .getLocation()
                                                .toURI())
                                        + File.pathSeparator
                                        + Path.of(NullMarked.class
                                                .getProtectionDomain()
                                                .getCodeSource()
                                                .getLocation()
                                                .toURI()),
                                "--processor-path",
                                System.getProperty("java.class.path"),
                                "-d",
                                classes.toString(),
                                "-s",
                                generated.toString()),
                        null,
                        manager.getJavaFileObjectsFromPaths(paths));
                success = task.call();
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
            return new Result(success, diagnostics.getDiagnostics().toString(), files, values);
        } finally {
            try (var files = Files.walk(directory)) {
                for (var file : files.sorted(Comparator.reverseOrder()).toList()) {
                    Files.delete(file);
                }
            }
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
    public void javaTextBlockMappingMatchesCompilerSemantics() {
        String token =
                "\"\"\"\r\n\t color: red;  \r\n\t content: \"\\141\\s\\\\a\";\\\r\n\t opacity: 0.5;\r\n\t \"\"\"";
        var result = TextBlock.decode(token, 10, "color: red;\ncontent: \"a \\a\";opacity: 0.5;\n");
        assertEquals(result.offsets()[result.value().indexOf("opacity")], 10 + token.indexOf("opacity"));
        String unicode = "\"\"\"\n  color: r" + "\\" + "u0065d;\n  \"\"\"";
        assertEquals(TextBlock.decode(unicode, 0, "color: red;\n").value(), "color: red;\n");
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
        String nested = "package demo;\nimport org.realityforge.css.Css;\nclass Outer {\n"
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
