package react4j.css.processor;

import static org.testng.Assert.*;

import java.util.List;
import org.testng.annotations.Test;

public final class TextBlockTest {
    private static void mapping(String token, String expected, int... offsets) {
        var result = TextBlock.decode(token, 10, expected);
        assertEquals(result.value(), expected);
        assertEquals(result.offsets().length, expected.length() + 1);
        assertEquals(result.offsets(), offsets);
    }

    @Test
    public void normalizesLineEndingsAndMapsOriginalPositions() {
        mapping("\"\"\"\na\n\"\"\"", "a\n", 14, 15, 16);
        mapping("\"\"\"\ra\r\"\"\"", "a\n", 14, 15, 16);
        mapping("\"\"\"\r\na\r\n\"\"\"", "a\n", 15, 16, 18);
        mapping("\"\"\" \t\r\na\r\nb\rc\n\"\"\"", "a\nb\nc\n", 17, 18, 20, 21, 22, 23, 24);
        mapping("\"\"\"\n\"\"\"", "", 14);
        mapping("\"\"\"\r\r\"\"\"", "\n", 14, 15);
    }

    @Test
    public void stripsIncidentalIndentAndTrailingWhitespace() {
        mapping("\"\"\"\n  a  \n    b\n  \"\"\"", "a\n  b\n", 16, 19, 22, 23, 24, 25, 28);
        mapping("\"\"\"\n\t a \t\n\t \n\t b\n\t \"\"\"", "a\n\nb\n", 16, 19, 22, 25, 26, 29);
        mapping("\"\"\"\n  a\n\"\"\"", "  a\n", 14, 15, 16, 17, 18);
        mapping("\"\"\"\n  a\"\"\"", "a", 16, 17);
        mapping("\"\"\"\n  \n \"\"\"", "\n", 16, 18);
    }

    @Test
    public void mapsSimpleEscapesToTheirBackslash() {
        mapping(
                "\"\"\"\n\\b\\t\\n\\f\\r\\s\\\\\\'\\\"\"\"\"",
                "\b\t\n\f\r \\'\"",
                14,
                16,
                18,
                20,
                22,
                24,
                26,
                28,
                30,
                32);
        mapping("\"\"\"\n a\\s\n \"\"\"", "a \n", 15, 16, 18, 20);
    }

    @Test
    public void octalEscapesRespectJavaDigitLimits() {
        mapping(
                "\"\"\"\n\\0\\77\\141\\377\\400\\78\\1z\"\"\"",
                "\0?a\u00ff 0\7" + "8\1z",
                14,
                16,
                19,
                23,
                27,
                30,
                31,
                33,
                34,
                36,
                37);
        mapping("\"\"\"\n\\7\"\"\"", "\7", 14, 16);
    }

    @Test
    public void continuationsRemoveNewlinesAfterIndentStripping() {
        mapping("\"\"\"\n a\\\n b\n \"\"\"", "ab\n", 15, 19, 20, 22);
        mapping("\"\"\"\r\n a\\\r\n b\r\n \"\"\"", "ab\n", 16, 21, 22, 25);
    }

    @Test
    public void unicodeTranslationPrecedesTextBlockProcessing() {
        String escape = "\\";
        mapping("\"\"\"\n" + escape + "u0061" + escape + "uuuu0062\"\"\"", "ab", 14, 20, 29);
        mapping("\"\"\"\n" + escape + "u005c" + escape + "u006e\"\"\"", "\n", 14, 26);
        mapping("\"\"\"\n" + escape + escape + "u0061\"\"\"", "\\u0061", 14, 16, 17, 18, 19, 20, 21);
        mapping("\"\"\"\n" + escape + escape + escape + "u0061\"\"\"", "\\a", 14, 16, 22);
        mapping("\"\"\"" + escape + "u000a  a\n  \"\"\"", "a\n", 21, 22, 25);
        mapping("\"\"\"\na" + escape + "u000d\"\"\"", "a\n", 14, 15, 21);
        mapping("\"\"\"\na" + escape + "u0022\"\"", "a", 14, 15);
    }

    @Test
    public void rejectsNonTextBlocksUnsupportedEscapesAndMappingDisagreement() {
        for (var token : List.of("\"ordinary string\"", "\"\"\"\na")) {
            var error = expectThrows(IllegalArgumentException.class, () -> TextBlock.decode(token, 0, "a"));
            assertEquals(error.getMessage(), "@Css requires a literal Java text block");
        }
        var escape = expectThrows(IllegalArgumentException.class, () -> TextBlock.decode("\"\"\"\n\\q\"\"\"", 0, "q"));
        assertEquals(escape.getMessage(), "Unsupported Java escape in @Css");
        var mismatch = expectThrows(IllegalArgumentException.class, () -> TextBlock.decode("\"\"\"\na\"\"\"", 0, "b"));
        assertEquals(mismatch.getMessage(), "CSS source mapping disagrees with javac's text-block value");
    }
}
