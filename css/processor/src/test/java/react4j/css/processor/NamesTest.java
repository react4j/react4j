package react4j.css.processor;

import static org.testng.Assert.*;

import java.util.HashSet;
import java.util.List;
import java.util.regex.Pattern;
import org.testng.annotations.Test;

public final class NamesTest {
    @Test
    public void rootsIncludePackageAndEnclosingOwners() {
        assertEquals(Names.root("com.biz.ui.SaveButton"), "jcss-com-biz-ui-SaveButton");
        assertEquals(Names.root("SaveButton"), "jcss-SaveButton");
        assertEquals(Names.root("demo.Outer$Inner"), "jcss-demo-Outer_0024_Inner");
        assertEquals(Names.encode("aAzZ09"), "aAzZ09");
        assertEquals(Names.encode(""), "");
    }

    @Test
    public void escapesPreserveUtf16AndCannotImpersonateSeparators() {
        assertEquals(Names.encode("_-$π\uD801\uDC00"), "_005f__002d__0024__03c0__d801__dc00_");
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
            assertTrue(roots.add(Names.root(owner)), owner);
        }
    }

    @Test
    public void everyUtf16CodeUnitHasADistinctCssSafeEncoding() {
        var encodings = new HashSet<String>();
        var safe = Pattern.compile("[a-zA-Z0-9_-]+");
        for (int code = 0; code <= Character.MAX_VALUE; code++) {
            String encoded = Names.encode(String.valueOf((char) code));
            assertTrue(encodings.add(encoded), "Collision at code unit " + code);
            assertTrue(safe.matcher(encoded).matches(), "Unsafe encoding at code unit " + code);
        }
    }
}
