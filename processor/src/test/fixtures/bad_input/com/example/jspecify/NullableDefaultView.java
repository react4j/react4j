package com.example.jspecify;
import org.jspecify.annotations.*;
import react4j.ReactNode;
import react4j.annotations.*;
@NullMarked
@View(type = View.Type.STATEFUL)
abstract class NullableDefaultView {
    @Input abstract String value();
    @InputDefault static @Nullable String getValueDefault() { return null; }
    @Render @Nullable ReactNode render() { return null; }
}
