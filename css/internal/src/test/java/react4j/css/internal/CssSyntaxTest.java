package react4j.css.internal;

import static org.testng.Assert.*;

import java.util.List;
import org.testng.annotations.Test;

public final class CssSyntaxTest {
    @Test
    public void sharedRulesAreValidatedBeforeModelConversionCanDiscardThem() {
        for (var css : List.of(
                "@import 'missing.css';",
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
}
