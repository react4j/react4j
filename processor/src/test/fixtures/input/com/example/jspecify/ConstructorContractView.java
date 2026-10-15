package com.example.jspecify;
import java.util.List;
import org.jspecify.annotations.*;
import react4j.ReactNode;
import react4j.annotations.*;
@NullMarked
@View(type = View.Type.STATEFUL)
abstract class ConstructorContractView {
    ConstructorContractView(List<@Nullable String> dependencies, @Nullable String optionalService,
            @Input @Nullable String @Nullable [] values) {}
    @Render @Nullable ReactNode render() { return null; }
}
