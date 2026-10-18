package react4j.css.processor;

import static org.testng.Assert.*;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import org.jspecify.annotations.NullMarked;
import org.testng.annotations.Test;

@NullMarked
public final class CssInterfaceProcessorTest {
    private static final String IMPORTS = "package demo;\n"
            + "import org.jspecify.annotations.NullMarked;\n"
            + "import react4j.css.Css;\n"
            + "import react4j.css.GlobalCss;\n"
            + "import react4j.css.Keyframes;\n"
            + "import react4j.css.CssImport;\n"
            + "import react4j.css.CssId;\n"
            + "import react4j.css.CssAnimation;\n"
            + "import react4j.css.CssCustomProperty;\n";

    private static String source(String kind, String css, String declaration, String members, String contracts) {
        return IMPORTS + "@NullMarked\n@" + kind + "(\"\"\"\n" + css + "\n\"\"\")\n" + declaration + " {\n" + members
                + "\n}\n" + contracts;
    }

    private static String text(CssProcessorTest.Result result, String path) {
        assertTrue(result.files().containsKey(path), result.files().keySet().toString());
        return new String(Base64.getDecoder().decode(result.files().get(path)), StandardCharsets.UTF_8);
    }

    @Test
    public void inheritedGenericAccessorsAndOrdinaryHelpersNeedNoRedeclaration() throws Exception {
        String contracts = "interface Base<T> { T style(); String success(); String VALUE = \"value\";"
                + "default String helper() { return style().toString(); } static String get() { return \"static\"; }"
                + "private String hidden() { return \"private\"; }}"
                + "interface Child<U> extends Base<U> {}";
        var result = CssProcessorTest.compile(
                Map.of(
                        "Button.java",
                        source(
                                "Css",
                                "color: red; &.success { color: green; }",
                                "abstract class Button implements Child<String>",
                                "static Button of() { return Styled_Button.get(); }",
                                contracts)),
                "demo.Button");
        assertTrue(result.success(), result.diagnostics());
        assertEquals(result.diagnostics(), "");
        assertEquals(result.values().get("demo.Button.style"), "jcss-demo-Button");
        assertEquals(result.values().get("demo.Button.success"), "jcss-demo-Button jcss-demo-Button--success");
        String generated = text(result, "generated/demo/Styled_Button.java");
        assertTrue(generated.contains("public String style()"), generated);
        assertTrue(generated.contains("public String success()"), generated);
        assertFalse(generated.contains("String helper()"), generated);
    }

    @Test
    public void covariantContractsDoNotDependOnInterfaceOrder() throws Exception {
        for (var order : List.of("Broad, Narrow", "Narrow, Broad")) {
            var result = CssProcessorTest.compile(
                    Map.of(
                            "Button.java",
                            source(
                                    "Css",
                                    "color: red;",
                                    "abstract class Button implements " + order,
                                    "static Button of() { return Styled_Button.get(); }",
                                    "interface Broad { Object style(); } interface Narrow { String style(); }")),
                    "demo.Button");
            assertTrue(result.success(), result.diagnostics());
            assertEquals(result.values().get("demo.Button.style"), "jcss-demo-Button");
        }
    }

    @Test
    public void invalidInheritedSignaturesAreAuthorErrors() throws Exception {
        for (var contract : List.of(
                "int style();",
                "Object style();",
                "String style(int value);",
                "<T> String style();",
                "String style() throws Exception;")) {
            var result = CssProcessorTest.compile(Map.of(
                    "Button.java",
                    source(
                            "Css",
                            "color: red;",
                            "abstract class Button implements Contract",
                            "",
                            "interface Contract { " + contract + " }")));
            assertFalse(result.success(), contract);
            assertTrue(result.diagnostics().contains("must take no arguments and return String"), result.diagnostics());
            assertFalse(result.files().containsKey("generated/demo/Styled_Button.java"));
        }
        var raw = CssProcessorTest.compile(Map.of(
                "Button.java",
                source(
                        "Css",
                        "color: red;",
                        "abstract class Button implements Contract",
                        "",
                        "interface Contract<T> { T style(); }")));
        assertFalse(raw.success());
        assertTrue(raw.diagnostics().contains("must take no arguments and return String"), raw.diagnostics());
    }

