package com.example.input_semantics;

import java.util.Objects;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.Render;
import react4j.annotations.View;

@NullMarked
@View(type = View.Type.STATEFUL)
abstract class PrimitiveEqualityView {
    public static final class Same implements arez.EqualityComparator {
        public boolean areEqual(@Nullable Object oldValue, @Nullable Object newValue) {
            return Objects.equals(oldValue, newValue);
        }
    }

    @Input(equalityComparator = Same.class)
    abstract boolean booleanValue();

    @Input(equalityComparator = Same.class)
    abstract byte byteValue();

    @Input(equalityComparator = Same.class)
    abstract short shortValue();

    @Input(equalityComparator = Same.class)
    abstract int intValue();

    @Input(equalityComparator = Same.class)
    abstract long longValue();

    @Input(equalityComparator = Same.class)
    abstract char charValue();

    @Input(equalityComparator = Same.class)
    abstract float floatValue();

    @Input(equalityComparator = Same.class)
    abstract double doubleValue();

    @Input(equalityComparator = Same.class)
    abstract @Nullable Boolean booleanBoxed();

    @Input(equalityComparator = Same.class)
    abstract @Nullable Byte byteBoxed();

    @Input(equalityComparator = Same.class)
    abstract @Nullable Short shortBoxed();

    @Input(equalityComparator = Same.class)
    abstract @Nullable Integer integerBoxed();

    @Input(equalityComparator = Same.class)
    abstract @Nullable Long longBoxed();

    @Input(equalityComparator = Same.class)
    abstract @Nullable Character characterBoxed();

    @Input(equalityComparator = Same.class)
    abstract @Nullable Float floatBoxed();

    @Input(equalityComparator = Same.class)
    abstract @Nullable Double doubleBoxed();

    @Render
    @Nullable
    ReactNode render() {
        return null;
    }
}
