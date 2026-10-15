package com.example.jspecify;
import org.jspecify.annotations.*;
import react4j.ReactNode;
import react4j.annotations.*;
@NullMarked
@View(type = View.Type.STATEFUL)
abstract class ParametricOmissionView<T extends @Nullable Object> {
    @Input abstract T value();
    @Render @Nullable ReactNode render() { return null; }
}