    @Test
    public void defaultRootStillRequiresReabstraction() throws Exception {
        String contract = "interface Contract { default String style() { return \"manual\"; } }";
        var invalid = CssProcessorTest.compile(Map.of(
                "Button.java",
                source("Css", "color: red;", "abstract class Button implements Contract", "", contract)));
        assertFalse(invalid.success());
        assertTrue(invalid.diagnostics().contains("require abstract String style()"), invalid.diagnostics());
        var valid = CssProcessorTest.compile(Map.of(
                "Button.java",
                source(
                        "Css",
                        "color: red;",
                        "abstract class Button implements Contract",
                        "public abstract String style();",
                        contract)));
        assertTrue(valid.success(), valid.diagnostics());
    }

    @Test
    public void resourceInterfacesWithOnlyHelpersRemainResourceOnly() throws Exception {
        for (var kind : List.of("GlobalCss", "Keyframes")) {
            String css = "Keyframes".equals(kind) ? "to { opacity: .5; }" : "body { color: red; }";
            var result = CssProcessorTest.compile(Map.of(
                    "Button.java",
                    source(
                            kind,
                            css,
                            "final class Button implements Contract",
                            "",
                            "interface Contract { String VALUE=\"value\"; default String helper() { return"
                                    + " VALUE; } }")));
            assertTrue(result.success(), result.diagnostics());
            assertTrue(result.files().containsKey("classes/demo/Button.css"));
            assertFalse(result.files().keySet().stream().anyMatch(path -> path.startsWith("generated/")));
            var invalid = CssProcessorTest.compile(Map.of(
                    "Button.java",
                    source(
                            kind,
                            css,
                            "abstract class Button implements Contract",
                            "",
                            "interface Contract { String missing(); }")));
            assertFalse(invalid.success());
            assertTrue(invalid.diagnostics().contains("require a symbol annotation: missing"), invalid.diagnostics());
        }
    }

    @Test
    public void inheritedInstanceGetConflictsWithGeneratedFactory() throws Exception {
        for (var member : List.of("String get();", "default String get() { return \"value\"; }")) {
            var result = CssProcessorTest.compile(Map.of(
                    "Button.java",
                    source(
                            "Css",
                            "color: red;",
                            "abstract class Button implements Contract",
                            "abstract String style();",
                            "interface Contract { " + member + " }")));
            assertFalse(result.success(), member);
            assertTrue(
                    result.diagnostics().contains("conflicts with the generated singleton factory"),
                    result.diagnostics());
        }
    }

    @Test
    public void inheritedSymbolContractsBindEveryDefinitionKind() throws Exception {
        String contract = "interface Contract<T> { @CssId T getControl(); "
                + "@CssCustomProperty String getAccent(); @CssAnimation String name(); }";
        String pulse = IMPORTS + "@Keyframes(\"\"\"\nto { opacity: .5; }\n\"\"\") final class Pulse {}";
        for (var kind : List.of("Css", "GlobalCss", "Keyframes")) {
            String css = "filter: url(#control); color: var(--accent);";
            if (!"Css".equals(kind)) {
                css = ("Keyframes".equals(kind) ? "to" : "body") + " { " + css + " }";
            }
            String owner = source(
                    kind,
                    css,
                    "abstract class Button implements Contract<String>",
                    ("Css".equals(kind) ? "abstract String style(); " : "")
                            + "static Button of() { return Styled_Button.get(); }",
                    contract);
            if (!"Keyframes".equals(kind)) {
                owner = owner.replace("@NullMarked", "@CssImport(value=Pulse.class,name=\"name\") @NullMarked");
            }
            var result = CssProcessorTest.compile(Map.of("Button.java", owner, "Pulse.java", pulse), "demo.Button");
            assertTrue(result.success(), kind + ": " + result.diagnostics());
            assertEquals(result.values().get("demo.Button.getControl"), "jcss-demo-Button--id-control");
            assertEquals(result.values().get("demo.Button.getAccent"), "--jcss-demo-Button--accent");
            assertEquals(
                    result.values().get("demo.Button.name"),
                    "jcss-demo-" + ("Keyframes".equals(kind) ? "Button" : "Pulse") + "--keyframes");
            if (!"Keyframes".equals(kind)) {
                assertTrue(text(result, "classes/demo/Button.css.json").contains("demo.Pulse"));
            }
        }
    }

