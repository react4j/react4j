package react4j.css.processor;

import static org.testng.Assert.*;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Base64;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.jspecify.annotations.NullMarked;
import org.testng.annotations.Test;

@NullMarked
public final class CssStableProcessorTest {
    private static final String IMPORTS = "package demo;\n"
            + "import react4j.css.Css;\n"
            + "import react4j.css.GlobalCss;\n"
            + "import react4j.css.Keyframes;\n"
            + "import react4j.css.CssStable;\n"
            + "import react4j.css.CssId;\n"
            + "import react4j.css.CssAnimation;\n"
            + "import react4j.css.CssCustomProperty;\n"
            + "import react4j.css.CssImport;\n";

    private static String source(String kind, String annotations, String css, String declaration, String members) {
        return IMPORTS + annotations + "\n@" + kind + "(\"\"\"\n" + css + "\n\"\"\")\n" + declaration + " { " + members
                + " }\n";
    }

    private static String text(CssProcessorTest.Result result, String name) {
        assertTrue(result.files().containsKey(name), result.files().keySet().toString());
        return new String(Base64.getDecoder().decode(result.files().get(name)), StandardCharsets.UTF_8);
    }

    private static JsonObject symbol(CssProcessorTest.Result result, String kind, String name) {
        var metadata = JsonParser.parseString(text(result, "classes/demo/Button.css.json"))
                .getAsJsonObject();
        return metadata.getAsJsonArray("symbols").asList().stream()
                .map(item -> item.getAsJsonObject())
                .filter(item -> kind.equals(item.get("kind").getAsString())
                        && name.equals(item.get("name").getAsString()))
                .findFirst()
                .orElseThrow();
    }

    @Test
    public void equalReadableOccurrencesKeepCanonicalOwnersAndAliasProviderRequirements() throws Exception {
        String local = source(
                "Css",
                "",
                "&#control { --accent: red; color: var(--accent); }",
                "abstract class Local",
                "abstract String style(); @CssId abstract String control(); @CssCustomProperty abstract String"
                        + " accent();");
        String global = source(
                "GlobalCss",
                "@CssStable",
                "#jcss-demo-Local--id-control { --jcss-demo-Local--accent: blue; }",
                "abstract class PublicNames",
                "@CssId(name=\"jcss-demo-Local--id-control\") abstract String id();");
        String consumer = source(
                "Css",
                "@CssImport(value=Local.class,id=\"control\",name=\"local\")"
                    + " @CssImport(value=PublicNames.class,id=\"jcss-demo-Local--id-control\",name=\"foreign\")@CssImport(value=Local.class,property=\"accent\",name=\"local\")"
                    + " @CssImport(value=PublicNames.class,property=\"jcss-demo-Local--accent\",name=\"foreign\")",
                "&#local { color: var(--local); } &#foreign { color: var(--foreign); }",
                "abstract class Button",
                "abstract String style();");
        var result = CssProcessorTest.compile(
                Map.of("Local.java", local, "PublicNames.java", global, "Button.java", consumer));
        assertTrue(result.success(), result.diagnostics());
        var metadata = JsonParser.parseString(text(result, "classes/demo/Button.css.json"))
                .getAsJsonObject();
        var occurrences = metadata.getAsJsonArray("occurrences");
        assertNotNull(occurrences);
        assert null != occurrences;
        var owners = new HashSet<String>();
        for (var item : occurrences) {
            var occurrence = item.getAsJsonObject();
            var symbol = occurrence.getAsJsonObject("symbol");
            if ("CLASS".equals(symbol.get("kind").getAsString())) {
                continue;
            }
            owners.add(symbol.get("owner").getAsString());
            String value = text(result, "classes/demo/Button.css")
                    .substring(
                            occurrence.get("start").getAsInt(),
                            occurrence.get("end").getAsInt());
            assertEquals(
                    value,
                    "ID".equals(symbol.get("kind").getAsString())
                            ? "jcss-demo-Local--id-control"
                            : "--jcss-demo-Local--accent");
            if ("PROPERTY".equals(symbol.get("kind").getAsString())) {
                assertEquals(
                        occurrence.getAsJsonArray("requires").get(0).getAsString(),
                        symbol.get("owner").getAsString());
            }
        }
        assertEquals(owners, Set.of("demo.Local", "demo.PublicNames"));
    }

