package com.example.input_semantics;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.Render;
import react4j.annotations.View;

@NullMarked
@View(type = View.Type.STATEFUL)
abstract class InputKeysNamesView<InputKeys, ArrayKeyStrategy> {
    InputKeysNamesView(@Input InputKeys[][] values, @Input ArrayKeyStrategy[] other) {}

    @Render
    @Nullable
    ReactNode render() {
        return null;
    }
}