    @Test
    public void unannotatedRedeclarationsInheritAndExplicitNamesRefine() throws Exception {
        for (boolean refineInterface : List.of(false, true)) {
            for (boolean refineOwner : List.of(false, true)) {
                String contract = "interface Base { @CssCustomProperty(name=\"base\") String value(); }"
                        + "interface Contract extends Base { "
                        + (refineInterface ? "@CssCustomProperty(name=\"child\") " : "") + "String value(); }";
                String member = (refineOwner ? "@CssCustomProperty(name=\"owner\") " : "")
                        + "public abstract String value(); abstract String style();"
                        + "static Button of() { return Styled_Button.get(); }";
                var result = CssProcessorTest.compile(
                        Map.of(
                                "Button.java",
                                source(
                                        "Css",
                                        "--base: red; --child: green; --owner: blue;",
                                        "abstract class Button implements Contract",
                                        member,
                                        contract)),
                        "demo.Button");
                assertTrue(result.success(), result.diagnostics());
                assertEquals(
                        result.values().get("demo.Button.value"),
                        "--jcss-demo-Button--" + (refineOwner ? "owner" : refineInterface ? "child" : "base"));
            }
        }
    }

    @Test
    public void equivalentAndPlainDiamondDeclarationsMergeInEitherOrder() throws Exception {
        for (var second : List.of("", "@CssCustomProperty(name=\"accent\")")) {
            for (var order : List.of("A, B", "B, A")) {
                var result = CssProcessorTest.compile(
                        Map.of(
                                "Button.java",
                                source(
                                        "Css",
                                        "--accent: red;",
                                        "abstract class Button implements " + order,
                                        "abstract String style(); static Button of() { return Styled_Button.get(); }",
                                        "interface A { @CssCustomProperty(name=\"accent\") String value(); }"
                                                + "interface B { " + second + " String value(); }")),
                        "demo.Button");
                assertTrue(result.success(), result.diagnostics());
                assertEquals(result.values().get("demo.Button.value"), "--jcss-demo-Button--accent");
            }
        }
    }

    @Test
    public void conflictingDiamondNamesNeedAnExplicitAnnotatedRedeclaration() throws Exception {
        String contracts = "interface A { @CssCustomProperty(name=\"a\") String value(); }"
                + "interface B { @CssCustomProperty(name=\"b\") String value(); }";
        for (var order : List.of("A, B", "B, A")) {
            for (var member : List.of("", "public abstract String value();")) {
                var invalid = CssProcessorTest.compile(Map.of(
                        "Button.java",
                        source(
                                "Css",
                                "--chosen: red;",
                                "abstract class Button implements " + order,
                                "abstract String style(); " + member,
                                contracts)));
                assertFalse(invalid.success());
                assertTrue(
                        invalid.diagnostics().contains("inherits conflicting CSS names; redeclare"),
                        invalid.diagnostics());
                assertFalse(invalid.files().containsKey("classes/demo/Button.css"));
            }
            var valid = CssProcessorTest.compile(Map.of(
                    "Button.java",
                    source(
                            "Css",
                            "--chosen: red;",
                            "abstract class Button implements " + order,
                            "abstract String style(); @CssCustomProperty(name=\"chosen\") public abstract String"
                                    + " value();",
                            contracts)));
            assertTrue(valid.success(), valid.diagnostics());
            var child = CssProcessorTest.compile(Map.of(
                    "Button.java",
                    source(
                            "Css",
                            "--chosen: red;",
                            "abstract class Button implements Child",
                            "abstract String style();",
                            contracts + "interface Child extends " + order
                                    + " { @CssCustomProperty(name=\"chosen\") String value(); }")));
            assertTrue(child.success(), child.diagnostics());
        }
    }

