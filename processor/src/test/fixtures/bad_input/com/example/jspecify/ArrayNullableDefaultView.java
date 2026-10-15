package com.example.jspecify;
import org.jspecify.annotations.*;
import react4j.ReactNode;
import react4j.annotations.*;
@NullMarked
@View(type = View.Type.STATEFUL)
abstract class ArrayNullableDefaultView {
    @Input abstract String[] value();
    @InputDefault static final @Nullable String[] DEFAULT_VALUE = {null};
    @Render @Nullable ReactNode render() { return null; }
}
