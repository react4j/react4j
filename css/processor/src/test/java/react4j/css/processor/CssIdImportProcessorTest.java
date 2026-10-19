package react4j.css.processor;

import static org.testng.Assert.*;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import org.jspecify.annotations.NullMarked;
import org.testng.annotations.Test;

@NullMarked
public final class CssIdImportProcessorTest {
    private static final String IMPORTS = "package demo;\nimport react4j.css.Css;\nimport react4j.css.GlobalCss;\n"
            + "import react4j.css.Keyframes;\nimport react4j.css.CssImport;\nimport react4j.css.CssId;\n"
            + "import react4j.css.CssStable;\nimport react4j.css.CssCustomProperty;\n";

    private static String source(String name, String kind, String annotations, String css, String members) {
        return IMPORTS + annotations + "\n@" + kind + "(\"\"\"\n" + css + "\n\"\"\")\nabstract class " + name + " {\n"
                + ("Css".equals(kind) ? "abstract String style();\n" : "")
                + ("Button".equals(name) && ("Css".equals(kind) || members.contains("abstract String"))
                        ? "static Button of() { return Styled_Button.get(); }\n"
                        : "")
                + members + "\n}\n";
    }

    private static String provider(boolean stable) {
        return source(
                "Owner",
                "GlobalCss",
                "",
                "#control { --accent: red; color: var(--accent); }",
                (stable ? "@CssStable " : "") + "@CssId abstract String control();");
    }

    private static String text(CssProcessorTest.Result result, String path) {
        assertTrue(result.files().containsKey(path), result.files().keySet().toString());
        return new String(Base64.getDecoder().decode(result.files().get(path)), StandardCharsets.UTF_8);
    }

    @Test
    public void aliasesResolveInSelectorsAndFragmentsInEveryDefinitionKind() throws Exception {
        for (var kind : List.of("Css", "GlobalCss", "Keyframes")) {
            String css = "filter: url(#alias); content: '#alias'; background: url(icons.svg#alias);";
            css = "Css".equals(kind)
                    ? "&#alias { " + css + " }"
                    : ("GlobalCss".equals(kind) ? "#alias" : "to") + " { " + css + " }";
            var result = CssProcessorTest.compile(
                    Map.of(
                            "Owner.java",
                            provider(false),
                            "Button.java",
                            source(
                                    "Button",
                                    kind,
                                    "@CssImport(value=Owner.class, id=\"control\", name=\"alias\")",
                                    css,
                                    "@CssId(name=\"alias\") abstract String id();")),
                    "demo.Button");
            assertTrue(result.success(), result.diagnostics());
            assertEquals(result.values().get("demo.Button.id"), "jcss-demo-Owner--id-control");
            String output = text(result, "classes/demo/Button.css");
            assertTrue(output.contains("url(#jcss-demo-Owner--id-control)"), output);
            assertTrue(output.contains("'#alias'"), output);
            assertTrue(output.contains("url(icons.svg#alias)"), output);
        }
    }

    @Test
    public void transitiveSourceAndBinaryAliasesPermitJavaOnlyImportingGetters() throws Exception {
        for (boolean stable : List.of(false, true)) {
            String owner = provider(stable);
            String alias = source(
                    "Alias",
                    "GlobalCss",
                    "@CssImport(value=Owner.class, id=\"control\", name=\"alias\")",
                    "#alias { color: blue; }",
                    "");
            var dependency = CssProcessorTest.compile(Map.of("Owner.java", owner, "Alias.java", alias));
            assertTrue(dependency.success(), dependency.diagnostics());
            var directory = Files.createTempDirectory("css-id-binary-");
            try {
                for (var entry : dependency.files().entrySet()) {
                    if (entry.getKey().startsWith("classes/") && entry.getKey().endsWith(".class")) {
                        var path = directory.resolve(entry.getKey().substring(8));
                        Files.createDirectories(path.getParent());
                        Files.write(path, Base64.getDecoder().decode(entry.getValue()));
                    }
                }
                for (boolean binary : List.of(false, true)) {
                    for (var kind : List.of("Css", "GlobalCss", "Keyframes")) {
                        String css = "Css".equals(kind)
                                ? "color: red;"
                                : "GlobalCss".equals(kind) ? "" : "to { color: red; }";
                        String consumer = source(
                                "Button",
                                kind,
                                "@CssImport(value=Alias.class, id=\"alias\", name=\"local\")",
                                css,
                                "@CssId(name=\"local\") abstract String id();");
                        var result = CssProcessorTest.compile(
                                binary
                                        ? Map.of("Button.java", consumer)
                                        : Map.of("Button.java", consumer, "Owner.java", owner, "Alias.java", alias),
                                binary
                                        ? List.of(
                                                "-classpath",
                                                CssProcessorTest.applicationClasspath()
                                                        + File.pathSeparator
                                                        + directory)
                                        : List.of(),
                                "demo.Button");
                        assertTrue(result.success(), kind + ": " + result.diagnostics());
                        assertEquals(
                                result.values().get("demo.Button.id"),
                                stable ? "control" : "jcss-demo-Owner--id-control");
                    }
                }
            } finally {
                CssProcessorTest.delete(directory);
            }
        }
    }

