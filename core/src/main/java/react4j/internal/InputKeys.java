package react4j.internal;

import org.jspecify.annotations.Nullable;

/** Support for generated immutable-input keys. */
public final class InputKeys {
    private InputKeys() {}

    public static String frame(@Nullable final String value) {
        return null == value ? "-1:" : value.length() + ":" + value;
    }

    public static String arrayKey(@Nullable final Object value) {
        if (null == value) {
            return "n";
        }
        final var key = new StringBuilder("a");
        if (value instanceof String[] values) {
            for (final String element : values) {
                key.append(frame(element));
            }
        } else if (value instanceof boolean[] values) {
            for (final boolean element : values) {
                key.append(frame(String.valueOf(element)));
            }
        } else if (value instanceof byte[] values) {
            for (final byte element : values) {
                key.append(frame(String.valueOf(element)));
            }
        } else if (value instanceof short[] values) {
            for (final short element : values) {
                key.append(frame(String.valueOf(element)));
            }
        } else if (value instanceof int[] values) {
            for (final int element : values) {
                key.append(frame(String.valueOf(element)));
            }
        } else if (value instanceof long[] values) {
            for (final long element : values) {
                key.append(frame(String.valueOf(element)));
            }
        } else if (value instanceof char[] values) {
            for (final char element : values) {
                key.append(frame(String.valueOf(element)));
            }
        } else if (value instanceof float[] values) {
            for (final float element : values) {
                key.append(frame(0 == element ? "0" : String.valueOf(element)));
            }
        } else if (value instanceof double[] values) {
            for (final double element : values) {
                key.append(frame(0 == element ? "0" : String.valueOf(element)));
            }
        } else {
            throw new IllegalArgumentException("Unsupported immutable array type");
        }
        return key.toString();
    }
}
