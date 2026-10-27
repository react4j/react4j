package com.example.enhancer;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.Render;
import react4j.annotations.View;

@NullMarked
@View(type = View.Type.STATEFUL, exportBuilder = true)
abstract class ChildrenView {
    @Input(enhancer = Enhancers.Children.class)
    abstract ReactNode @Nullable [] children();

    @Input
    abstract @Nullable String title();

    @Render
    @Nullable
    ReactNode render() {
        return null;
    }
}
