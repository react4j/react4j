package com.example.jspecify;
import java.util.List;
import org.jspecify.annotations.*;
import react4j.ReactNode;
import react4j.annotations.*;
@NullMarked
@View(type = View.Type.STATEFUL)
abstract class UnmarkedConstructorView {
    @NullUnmarked
    UnmarkedConstructorView(@NonNull List<@NonNull String> dependencies, @NonNull String service,
            @Input @Nullable String @Nullable [] values) {}
    @Render @Nullable ReactNode render() { return null; }
}
