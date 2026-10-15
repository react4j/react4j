package com.example.jspecify;
import org.jspecify.annotations.*;
import react4j.ReactNode;
import react4j.annotations.*;
@NullMarked
@View(type = View.Type.STATEFUL)
abstract class NestedNullableDefaultView {
    @Input abstract java.util.List<String> value();
    @InputDefault static java.util.List<@Nullable String> getValueDefault() { return java.util.Arrays.asList((String) null); }
    @Render @Nullable ReactNode render() { return null; }
}
