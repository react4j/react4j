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
abstract class CheckedKeyConstructor {
    public static final class Keys implements InputKeyGenerator<String[]> {
        public Keys() throws java.io.IOException {}

        public String getKey(String[] value) {
            return "k";
        }
    }

    CheckedKeyConstructor(@Input(keyGenerator = Keys.class) String[] values) {}

    @Render
    @Nullable
    ReactNode render() {
        return null;
    }
}