    @Test
    public void idAndPropertyAliasesHaveSeparateNamespaces() throws Exception {
        String consumer = source(
                "Button",
                "Css",
                "@CssImport(value=Owner.class,id=\"control\",name=\"token\")"
                        + "@CssImport(value=Owner.class,property=\"accent\",name=\"token\")",
                "&#token { color: var(--token); }",
                "@CssId(name=\"token\") abstract String id();"
                        + "@CssCustomProperty(name=\"token\") abstract String property();");
        var result =
                CssProcessorTest.compile(Map.of("Owner.java", provider(false), "Button.java", consumer), "demo.Button");
        assertTrue(result.success(), result.diagnostics());
        assertEquals(result.values().get("demo.Button.id"), "jcss-demo-Owner--id-control");
        assertEquals(result.values().get("demo.Button.property"), "--jcss-demo-Owner--accent");
    }

    @Test
    public void explicitStablePromisesFollowTheCanonicalIdOwner() throws Exception {
        for (boolean stable : List.of(false, true)) {
            for (boolean promise : List.of(false, true)) {
                String consumer = source(
                        "Button",
                        "Css",
                        "@CssStable @CssImport(value=Owner.class,id=\"control\")",
                        "color: red;",
                        (promise ? "@CssStable " : "") + "@CssId(name=\"control\") abstract String id();");
                var result = CssProcessorTest.compile(
                        Map.of("Owner.java", provider(stable), "Button.java", consumer), "demo.Button");
                assertEquals(result.success(), !promise || stable, result.diagnostics());
                if (result.success()) {
                    assertEquals(
                            result.values().get("demo.Button.id"), stable ? "control" : "jcss-demo-Owner--id-control");
                } else {
                    assertTrue(
                            result.diagnostics().contains("conflicts with its defining owner's policy"),
                            result.diagnostics());
                }
            }
        }
    }

    @Test
    public void malformedEmptyDuplicateUnusedAndUnknownIdImportsFail() throws Exception {
        var cases = Map.ofEntries(
                Map.entry("id=\"\"", "id must not be empty"),
                Map.entry("id=\"#control\"", "Invalid imported CSS name"),
                Map.entry("id=\"1bad\"", "Invalid imported CSS name"),
                Map.entry("id=\"--\"", "Invalid imported CSS name"),
                Map.entry("id=\"control\",name=\"bad alias\"", "Invalid imported CSS name"),
                Map.entry("id=\"control\",property=\"accent\"", "mutually exclusive"),
                Map.entry("id=\"missing\"", "Unknown ID"),
                Map.entry("id=\"control\"", "Unused ID import"));
        for (var entry : cases.entrySet()) {
            var result = CssProcessorTest.compile(Map.of(
                    "Owner.java",
                    provider(false),
                    "Button.java",
                    source(
                            "Button",
                            "Css",
                            "@CssImport(value=Owner.class," + entry.getKey() + ")",
                            "color: red;",
                            "")));
            assertFalse(result.success(), entry.getKey());
            assertTrue(result.diagnostics().contains(entry.getValue()), result.diagnostics());
            assertFalse(result.files().containsKey("generated/demo/Styled_Button.java"));
        }
        var duplicate = CssProcessorTest.compile(Map.of(
                "Owner.java",
                provider(false),
                "Button.java",
                source(
                        "Button",
                        "Css",
                        "@CssImport(value=Owner.class,id=\"control\",name=\"alias\")"
                                + "@CssImport(value=Owner.class,id=\"control\",name=\"alias\")",
                        "filter: url(#alias);",
                        "")));
        assertFalse(duplicate.success());
        assertTrue(duplicate.diagnostics().contains("Duplicate imported ID"), duplicate.diagnostics());
    }

