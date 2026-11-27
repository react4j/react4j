package react4j.internal;

import org.jspecify.annotations.Nullable;

/** The active owner used while creating and rendering elements. */
public final class CurrentOwner {
    private static @Nullable Object c_owner;

    private CurrentOwner() {}

    public static @Nullable Object get() {
        return c_owner;
    }

    public static @Nullable Object swap(@Nullable Object owner) {
        final var previous = c_owner;
        c_owner = owner;
        return previous;
    }
}
