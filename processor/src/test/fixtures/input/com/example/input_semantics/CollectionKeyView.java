package com.example.input_semantics;

import java.util.List;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import react4j.InputKeyGenerator;
import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.Render;
import react4j.annotations.View;

@NullMarked
@View(type = View.Type.STATEFUL)
abstract class CollectionKeyView {
    public static final class Keys implements InputKeyGenerator<List<String>> {
        public String getKey(List<String> value) {
            return String.join(",", value);
        }
    }

    CollectionKeyView(@Input(keyGenerator = Keys.class) List<String> values) {}

    @Render
    @Nullable
    ReactNode render() {
        return null;
    }
}
