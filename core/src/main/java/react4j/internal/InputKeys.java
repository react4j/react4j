package react4j.internal;

import org.jspecify.annotations.Nullable;

/** Support for generated immutable-input keys. */
public final class InputKeys {
    private InputKeys() {}

    public static String frame(@Nullable final String value) {
        return null == value ? "-1:" : value.length() + ":" + value;
    }
}
