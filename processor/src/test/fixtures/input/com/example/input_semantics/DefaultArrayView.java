package com.example.input_semantics;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.InputDefault;
import react4j.annotations.Render;
import react4j.annotations.View;

@NullMarked
@View(type = View.Type.STATEFUL)
abstract class DefaultArrayView {
    DefaultArrayView(@Input String[] values) {}

    @InputDefault
    static String[] getValuesDefault() {
        return new String[] {"default"};
    }

    @Render
    @Nullable
    ReactNode render() {
        return null;
    }
}
