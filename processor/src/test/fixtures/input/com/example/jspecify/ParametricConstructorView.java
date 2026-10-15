package com.example.jspecify;
import org.jspecify.annotations.*;
import react4j.ReactNode;
import react4j.annotations.*;
@NullMarked
@View(type = View.Type.STATEFUL)
abstract class ParametricConstructorView<T extends @Nullable Object> {
    ParametricConstructorView(@Input(require = Feature.ENABLE) T value) {}
    @Render @Nullable ReactNode render() { return null; }
}
