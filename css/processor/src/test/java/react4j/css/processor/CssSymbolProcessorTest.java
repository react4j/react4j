package react4j.css.processor;

import static org.testng.Assert.*;

import com.google.gson.JsonParser;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Base64;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import org.jspecify.annotations.NullMarked;
import org.testng.annotations.Test;

@NullMarked
public final class CssSymbolProcessorTest {
    private static final String IMPORTS = "package demo;\n"
            + "import react4j.css.Css;\n"
            + "import react4j.css.CssImport;\n"
            + "import react4j.css.CssCustomProperty;\n"
            + "import react4j.css.GlobalCss;\n"
            + "import react4j.css.Keyframes;\n"
            + "import org.jspecify.annotations.NullMarked;\n";

    private static String source(String kind, String annotations, String css, String members) {
        String body = "Css".equals(kind) ? css : "GlobalCss".equals(kind) ? ":root {" + css + "}" : "to {" + css + "}";
        return IMPORTS + "@NullMarked\n" + annotations + "\n@" + kind + "(\"\"\"\n" + body
                + "\n\"\"\")\nabstract class Button {\n"
                + "static Button of() { return Styled_Button.get(); }\n"
                + ("Css".equals(kind) ? "abstract String style();\n" : "") + members + "\n}";
    }

    private static String text(CssProcessorTest.Result result, String path) {
        return new String(Base64.getDecoder().decode(result.files().get(path)), StandardCharsets.UTF_8);
    }

    @Test
    public void bindingsOnAllKindsInferGetterNamesAndHonorExplicitNames() throws Exception {
        for (var kind : List.of("Css", "GlobalCss", "Keyframes")) {
            var result = CssProcessorTest.compile(
                    Map.of(
                            "Button.java",
                            source(
                                    kind,
                                    "",
                                    "--accent: red; color: var(--other-name); left: var(--uRL);",
                                    "@CssCustomProperty abstract String getAccent();"
                                            + " @CssCustomProperty(name=\"other-name\") public abstract String other();"
                                            + " @CssCustomProperty abstract String getURL();")),
                    "demo.Button");
            assertTrue(result.success(), result.diagnostics());
            assertEquals(result.values().get("demo.Button.getAccent"), "--jcss-demo-Button--accent");
            assertEquals(result.values().get("demo.Button.other"), "--jcss-demo-Button--other_002d_name");
            assertEquals(result.values().get("demo.Button.getURL"), "--jcss-demo-Button--uRL");
            assertTrue(JsonParser.parseString(text(result, "classes/demo/Button.css.json"))
                    .getAsJsonObject()
                    .get("bindings")
                    .getAsBoolean());
        }
    }

    @Test
    public void usedAliasesPreserveOriginalOwnershipAcrossKindsAndDeduplicateDependencies() throws Exception {
        for (var kind : List.of("Css", "GlobalCss", "Keyframes")) {
            var result = CssProcessorTest.compile(
                    Map.of(
                            "Defaults.java",
                                    source("GlobalCss", "", "--accent: blue; color: var(--focus);", "")
                                            .replace("abstract class Button", "abstract class Defaults")
                                            .replace("static Button of() { return Styled_Button.get(); }", ""),
                            "Button.java",
                                    source(
                                            kind,
                                            "@CssImport(value=Defaults.class,property=\"accent\",name=\"local\")\n"
                                                + "@CssImport(value=Defaults.class,property=\"focus\")\n"
                                                + "@CssImport(value=Defaults.class,property=\"accent\",name=\"unused\")",
                                            "--local: gold; outline-color: var(--focus);",
                                            "@CssCustomProperty abstract String local(); @CssCustomProperty abstract"
                                                    + " String focus();")),
                    "demo.Button");
            assertTrue(result.success(), result.diagnostics());
            assertEquals(result.values().get("demo.Button.local"), "--jcss-demo-Defaults--accent");
            assertEquals(result.values().get("demo.Button.focus"), "--jcss-demo-Defaults--focus");
            assertEquals(
                    JsonParser.parseString(text(result, "classes/demo/Button.css.json"))
                            .getAsJsonObject()
                            .getAsJsonArray("dependencies")
                            .toString(),
                    "[\"demo.Defaults\"]");
        }
    }

    @Test
    public void invalidAccessorsFailBeforeGeneratingJavaOrCss() throws Exception {
        for (var kind : List.of("Css", "GlobalCss", "Keyframes")) {
            for (var method : List.of(
                    "@CssCustomProperty abstract String missing();",
                    "@CssCustomProperty(name=\"\") abstract String accent();",
                    "@CssCustomProperty(name=\"--accent\") abstract String accent();",
                    "@CssCustomProperty(name=\"a b\") abstract String accent();",
                    "@CssCustomProperty String accent() { return \"x\"; }",
                    "@CssCustomProperty static String accent() { return \"x\"; }",
                    "@CssCustomProperty abstract int accent();",
                    "@CssCustomProperty abstract String accent(int x);",
                    "@CssCustomProperty abstract <T> String accent();",
                    "@CssCustomProperty abstract String accent() throws Exception;",
                    "@CssCustomProperty abstract String accent(); @CssCustomProperty abstract String getAccent();")) {
                var result =
                        CssProcessorTest.compile(Map.of("Button.java", source(kind, "", "--accent: red;", method)));
                assertFalse(result.success(), kind + ": " + method);
                assertFalse(result.files().containsKey("generated/demo/Styled_Button.java"), result.diagnostics());
                assertFalse(result.files().containsKey("classes/demo/Button.css"), result.diagnostics());
            }
        }
        var orphan = CssProcessorTest.compile(Map.of(
                "Button.java", IMPORTS + "abstract class Button { @CssCustomProperty abstract String accent(); }"));
        assertFalse(orphan.success());
        assertTrue(orphan.diagnostics().contains("Declare exactly one"), orphan.diagnostics());
    }

