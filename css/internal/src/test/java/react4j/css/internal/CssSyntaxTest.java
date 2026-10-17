package react4j.css.internal;

import static org.testng.Assert.*;

import com.helger.css.decl.CSSKeyframesRule;
import java.util.List;
import java.util.Locale;
import org.testng.annotations.Test;

public final class CssSyntaxTest {
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
                "@layer components { body { color: red; } }",
                "button { color: red !important; }")) {
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
