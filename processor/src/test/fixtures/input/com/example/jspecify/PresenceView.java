package com.example.jspecify;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import react4j.ReactNode;
import react4j.annotations.*;
@NullMarked
@View(type = View.Type.STATEFUL)
abstract class PresenceView {
    @Input(require = Feature.ENABLE) abstract @Nullable String requiredNullable();
    @Input abstract @Nullable String optionalNullable();
    @Input(require = Feature.DISABLE) abstract String defaulted();
    @InputDefault static String getDefaultedDefault() { return "default"; }
    @Render @Nullable ReactNode render() { return null; }
}
