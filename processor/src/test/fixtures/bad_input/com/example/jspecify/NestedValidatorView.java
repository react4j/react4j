package com.example.jspecify;
import org.jspecify.annotations.*;
import react4j.ReactNode;
import react4j.annotations.*;
@NullMarked
@View(type = View.Type.STATEFUL)
abstract class NestedValidatorView {
    @Input abstract java.util.List<@Nullable String> value();
    @InputValidate(name = "value") void validate(java.util.List<String> value) {}
    @Render @Nullable ReactNode render() { return null; }
}