    @Test
    public void unusedImportsAreCheckedAndDoNotRetainProviders() throws Exception {
        String defaults = source("GlobalCss", "", "--accent: red;", "")
                .replace("abstract class Button", "abstract class Defaults")
                .replace("static Button of() { return Styled_Button.get(); }", "");
        for (var annotation : List.of(
                "@CssImport(value=Defaults.class,property=\"missing\")",
                "@CssImport(value=Defaults.class,property=\"--accent\")",
                "@CssImport(value=Defaults.class,property=\"accent\",name=\"--alias\")",
                "@CssImport(value=Defaults.class,property=\"accent\")"
                        + " @CssImport(value=Defaults.class,property=\"accent\")")) {
            var result = CssProcessorTest.compile(
                    Map.of("Defaults.java", defaults, "Button.java", source("Css", annotation, "color: red;", "")));
            assertFalse(result.success(), annotation);
            assertFalse(result.files().containsKey("generated/demo/Styled_Button.java"));
        }
        var result = CssProcessorTest.compile(Map.of(
                "Defaults.java",
                defaults,
                "Button.java",
                source("Css", "@CssImport(value=Defaults.class,property=\"accent\")", "color: red;", "")));
        assertTrue(result.success(), result.diagnostics());
        assertTrue(JsonParser.parseString(text(result, "classes/demo/Button.css.json"))
                .getAsJsonObject()
                .getAsJsonArray("dependencies")
                .isEmpty());
    }

    @Test
    public void transitiveAliasesResolveThroughHeaderJarsWithoutResourceSidecars() throws Exception {
        String defaults = source("GlobalCss", "", "--accent: blue;", "")
                .replace("abstract class Button", "abstract class Defaults")
                .replace("static Button of() { return Styled_Button.get(); }", "");
        String alias = source(
                        "GlobalCss",
                        "@CssImport(value=Defaults.class,property=\"accent\",name=\"alias\")",
                        "--alias: gold;",
                        "")
                .replace("abstract class Button", "abstract class Alias")
                .replace("static Button of() { return Styled_Button.get(); }", "");
        var providers = CssProcessorTest.compile(Map.of("Defaults.java", defaults, "Alias.java", alias));
        assertTrue(providers.success(), providers.diagnostics());
        var directory = Files.createTempDirectory("css-symbol-headers-");
        try {
            var header = directory.resolve("header.jar");
            try (var jar = new JarOutputStream(Files.newOutputStream(header))) {
                for (var entry : providers.files().entrySet()) {
                    if (entry.getKey().startsWith("classes/") && entry.getKey().endsWith(".class")) {
                        jar.putNextEntry(new JarEntry(entry.getKey().substring(8)));
                        jar.write(Base64.getDecoder().decode(entry.getValue()));
                        jar.closeEntry();
                    }
                }
            }
            var result = CssProcessorTest.compile(
                    Map.of(
                            "Button.java",
                            source(
                                    "Css",
                                    "@CssImport(value=Alias.class,property=\"alias\",name=\"local\")",
                                    "color: var(--local);",
                                    "@CssCustomProperty abstract String local();")),
                    List.of("-classpath", CssProcessorTest.applicationClasspath() + File.pathSeparator + header),
                    "demo.Button");
            assertTrue(result.success(), result.diagnostics());
            assertEquals(result.values().get("demo.Button.local"), "--jcss-demo-Defaults--accent");
            assertEquals(
                    JsonParser.parseString(text(result, "classes/demo/Button.css.json"))
                            .getAsJsonObject()
                            .getAsJsonArray("dependencies")
                            .toString(),
                    "[\"demo.Alias\"]");
        } finally {
            try (var paths = Files.walk(directory)) {
                for (var path : paths.sorted(Comparator.reverseOrder()).toList()) {
                    Files.delete(path);
                }
            }
        }
    }

    @Test
    public void aliasCyclesWithoutAnOwnerFailAndLocalVariableCyclesRemainCss() throws Exception {
        String a = source(
                "GlobalCss", "@CssImport(value=Other.class,property=\"accent\")", "--accent: var(--accent);", "");
        String b = a.replace("Other.class", "Button.class")
                .replace("abstract class Button", "abstract class Other")
                .replace("static Button of() { return Styled_Button.get(); }", "");
        var result = CssProcessorTest.compile(Map.of("Button.java", a, "Other.java", b));
        assertFalse(result.success());
        assertTrue(result.diagnostics().contains("Cyclic custom property import"), result.diagnostics());
        result =
                CssProcessorTest.compile(Map.of("Button.java", source("Css", "", "--a: var(--b); --b: var(--a);", "")));
        assertTrue(result.success(), result.diagnostics());
    }
}
