package react4j.css.internal;

import static org.testng.Assert.*;

import com.helger.css.decl.CSSDeclaration;
import com.helger.css.decl.CSSKeyframesRule;
import com.helger.css.decl.visit.CSSVisitor;
import com.helger.css.decl.visit.DefaultCSSVisitor;
import com.helger.css.writer.CSSWriter;
import com.helger.css.writer.CSSWriterSettings;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.testng.annotations.Test;

public final class CssSyntaxTest {
    @Test
    public void emptyVariableFallbackPreservesTheCommaAndSourceLocations() {
        String css = ".turn { transform: var(--rotate, ) var(--skew,); }";
        var sheet = new CssSyntax(css, 0).parse(false);
        var declaration = sheet.getAllStyleRules().get(0).getAllDeclarations().get(0);
        assertEquals(declaration.getExpression().getMemberCount(), 2);
        assertEquals(
                CssTokens.read(css).stream()
                        .map(token -> css.substring(token.start(), token.end()))
                        .toList(),
                List.of("turn", "--rotate", "--skew"));
        expectThrows(
                CssSyntax.InvalidCss.class,
                () -> new CssSyntax(".turn { transform: var(--rotate / ); }", 0).parse(false));
    }

    @Test
    public void sharedKeyframesAreRejectedAtEverySupportedNestingLevel() {
        for (var wrapper : List.of(
                "%s",
                "button { %s }",
                "@media (color) { %s }",
                "@supports (display: grid) { %s }",
                "@media (color) { @supports (display: grid) { %s } }")) {
            String source = wrapper.formatted("@keyframes pulse { to { opacity: 0; } }");
            var error = expectThrows(CssSyntax.InvalidCss.class, () -> new CssSyntax(source, 0).parse(false));
            assertTrue(
                    String.valueOf(error.getMessage()).toLowerCase(Locale.ROOT).contains("keyframes"),
                    error.getMessage());
            assertEquals(error.offset(), source.indexOf("@keyframes"));
        }
    }

    @Test
    public void sharedRulesAreValidatedBeforeModelConversionCanDiscardThem() {
        for (var css : List.of(
                "@import 'missing.css';",
                "@keyframes pulse { to { opacity: 1; } }",
                "@scope (.root) { body { color: red; } }")) {
            expectThrows(CssSyntax.InvalidCss.class, () -> new CssSyntax(css, 0).parse(false));
        }
        assertEquals(new CssSyntax("/* comment */", 0).parse(false).getRuleCount(), 0);
        assertEquals(
                new CssSyntax(
                                "@media screen { button { color: red; } } @supports (display: grid) { body { display:"
                                        + " grid; } }",
                                0)
                        .parse(false)
                        .getRuleCount(),
                2);
        String source = "button { color red; }";
        var error = expectThrows(CssSyntax.InvalidCss.class, () -> new CssSyntax(source, 0).parse(false));
        assertEquals(error.offset(), source.indexOf(';'));
        String incomplete = "button { color:";
        var eof = expectThrows(CssSyntax.InvalidCss.class, () -> new CssSyntax(incomplete, 0).parse(false));
        assertEquals(eof.offset(), incomplete.indexOf(':'));
    }

