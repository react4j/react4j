package react4j.internal;

import arez.component.Identifiable;
import org.jspecify.annotations.Nullable;
import react4j.Keyed;

/** Support for generated immutable-input keys. */
public final class InputKeys {
    private InputKeys() {}

    /** Traverses the declared dimensions, even when a scalar leaf happens to contain an array. */
    public static String arrayKey(
            @Nullable final Object values, final int dimensions, final ArrayKeyStrategy strategy) {
        if (null == values) {
            return "n";
        }
        final var key = new StringBuilder("a");
        if (1 < dimensions) {
            for (final var value : (@Nullable Object[]) values) {
                key.append(frame(arrayKey(value, dimensions - 1, strategy)));
            }
        } else {
            appendLeaf(key, values, strategy);
        }
        return key.toString();
    }

    private static void appendLeaf(final StringBuilder key, final Object values, final ArrayKeyStrategy strategy) {
        switch (strategy) {
            case STRING -> appendStrings(key, (@Nullable Object[]) values);
            case KEYED -> appendKeyed(key, (@Nullable Object[]) values);
            case AREZ_IDENTIFIABLE -> appendIdentifiable(key, (@Nullable Object[]) values);
            case DYNAMIC -> appendDynamic(key, (@Nullable Object[]) values);
            case FLOATING_POINT -> appendFloatingPoint(key, (@Nullable Number[]) values);
            case BOOLEAN -> appendBooleans(key, (boolean[]) values);
            case BYTE -> appendBytes(key, (byte[]) values);
            case SHORT -> appendShorts(key, (short[]) values);
            case INT -> appendInts(key, (int[]) values);
            case LONG -> appendLongs(key, (long[]) values);
            case CHAR -> appendChars(key, (char[]) values);
            case FLOAT -> appendFloats(key, (float[]) values);
            case DOUBLE -> appendDoubles(key, (double[]) values);
        }
    }

    private static void appendStrings(final StringBuilder key, final @Nullable Object[] values) {
        for (final var value : values) {
            key.append(frame(null == value ? null : String.valueOf(value)));
        }
    }

    private static void appendKeyed(final StringBuilder key, final @Nullable Object[] values) {
        for (final var value : values) {
            key.append(frame(null == value ? null : Keyed.getKey(value)));
        }
    }

    private static void appendIdentifiable(final StringBuilder key, final @Nullable Object[] values) {
        for (final var value : values) {
            key.append(frame(null == value ? null : String.valueOf(Identifiable.<Object>getArezId(value))));
        }
    }

    private static void appendDynamic(final StringBuilder key, final @Nullable Object[] values) {
        for (final var value : values) {
            key.append(frame(dynamicKey(value)));
        }
    }

    private static void appendFloatingPoint(final StringBuilder key, final @Nullable Number[] values) {
        for (final var value : values) {
            key.append(frame(floatingPointKey(value)));
        }
    }

    private static void appendBooleans(final StringBuilder key, final boolean[] values) {
        for (final var value : values) {
            key.append(frame(String.valueOf(value)));
        }
    }

    private static void appendBytes(final StringBuilder key, final byte[] values) {
        for (final var value : values) {
            key.append(frame(String.valueOf(value)));
        }
    }

    private static void appendShorts(final StringBuilder key, final short[] values) {
        for (final var value : values) {
            key.append(frame(String.valueOf(value)));
        }
    }

    private static void appendInts(final StringBuilder key, final int[] values) {
        for (final var value : values) {
            key.append(frame(String.valueOf(value)));
        }
    }

    private static void appendLongs(final StringBuilder key, final long[] values) {
        for (final var value : values) {
            key.append(frame(String.valueOf(value)));
        }
    }

    private static void appendChars(final StringBuilder key, final char[] values) {
        for (final var value : values) {
            key.append(frame(String.valueOf(value)));
        }
    }

    private static void appendFloats(final StringBuilder key, final float[] values) {
        for (final var value : values) {
            key.append(frame(0 == value ? "0" : String.valueOf(value)));
        }
    }

    private static void appendDoubles(final StringBuilder key, final double[] values) {
        for (final var value : values) {
            key.append(frame(0 == value ? "0" : String.valueOf(value)));
        }
    }

    private static @Nullable String dynamicKey(@Nullable final Object value) {
        if (null == value) {
            return null;
        }
        if (value instanceof Keyed) {
            return String.valueOf(Keyed.getKey(value));
        }
        if (value instanceof Identifiable) {
            return String.valueOf(Identifiable.<Object>getArezId(value));
        }
        return String.valueOf(value);
    }

    private static @Nullable String floatingPointKey(@Nullable final Number value) {
        if (null == value) {
            return null;
        }
        return 0 == value.doubleValue() ? "0" : String.valueOf(value);
    }

    public static String frame(@Nullable final String value) {
        return null == value ? "-1:" : value.length() + ":" + value;
    }
}
