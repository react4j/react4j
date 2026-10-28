package com.example.input_semantics;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.Render;
import react4j.annotations.View;

@NullMarked
@View(type = View.Type.STATEFUL)
abstract class MixedArrayScalarsView {
    MixedArrayScalarsView(
            @Input String[] values,
            @Input int count,
            @Input char letter,
            @Input long id,
            @Input float rate,
            @Input boolean flag,
            @Input double size,
            @Input byte tiny,
            @Input short small,
            @Input @Nullable Long wrapped) {}

    @Render
    @Nullable
    ReactNode render() {
        return null;
    }
}
