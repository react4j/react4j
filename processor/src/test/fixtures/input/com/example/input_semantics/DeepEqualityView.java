package com.example.input_semantics;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.OnInputChange;
import react4j.annotations.Render;
import react4j.annotations.View;

@NullMarked
@View(type = View.Type.STATEFUL)
abstract class DeepEqualityView {
    @Input(equalityComparator = arez.ObjectsDeepEqualsComparator.class)
    abstract String[] values();

    @OnInputChange
    void changed(String[] values) {}

    @OnInputChange(phase = OnInputChange.Phase.POST)
    void after(String[] values) {}

    @Render
    @Nullable
    ReactNode render() {
        return null;
    }
}
