package com.example.jspecify;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import react4j.ReactNode;
import react4j.annotations.*;
@NullMarked
@View(type = View.Type.STATEFUL)
abstract class ParametricRequiredView<T extends @Nullable Object> {
    @Input(require = Feature.ENABLE) abstract T value();
    @Render @Nullable ReactNode render() { return null; }
}
