package com.example.jspecify;
import org.jspecify.annotations.*;
import react4j.ReactNode;
import react4j.annotations.*;
@NullMarked
@View(type = View.Type.STATEFUL)
abstract class ParametricOptionalView<T extends @Nullable Object> {
    @Input(require = Feature.DISABLE) abstract T value();
    @Render @Nullable ReactNode render() { return null; }
}
