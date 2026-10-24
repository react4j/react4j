package react4j.css.processor;

import static org.testng.Assert.*;

import java.util.HashSet;
import java.util.List;
import java.util.regex.Pattern;
import org.jspecify.annotations.NullMarked;
import org.testng.annotations.Test;
import react4j.css.internal.CssIdentity;

@NullMarked
public final class NamesTest {
    @Test
    public void rootsIncludePackageAndEnclosingOwners() {
        assertEquals(CssIdentity.root("com.biz.ui.SaveButton"), "jcss-com-biz-ui-SaveButton");
        assertEquals(CssIdentity.root("SaveButton"), "jcss-SaveButton");
        assertEquals(CssIdentity.root("demo.Outer$Inner"), "jcss-demo-Outer_0024_Inner");
        assertEquals(CssIdentity.encode("aAzZ09"), "aAzZ09");
        assertEquals(CssIdentity.encode(""), "");
    }

    @Test
    public void escapesPreserveUtf16AndCannotImpersonateSeparators() {
        assertEquals(CssIdentity.encode("_-$π\uD801\uDC00"), "_005f__002d__0024__03c0__d801__dc00_");
        var owners = List.of(
                "com.biz_ui.SaveButton",
                "com_biz.ui.SaveButton",
                "demo.Outer$Inner",
                "demo.Outer_0024_Inner",
                "demo.π",
                "demo._03c0_",
                "demo.Button",
                "demo_Button",
                "Button");
        var roots = new HashSet<String>();
        for (var owner : owners) {
            assertTrue(roots.add(CssIdentity.root(owner)), owner);
        }
    }

    @Test
    public void everyUtf16CodeUnitHasADistinctCssSafeEncoding() {
        var encodings = new HashSet<String>();
        var safe = Pattern.compile("[a-zA-Z0-9_-]+");
        for (int code = 0; code <= Character.MAX_VALUE; code++) {
            String encoded = CssIdentity.encode(String.valueOf((char) code));
            assertTrue(encodings.add(encoded), "Collision at code unit " + code);
            assertTrue(safe.matcher(encoded).matches(), "Unsafe encoding at code unit " + code);
        }
    }
}