    @Test
    public void finalGlobalResourcesCanStabilizeCssOnlyProperties() throws Exception {
        var result = CssProcessorTest.compile(Map.of(
                "Button.java",
                source("GlobalCss", "@CssStable", ":root { --accent: red; }", "final class Button", "")));
        assertTrue(result.success(), result.diagnostics());
        assertEquals(symbol(result, "PROPERTY", "accent").get("value").getAsString(), "--accent");
        assertTrue(symbol(result, "PROPERTY", "accent").get("stable").getAsBoolean());
        assertFalse(result.files().containsKey("generated/demo/Styled_Button.java"));
    }

    @Test
    public void rootStabilityDoesNotStabilizeTheModifier() throws Exception {
        var result = CssProcessorTest.compile(Map.of(
                "Button.java",
                source(
                        "Css",
                        "",
                        "&.success { color: green; }",
                        "abstract class Button",
                        "@CssStable abstract String style(); abstract String success();")));
        assertTrue(result.success(), result.diagnostics());
        assertTrue(symbol(result, "CLASS", "").get("stable").getAsBoolean());
        assertFalse(symbol(result, "CLASS", "success").get("stable").getAsBoolean());
    }

    @Test
    public void importingAnimationPromisesMustMatchTheDefiningOwner() throws Exception {
        for (boolean providerStable : List.of(false, true)) {
            for (boolean promise : List.of(false, true)) {
                String frames = source(
                        "Keyframes", providerStable ? "@CssStable" : "", "to { opacity: 0; }", "final class Pulse", "");
                String consumer = source(
                        "Css",
                        "@CssStable @CssImport(value=Pulse.class, name=\"pulse\")",
                        "color: red;",
                        "abstract class Button",
                        "static Button of() { return Styled_Button.get(); } abstract String style();"
                                + (promise ? "@CssStable " : "")
                                + "@CssAnimation(name=\"pulse\") abstract String animation();");
                var result =
                        CssProcessorTest.compile(Map.of("Button.java", consumer, "Pulse.java", frames), "demo.Button");
                assertEquals(result.success(), !promise || providerStable, result.diagnostics());
                if (result.success()) {
                    assertEquals(result.values().get("demo.Button.animation"), "jcss-demo-Pulse--keyframes");
                } else {
                    assertTrue(
                            result.diagnostics().contains("conflicts with its defining owner's policy"),
                            result.diagnostics());
                }
            }
        }
    }

    @Test
    public void stableGlobalsUseAuthoredIdsAndPropertiesIncludingCssOnlyNames() throws Exception {
        for (boolean definition : List.of(false, true)) {
            String input = source(
                    "GlobalCss",
                    definition ? "@CssStable" : "",
                    "#control { --accent: red; color: var(--accent); --css-only: blue; }",
                    "abstract class Button",
                    "static Button of() { return Styled_Button.get(); }"
                            + "@CssStable @CssId abstract String control();"
                            + "@CssStable @CssCustomProperty abstract String accent();");
            var result = CssProcessorTest.compile(Map.of("Button.java", input), "demo.Button");
            assertTrue(result.success(), result.diagnostics());
            assertEquals(result.values(), Map.of("demo.Button.control", "control", "demo.Button.accent", "--accent"));
            assertTrue(text(result, "classes/demo/Button.css").contains("#control{--accent:red;color:var(--accent);"));
            assertTrue(symbol(result, "ID", "control").get("stable").getAsBoolean());
            assertTrue(symbol(result, "PROPERTY", "accent").get("stable").getAsBoolean());
            var cssOnly = symbol(result, "PROPERTY", "css-only");
            assertEquals(cssOnly.get("stable").getAsBoolean(), definition);
            assertEquals(
                    cssOnly.get("value").getAsString(),
                    definition ? "--css-only" : "--jcss-demo-Button--css_002d_only");
        }
    }

