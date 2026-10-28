package react4j;

import org.jspecify.annotations.Nullable;

/** Supplies one immutable input's identity before its view is constructed. */
@FunctionalInterface
public interface InputKeyGenerator<T extends @Nullable Object> {
    /** Return a stable, non-null key. Equal keys retain the original constructor input. */
    String getKey(T value);
}