    @Test
    public void changingSymbolKindsCannotBeResolvedByRedeclaration() throws Exception {
        for (var other : List.of("CssId", "CssAnimation")) {
            for (var shape : List.of("A, B", "B, A", "Child")) {
                String contracts = "interface A { @CssCustomProperty String value(); }"
                        + "interface B { @" + other + " String value(); }"
                        + "interface Child extends A { @" + other + " String value(); }";
                var result = CssProcessorTest.compile(Map.of(
                        "Button.java",
                        source(
                                "Css",
                                "--value: red;",
                                "abstract class Button implements " + shape,
                                "abstract String style(); @CssCustomProperty public abstract String value();",
                                contracts)));
                assertFalse(result.success());
                assertTrue(result.diagnostics().contains("inherits conflicting symbol kinds"), result.diagnostics());
                assertFalse(result.files().containsKey("classes/demo/Button.css"));
            }
        }
    }

    @Test
    public void concreteSymbolMethodsFailAndReabstractionRestoresGeneration() throws Exception {
        for (var kind : List.of("Css", "GlobalCss", "Keyframes")) {
            String css = "color: var(--accent);";
            if (!"Css".equals(kind)) {
                css = ("Keyframes".equals(kind) ? "to" : "body") + " { " + css + " }";
            }
            for (boolean inheritedDefault : List.of(false, true)) {
                String contracts = "interface Contract { @CssCustomProperty(name=\"accent\") "
                        + (inheritedDefault ? "default String value() { return \"manual\"; }" : "String value();")
                        + " }";
                String root = "Css".equals(kind) ? "abstract String style(); " : "";
                var invalid = CssProcessorTest.compile(Map.of(
                        "Button.java",
                        source(
                                kind,
                                css,
                                "abstract class Button implements Contract",
                                root + (inheritedDefault ? "" : "public String value() { return \"manual\"; }"),
                                contracts)));
                assertFalse(invalid.success());
                assertTrue(
                        invalid.diagnostics().contains("CSS symbol accessors must be abstract"), invalid.diagnostics());
                var valid = CssProcessorTest.compile(Map.of(
                        "Button.java",
                        source(
                                kind,
                                css,
                                "abstract class Button implements Contract",
                                root + "public abstract String value();",
                                contracts)));
                assertTrue(valid.success(), valid.diagnostics());
            }
        }
    }

    @Test
    public void standaloneContractsAreValidatedOnlyWhenConsumed() throws Exception {
        String declaration =
                IMPORTS + "interface Contract<T> { @CssCustomProperty(name=\"--invalid\") T value(int parameter); }";
        var standalone = CssProcessorTest.compile(Map.of("Contract.java", declaration));
        assertTrue(standalone.success(), standalone.diagnostics());
        assertFalse(standalone.files().keySet().stream().anyMatch(path -> !path.endsWith(".class")));
        var consumer = CssProcessorTest.compile(Map.of(
                "Contract.java",
                declaration,
                "Button.java",
                source(
                        "Css",
                        "color: red;",
                        "abstract class Button implements Contract<String>",
                        "abstract String style();",
                        "")));
        assertFalse(consumer.success());
        assertTrue(consumer.diagnostics().contains("must take no arguments and return String"), consumer.diagnostics());
        assertFalse(consumer.files().containsKey("classes/demo/Button.css"));
    }
}