    @Test
    public void modernConditionalBodiesShareVisitorsLocationsAndRendering() {
        String css = """
            /* 😀 */
            @layer theme,utilities;
            @property /* --rotate is a comment */ --rotate { syntax:"<angle>";inherits:false;initial-value:0deg; }
            @layer theme { :root { --space:.25rem; } }
            @layer utilities {
              @supports (display:grid) {
                @container sidebar (width >= 20rem) and style(--density:compact) {
                  @starting-style { .starting\\:opacity-0 { opacity:0;transform:var(--rotate, ); } }
                }
              }
            }
            """;
        var syntax = new CssSyntax(css, 0);
        var sheet = syntax.parse(CssSyntax.Mode.UTILITY);
        var properties = new ArrayList<String>();
        CSSVisitor.visitCSS(sheet, new DefaultCSSVisitor() {
            @Override
            public void onDeclaration(CSSDeclaration declaration) {
                properties.add(declaration.getProperty());
                assertEquals(
                        css.substring(syntax.invalid("location", declaration).offset())
                                .indexOf(declaration.getProperty()),
                        0);
                if ("opacity".equals(declaration.getProperty())) {
                    declaration.setProperty("z-index");
                }
            }
        });
        assertTrue(properties.containsAll(List.of("--space", "opacity", "transform")), properties.toString());
        var tokens = CssTokens.read(css);
        assertTrue(tokens.stream().anyMatch(token -> "starting:opacity-0".equals(token.name())));
        assertTrue(tokens.stream().anyMatch(token -> "--density".equals(token.name())));
        assertEquals(
                tokens.stream().filter(token -> "--rotate".equals(token.name())).count(), 2);
        assertEquals(
                tokens.stream()
                        .filter(token -> "--rotate".equals(token.name()))
                        .findFirst()
                        .orElseThrow()
                        .start(),
                css.indexOf("--rotate {"));
        String rendered = new CSSWriter(new CSSWriterSettings(true)).getCSSAsString(sheet);
        assertTrue(rendered.contains("@container sidebar"), rendered);
        assertTrue(rendered.contains("@starting-style"), rendered);
        assertTrue(rendered.contains("z-index:0"), rendered);
        assertFalse(rendered.contains("opacity:0"), rendered);
        new CssSyntax(rendered, 0).parse(CssSyntax.Mode.UTILITY);
        for (var wrapper : List.of("@media screen{%s}", "@supports (display:grid){%s}", "@layer support{%s}")) {
            String registered = wrapper.formatted("@property --state{syntax:\"*\";inherits:false;}");
            assertEquals(
                    CssTokens.read(registered).stream()
                            .filter(token -> "--state".equals(token.name()))
                            .count(),
                    1);
        }
    }

    @Test
    public void modesKeepImportantAndNativeNestingBoundariesExplicit() {
        String important = ".x{display:none!important;}";
        new CssSyntax(important, 0).parse(CssSyntax.Mode.GLOBAL);
        for (var mode : List.of(CssSyntax.Mode.COMPONENT, CssSyntax.Mode.UTILITY)) {
            expectThrows(CssSyntax.InvalidCss.class, () -> new CssSyntax(important, 0).parse(mode));
        }
        String nested = ".x{&:hover{opacity:.5;}}";
        new CssSyntax(nested, 0).parse(CssSyntax.Mode.COMPONENT);
        expectThrows(CssSyntax.InvalidCss.class, () -> new CssSyntax(nested, 0).parse(CssSyntax.Mode.UTILITY));
        for (var css : List.of(
                "@container {.x{opacity:0;}}",
                "@starting-style (color) {.x{opacity:0;}}",
                "@container panel (width>10px){.x{color red;}}")) {
            expectThrows(CssSyntax.InvalidCss.class, () -> new CssSyntax(css, 0).parse(CssSyntax.Mode.UTILITY));
        }
        String malformed = "@starting-style { .x { color red; } }";
        var error = expectThrows(
                CssSyntax.InvalidCss.class, () -> new CssSyntax(malformed, 0).parse(CssSyntax.Mode.UTILITY));
        assertEquals(error.offset(), malformed.indexOf(';'));
    }

    @Test
    public void componentKeyframesSurviveModelConversionWithTheirOriginalLocations() {
        String source = ".root {\ncolor: red;\n@keyframes pulse {\nfrom { opacity: 0; }\nto { opacity: 1; }\n}\n"
                + "&:hover { animation: pulse 1s; }\ncolor: blue;\n}";
        var syntax = new CssSyntax(source, 0);
        var sheet = syntax.parse(true);
        assertEquals(sheet.getRuleCount(), 2);
        var owner = sheet.getAllStyleRules().get(0);
        assertEquals(owner.getDeclarationCount(), 1);
        assertEquals(owner.getRuleCount(), 2);
        var frames = (CSSKeyframesRule) sheet.getAllRules().get(1);
        assertEquals(frames.getAnimationName(), "pulse");
        assertEquals(frames.getBlockCount(), 2);
        assertEquals(syntax.invalid("header", frames).offset(), source.indexOf("@keyframes"));
        var block = frames.getAllBlocks().get(0);
        assertEquals(syntax.invalid("block", block).offset(), source.indexOf("from"));
        assertEquals(
                syntax.invalid("declaration", block.getAllDeclarations().get(0)).offset(), source.indexOf("opacity"));
    }
}