    @Test
    public void scopedDefinitionsKeepReadableNamesAndMarkIndividualTokens() throws Exception {
        for (boolean definition : List.of(false, true)) {
            var result = CssProcessorTest.compile(
                    Map.of(
                            "Button.java",
                            source(
                                    "Css",
                                    definition ? "@CssStable" : "",
                                    "--accent: red; color: var(--accent); filter: url(#control);"
                                            + " &.success { opacity: .5; } @keyframes pulse { to { opacity: 0; } }",
                                    "abstract class Button",
                                    "static Button of() { return Styled_Button.get(); }abstract String style();"
                                        + " @CssStable abstract String success();@CssId abstract String control();"
                                        + " @CssCustomProperty abstract String accent();@CssAnimation(name=\"pulse\")"
                                        + " abstract String animation();")),
                    "demo.Button");
            assertTrue(result.success(), result.diagnostics());
            assertEquals(result.values().get("demo.Button.success"), "jcss-demo-Button jcss-demo-Button--success");
            assertEquals(result.values().get("demo.Button.control"), "jcss-demo-Button--id-control");
            assertEquals(result.values().get("demo.Button.accent"), "--jcss-demo-Button--accent");
            assertEquals(result.values().get("demo.Button.animation"), "jcss-demo-Button--keyframes-pulse");
            assertTrue(symbol(result, "CLASS", "success").get("stable").getAsBoolean());
            for (var entry : Map.of("CLASS", "", "ID", "control", "PROPERTY", "accent", "ANIMATION", "pulse")
                    .entrySet()) {
                assertEquals(
                        symbol(result, entry.getKey(), entry.getValue())
                                .get("stable")
                                .getAsBoolean(),
                        definition);
            }
        }
        var cssOnly = CssProcessorTest.compile(Map.of(
                "Button.java",
                source(
                        "Css",
                        "@CssStable",
                        "animation: pulse 1s; @keyframes pulse { to { opacity: 0; } }",
                        "abstract class Button",
                        "abstract String style();")));
        assertTrue(cssOnly.success(), cssOnly.diagnostics());
        assertTrue(symbol(cssOnly, "ANIMATION", "pulse").get("stable").getAsBoolean());
    }

    @Test
    public void standaloneKeyframesKeepTheirClassDerivedName() throws Exception {
        for (boolean definition : List.of(false, true)) {
            var result = CssProcessorTest.compile(
                    Map.of(
                            "Button.java",
                            source(
                                    "Keyframes",
                                    definition ? "@CssStable" : "",
                                    "to { --accent: red; }",
                                    "abstract class Button",
                                    "static Button of() { return Styled_Button.get(); } @CssStable @CssAnimation"
                                            + " abstract String name();")),
                    "demo.Button");
            assertTrue(result.success(), result.diagnostics());
            assertEquals(result.values().get("demo.Button.name"), "jcss-demo-Button--keyframes");
            assertTrue(symbol(result, "ANIMATION", "").get("stable").getAsBoolean());
            assertEquals(symbol(result, "PROPERTY", "accent").get("stable").getAsBoolean(), definition);
        }
    }

    @Test
    public void stabilityUsesTheEffectiveInterfaceDeclaration() throws Exception {
        for (var child : List.of(
                "",
                "interface Child extends Base { @CssCustomProperty String accent(); }",
                "interface Child extends Base { @CssStable @CssCustomProperty String accent(); }")) {
            String input = source(
                            "GlobalCss",
                            "",
                            "body { --accent: red; }",
                            "abstract class Button implements " + (child.isEmpty() ? "Base" : "Child"),
                            "static Button of() { return Styled_Button.get(); }")
                    + "interface Base { @CssStable @CssCustomProperty String accent(); }" + child;
            var result = CssProcessorTest.compile(Map.of("Button.java", input), "demo.Button");
            assertTrue(result.success(), result.diagnostics());
            boolean stable = child.isEmpty() || child.contains("@CssStable");
            assertEquals(result.values().get("demo.Button.accent"), stable ? "--accent" : "--jcss-demo-Button--accent");
            assertEquals(symbol(result, "PROPERTY", "accent").get("stable").getAsBoolean(), stable);
        }
    }

    @Test
    public void stableMetadataSelectionDoesNotReplaceCovariantSignatureChecks() throws Exception {
        for (var order : List.of("A, B", "B, A")) {
            String input = source(
                            "GlobalCss",
                            "",
                            "body { --accent: red; }",
                            "abstract class Button implements " + order,
                            "static Button of() { return Styled_Button.get(); }")
                    + "interface A { @CssCustomProperty String accent(); }"
                    + "interface B { @CssStable @CssCustomProperty Object accent(); }";
            var result = CssProcessorTest.compile(Map.of("Button.java", input), "demo.Button");
            assertTrue(result.success(), result.diagnostics());
            assertEquals(
                    result.values().get("demo.Button.accent"),
                    "A, B".equals(order) ? "--accent" : "--jcss-demo-Button--accent");
        }
    }

