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
            + "import react4j.css.Css;\nimport react4j.css.GlobalCss;\nimport react4j.css.Keyframes;\n"
            + "import react4j.css.CssId;\nimport react4j.css.CssAnimation;\nimport react4j.css.CssCustomProperty;\n";

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
}
