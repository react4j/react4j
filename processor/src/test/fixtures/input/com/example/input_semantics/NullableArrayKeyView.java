package com.example.input_semantics;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import react4j.InputKeyGenerator;
import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.Render;
import react4j.annotations.View;

@NullMarked
@View(type = View.Type.STATEFUL)
abstract class NullableArrayKeyView {
    public static final class Keys implements InputKeyGenerator<@Nullable String @Nullable []> {
        public String getKey(@Nullable String @Nullable [] value) {
            return null == value ? "null" : String.valueOf(value.length);
        }
    }

    NullableArrayKeyView(@Input(keyGenerator = Keys.class) @Nullable String @Nullable [] values) {}

    @Render
    @Nullable
    ReactNode render() {
        return null;
    }
}
