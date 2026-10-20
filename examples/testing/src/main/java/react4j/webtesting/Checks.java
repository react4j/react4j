package react4j.webtesting;

import java.util.Arrays;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public final class Checks {
    private Checks() {}

    public static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    public static void equal(String expected, @Nullable String actual) {
        check(expected.equals(actual), "Expected <" + expected + ">, got <" + actual + ">");
    }

    public static void arrayEqual(String[] expected, String[] actual) {
        check(
                Arrays.equals(expected, actual),
                "Expected " + Arrays.toString(expected) + ", got " + Arrays.toString(actual));
    }
}
