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
abstract class PrivateKeyConstructor {
    public static final class Keys implements InputKeyGenerator<String[]> {
        private Keys() {}

        public String getKey(String[] value) {
            return "k";
        }
    }

    PrivateKeyConstructor(@Input(keyGenerator = Keys.class) String[] values) {}

    @Render
    @Nullable
    ReactNode render() {
        return null;
    }
}
