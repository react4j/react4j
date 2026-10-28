package com.example.input_semantics;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.Render;
import react4j.annotations.View;

@NullMarked
@View(type = View.Type.STATEFUL)
abstract class PrivateComparatorConstructor {
    public static final class Same implements arez.EqualityComparator {
        private Same() {}

        public boolean areEqual(@Nullable Object a, @Nullable Object b) {
            return true;
        }
    }

    @Input(equalityComparator = Same.class)
    abstract String value();

    @Render
    @Nullable
    ReactNode render() {
        return null;
    }
}
