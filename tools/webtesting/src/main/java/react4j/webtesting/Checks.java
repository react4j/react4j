package react4j.webtesting;

import org.jspecify.annotations.NullMarked;

@NullMarked
public final class Checks {
    private Checks() {}

    public static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
