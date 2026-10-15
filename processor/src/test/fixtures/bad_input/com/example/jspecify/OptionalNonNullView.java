package com.example.jspecify;
import org.jspecify.annotations.*;
import react4j.ReactNode;
import react4j.annotations.*;
@NullMarked
@View(type = View.Type.STATEFUL)
abstract class OptionalNonNullView {
    @Input(require = Feature.DISABLE) abstract String value();
    @Render @Nullable ReactNode render() { return null; }
}
