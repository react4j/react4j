package react4j.css.internal;

import static org.testng.Assert.*;

import java.util.Map;
import org.testng.annotations.Test;

public final class UnicodeCaseFoldTest {
    @Test
    public void fullCaselessMatchingIsIndependentOfLocaleAndJdkUnicodeVersion() {
        for (var entry : Map.of(
                        "Straße",
                        "strasse",
                        "STRASSE",
                        "strasse",
                        "Σςσ",
                        "σσσ",
                        "İIı",
                        "i\u0307iı",
                        "Kſﬀ",
                        "ksff",
                        "\uD801\uDC00",
                        "\uD801\uDC28",
                        "\u1C89",
                        "\u1C8A",
                        "  A  B  ",
                        "  a  b  ")
                .entrySet()) {
            assertEquals(UnicodeCaseFold.fold(entry.getKey()), entry.getValue());
        }
        assertEquals(UnicodeCaseFold.fold(""), "");
        assertNotEquals(UnicodeCaseFold.fold("Å"), UnicodeCaseFold.fold("A\u030A"));
        assertNotEquals(UnicodeCaseFold.fold("A B"), UnicodeCaseFold.fold("A  B"));
        assertEquals(CssFontValues.canonical("back\\slash"), "back\\slash");
    }
}