    @Test
    public void canonicalImportedPolicySurvivesSourceAndBinaryProviders() throws Exception {
        for (var providerPolicy : List.of("", "@CssStable", "method")) {
            String provider = source(
                    "GlobalCss",
                    "method".equals(providerPolicy) ? "" : providerPolicy,
                    ":root { --accent: red; }",
                    "abstract class Defaults",
                    ("method".equals(providerPolicy) ? "@CssStable " : "")
                            + "@CssCustomProperty abstract String accent();");
            String alias = source(
                    "GlobalCss",
                    "@CssStable @CssImport(value=Defaults.class, property=\"accent\", name=\"ink\")",
                    ":root { --ink: blue; }",
                    "final class Alias",
                    "");
            var compiled = CssProcessorTest.compile(Map.of("Defaults.java", provider, "Alias.java", alias));
            assertTrue(compiled.success(), compiled.diagnostics());
            var directory = Files.createTempDirectory("css-stable-binary-");
            try {
                for (var entry : compiled.files().entrySet()) {
                    if (entry.getKey().startsWith("classes/") && entry.getKey().endsWith(".class")) {
                        var path = directory.resolve(entry.getKey().substring(8));
                        Files.createDirectories(path.getParent());
                        Files.write(path, Base64.getDecoder().decode(entry.getValue()));
                    }
                }
                for (boolean binary : List.of(false, true)) {
                    for (boolean promise : List.of(false, true)) {
                        String consumer = source(
                                "Css",
                                "@CssStable @CssImport(value=Alias.class, property=\"ink\", name=\"alias\")",
                                "color: var(--alias); --owned: blue;",
                                "abstract class Button",
                                "static Button of() { return Styled_Button.get(); } abstract String style();"
                                        + (promise ? "@CssStable " : "")
                                        + "@CssCustomProperty(name=\"alias\") abstract String accent();");
                        var result = CssProcessorTest.compile(
                                binary
                                        ? Map.of("Button.java", consumer)
                                        : Map.of(
                                                "Button.java",
                                                consumer,
                                                "Defaults.java",
                                                provider,
                                                "Alias.java",
                                                alias),
                                binary
                                        ? List.of(
                                                "-classpath",
                                                CssProcessorTest.applicationClasspath()
                                                        + File.pathSeparator
                                                        + directory)
                                        : List.of(),
                                "demo.Button");
                        assertEquals(result.success(), !promise || !providerPolicy.isEmpty(), result.diagnostics());
                        if (result.success()) {
                            assertEquals(
                                    result.values().get("demo.Button.accent"),
                                    providerPolicy.isEmpty() ? "--jcss-demo-Defaults--accent" : "--accent");
                            assertTrue(symbol(result, "PROPERTY", "owned")
                                    .get("stable")
                                    .getAsBoolean());
                            String metadata = text(result, "classes/demo/Button.css.json");
                            assertFalse(metadata.contains("\"name\":\"alias\""), metadata);
                        } else {
                            assertTrue(
                                    result.diagnostics().contains("conflicts with its defining owner's policy"),
                                    result.diagnostics());
                        }
                    }
                }
            } finally {
                CssProcessorTest.delete(directory);
            }
        }
    }

    @Test
    public void unsupportedDefinitionsAndConcreteOrInvalidAccessorsFail() throws Exception {
        for (var declaration : List.of("@CssStable class Button {}", "@CssStable interface Button {}")) {
            var result = CssProcessorTest.compile(Map.of("Button.java", IMPORTS + declaration));
            assertFalse(result.success());
            assertTrue(result.diagnostics().contains("Declare exactly one"), result.diagnostics());
        }
        for (var kind : List.of("Css", "GlobalCss", "Keyframes")) {
            String css = "--accent: red;";
            if (!"Css".equals(kind)) {
                css = ("Keyframes".equals(kind) ? "to" : "body") + " { " + css + " }";
            }
            for (var member : List.of(
                    "@CssStable String helper() { return \"manual\"; }",
                    "@CssStable static String helper() { return \"manual\"; }",
                    "@CssStable @CssCustomProperty abstract String accent(int ignored);",
                    "@CssStable @CssCustomProperty abstract Object accent();")) {
                var result = CssProcessorTest.compile(Map.of(
                        "Button.java",
                        source(
                                kind,
                                "",
                                css,
                                "abstract class Button",
                                ("Css".equals(kind) ? "abstract String style(); " : "") + member)));
                assertFalse(result.success(), kind + ": " + member);
                assertTrue(
                        result.diagnostics().contains("must be abstract")
                                || result.diagnostics().contains("must take no arguments"),
                        result.diagnostics());
                assertFalse(result.files().containsKey("generated/demo/Styled_Button.java"));
            }
        }
    }
}