    @Test
    public void unboundGlobalIdSelectorsAreErrorsIncludingNestedFunctions() throws Exception {
        for (var css : List.of(
                "#control { color: red; }",
                "body:is(#control,:not(#control)) { color: red; }",
                "@supports (display:grid) { #control { color: red; } }",
                "body:has(#control) { color: red; }",
                ":custom(#control) { color: red; }")) {
            var result = CssProcessorTest.compile(Map.of("Button.java", source("Button", "GlobalCss", "", css, "")));
            assertFalse(result.success(), css);
            assertTrue(result.diagnostics().contains("ID"), css + ": " + result.diagnostics());
            assertFalse(result.files().containsKey("classes/demo/Button.css"));
        }
    }

    @Test
    public void eachDefinitionKindCanOwnAnImportedIdThroughAFragment() throws Exception {
        for (var kind : List.of("Css", "GlobalCss", "Keyframes")) {
            String css = "filter: url(#control);";
            if (!"Css".equals(kind)) {
                css = ("GlobalCss".equals(kind) ? "body" : "to") + " { " + css + " }";
            }
            String owner = source("Owner", kind, "", css, "@CssStable @CssId abstract String control();");
            String consumer = source(
                    "Button",
                    "Css",
                    "@CssImport(value=Owner.class,id=\"control\")",
                    "color: red;",
                    "@CssStable @CssId(name=\"control\") abstract String id();");
            var result = CssProcessorTest.compile(Map.of("Owner.java", owner, "Button.java", consumer), "demo.Button");
            assertTrue(result.success(), result.diagnostics());
            assertEquals(
                    result.values().get("demo.Button.id"),
                    "GlobalCss".equals(kind) ? "control" : "jcss-demo-Owner--id-control");
        }
    }

    @Test
    public void inheritedGenericInterfaceAccessorsCanExposeImports() throws Exception {
        String consumer = source(
                                "Button",
                                "Css",
                                "@CssImport(value=Owner.class,id=\"control\",name=\"local\")",
                                "color: red;",
                                "")
                        .replace("abstract class Button", "abstract class Button implements Contract<String>")
                + "interface Contract<T> { @CssStable @CssId(name=\"local\") T id(); }";
        var result =
                CssProcessorTest.compile(Map.of("Owner.java", provider(true), "Button.java", consumer), "demo.Button");
        assertTrue(result.success(), result.diagnostics());
        assertEquals(result.values().get("demo.Button.id"), "control");
        assertTrue(text(result, "generated/demo/Styled_Button.java").contains("public String id()"));
    }

    @Test
    public void anAliasChainMayRevisitAnOwnerWhenItEndsAtALocalId() throws Exception {
        String a = source(
                "A",
                "GlobalCss",
                "@CssImport(value=B.class,id=\"b\",name=\"alias\")",
                "#a { color: red; } #alias { color: blue; }",
                "@CssId abstract String a();");
        String b = source("B", "GlobalCss", "@CssImport(value=A.class,id=\"a\",name=\"b\")", "#b { opacity: .5; }", "");
        String consumer = source(
                "Button",
                "Css",
                "@CssImport(value=B.class,id=\"b\")",
                "color: red;",
                "@CssId(name=\"b\") abstract String id();");
        var result = CssProcessorTest.compile(Map.of("A.java", a, "B.java", b, "Button.java", consumer), "demo.Button");
        assertTrue(result.success(), result.diagnostics());
        assertEquals(result.values().get("demo.Button.id"), "jcss-demo-A--id-a");
    }

    @Test
    public void externalUrlsAndStringsDoNotUseSelectedIds() throws Exception {
        String consumer = source(
                "Button",
                "Css",
                "@CssImport(value=Owner.class,id=\"control\",name=\"alias\")",
                "background: url(icons.svg#alias); content: '#alias';",
                "");
        var result = CssProcessorTest.compile(Map.of("Owner.java", provider(false), "Button.java", consumer));
        assertFalse(result.success());
        assertTrue(result.diagnostics().contains("Unused ID import"), result.diagnostics());
    }

    @Test
    public void ownerlessIdAliasCyclesFail() throws Exception {
        String a = source("A", "GlobalCss", "@CssImport(value=B.class,id=\"b\",name=\"a\")", "#a { color: red; }", "");
        String b = source("B", "GlobalCss", "@CssImport(value=A.class,id=\"a\",name=\"b\")", "#b { color: blue; }", "");
        var result = CssProcessorTest.compile(Map.of("A.java", a, "B.java", b));
        assertFalse(result.success());
        assertTrue(result.diagnostics().contains("Cyclic ID import without an owner"), result.diagnostics());
    }
}
