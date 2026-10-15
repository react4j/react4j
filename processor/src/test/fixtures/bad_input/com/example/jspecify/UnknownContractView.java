package com.example.jspecify;
import org.jspecify.annotations.*;
import react4j.ReactNode;
import react4j.annotations.*;
@NullUnmarked
@View(type = View.Type.STATEFUL)
abstract class UnknownContractView {
    @Input abstract String value();
    @Render @Nullable ReactNode render() { return null; }
}
