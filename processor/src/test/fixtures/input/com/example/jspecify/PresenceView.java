package com.example.jspecify;
import java.util.ArrayList;
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
    @Deprecated @InputDefault static String getDefaultedDefault() { return "default"; }
    @Deprecated @Input abstract @Nullable ArrayList<String> collection();
    @Publish @Nullable String getPublished() { return null; }
    @Render @Nullable ReactNode render() { return null; }
}
