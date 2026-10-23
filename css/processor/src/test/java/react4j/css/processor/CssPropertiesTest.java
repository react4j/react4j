package react4j.css.processor;

import static org.testng.Assert.*;

import com.helger.css.decl.CascadingStyleSheet;
import com.helger.css.reader.CSSReader;
import com.helger.css.reader.CSSReaderSettings;
import com.helger.css.writer.CSSWriterSettings;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.testng.annotations.Test;
import react4j.css.internal.CssResource.Kind;
import react4j.css.internal.CssSyntax;
import react4j.css.internal.SourceMap;

public final class CssPropertiesTest {
    private static final String ROOT = "jcss-demo-Button";

    private static CssCompiler.Result component(String css, Map<String, CssProperties.Import> imports) {
        return CssCompiler.component(
                css,
                ROOT,
                new CssCompiler.Exports(List.of("style"), Set.of(), Set.of()),
                offset -> new SourceMap.Location("Button.java", css, 0, offset),
                Map.of(),
                imports,
                Map.of(),
                new CssCompiler.Resources(Map.of(), Map.of()));
    }

    private static CascadingStyleSheet parse(String css) {
        return Objects.requireNonNull(CSSReader.readFromStringReader(css, new CSSReaderSettings()));
    }

    @Test
    public void staticTransitionTargetsFailBeforeRenamingSharedData() {
        var error = expectThrows(
                CssSyntax.InvalidCss.class,
                () -> component(
                        "--targets:--angle;transition:var(--targets) 1s;list-style-type:var(--targets);", Map.of()));
        assertTrue(String.valueOf(error.getMessage())
                .contains("Managed transition targets in static variable definitions"));
    }

    @Test
    public void declarationsNestedFallbacksAndRuntimeOnlySymbolsShareOwnership() {
        String css = "--accent: red; color: VAR(--accent,var(--fallback,blue)); left: calc(var(--left) * 1px); content:"
                + " '--accent';";
        var result = component(css, Map.of());
        assertEquals(
                result.properties(),
                Map.of(
                        "accent",
                        "--" + ROOT + "--accent",
                        "fallback",
                        "--" + ROOT + "--fallback",
                        "left",
                        "--" + ROOT + "--left"));
        assertEquals(
                CssProperties.names(css, Kind.COMPONENT), result.properties().keySet());
        var declarations = parse(result.output().css()).getAllStyleRules().get(0);
        assertNotNull(declarations.getDeclarationOfPropertyName("--" + ROOT + "--accent"));
        assertEquals(
                Objects.requireNonNull(declarations.getDeclarationOfPropertyName("color"))
                        .getExpression()
                        .getAsCSSString(new CSSWriterSettings(true), 0),
                "VAR(--" + ROOT + "--accent,var(--" + ROOT + "--fallback,blue))");
        assertEquals(
                Objects.requireNonNull(declarations.getDeclarationOfPropertyName("content"))
                        .getExpression()
                        .getAsCSSString(new CSSWriterSettings(true), 0),
                "'--accent'");
        assertTrue(result.propertyImports().isEmpty());
    }

    @Test
    public void stableGlobalImportsKeepDistinctIntermediateAliases() {
        var binding = new CssProperties.Binding("theme.Defaults", "accent", Kind.GLOBAL, true);
        var result = component(
                "color: var(--local); --local: blue;",
                Map.of("local", new CssProperties.Import("theme.Defaults", binding)));
        assertTrue(binding.stable());
        assertEquals(binding.cssName(), "--accent");
        assertEquals(result.properties().get("local"), "--" + ROOT + "--local");
        assertEquals(
                result.output().css(), "." + ROOT + "{color:var(--" + ROOT + "--local);--" + ROOT + "--local:blue;}");
        String css = ":root { --accent: red; color: var(--accent); }";
        var owned = CssCompiler.global(
                css,
                ROOT,
                new CssCompiler.Exports(List.of(), Set.of(), Set.of()),
                offset -> new SourceMap.Location("Button.java", css, 0, offset),
                Map.of(),
                Map.of(),
                Map.of(),
                new CssCompiler.Resources(Map.of(), Map.of()));
        assertEquals(owned.output().css(), ":root{--" + ROOT + "--accent:red;color:var(--" + ROOT + "--accent);}");
    }

    @Test
    public void importsRenameReadsAndOverridesAndTrackOnlyUsedBindings() {
        var binding = new CssProperties.Binding("theme.Defaults", "focus-ring", Kind.GLOBAL, false);
        var imported = new CssProperties.Import("theme.Alias", binding);
        var result =
                component("--local: gold; outline-color: var(--local);", Map.of("local", imported, "unused", imported));
        assertEquals(result.properties(), Map.of("local", "--" + ROOT + "--local"));
        assertEquals(result.propertyImports(), Set.of("local"));
        assertNotNull(parse(result.output().css())
                .getAllStyleRules()
                .get(0)
                .getDeclarationOfPropertyName("--" + ROOT + "--local"));
    }

    @Test
    public void allDefinitionKindsAndConditionalContextsAreVisited() {
        for (var kind : Kind.values()) {
            String body = "--x: red; color: v\\61 r(--y);";
            String css = Kind.COMPONENT == kind
                    ? body
                    : (Kind.GLOBAL == kind || Kind.UTILITY == kind) ? "body { " + body + " }" : "to { " + body + " }";
            assertEquals(CssProperties.names(css, kind), Set.of("x", "y"));
        }
        String css = "@keyframes pulse {to {--x: red; color: var(--y);}} @supports ((--z: red) and (color: var(--w))) {"
                + " &:hover { color: var(--v); } } @media screen { &:focus { --u: green; } }";
        assertEquals(CssProperties.names(css, Kind.COMPONENT), Set.of("x", "y", "z", "w", "v", "u"));
        assertEquals(component(css, Map.of()).properties().keySet(), Set.of("x", "y", "z", "w", "v", "u"));
        var empty = component("transform: custom();", Map.of());
        assertTrue(empty.properties().isEmpty());
    }

    @Test
    public void namesAreCaseSensitiveAndEncodedWithoutCollisions() {
        var result = component("--A: red; --a: blue; --a-b: gold; --a_b: green; --π: red;", Map.of());
        assertEquals(Set.copyOf(result.properties().values()).size(), 5);
        assertEquals(result.properties().get("π"), "--" + ROOT + "--_03c0_");
        for (var name : Set.of("", "--accent", "--", "1bad", "a b", "x\\31", "#x", "'x'")) {
            expectThrows(IllegalArgumentException.class, () -> CssProperties.checkName(name));
        }
    }

    @Test
    public void malformedVarGrammarAndEscapedPropertiesFail() {
        for (var css : Set.of(
                "color: var();",
                "color: var(red);",
                "color: var('--x');",
                "color: var(--x red);",
                "----x: red;",
                "--a\\62: red;",
                "color: var(--x,var(red));")) {
            expectThrows(CssSyntax.InvalidCss.class, () -> component(css, Map.of()));
        }
    }
}
