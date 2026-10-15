package com.example.jspecify;
import java.util.Arrays;
import java.util.List;
import org.jspecify.annotations.*;
import react4j.ReactNode;
import react4j.annotations.*;
@NullMarked
@View(type = View.Type.STATEFUL)
abstract class BoundedNullableDefaultView {
    @Input abstract List<String> value();
    @InputDefault @SuppressWarnings("unchecked")
    static <T extends List<@Nullable String>> T getValueDefault() { return (T) Arrays.asList((String) null); }
    @Render @Nullable ReactNode render() { return null; }
}
