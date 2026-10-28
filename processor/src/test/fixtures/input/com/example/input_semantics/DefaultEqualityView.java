package com.example.input_semantics;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import react4j.ReactNode;
import react4j.annotations.Feature;
import react4j.annotations.Input;
import react4j.annotations.Render;
import react4j.annotations.View;

@NullMarked
@View(type = View.Type.STATEFUL)
abstract class DefaultEqualityView<T extends DefaultEqualityView.Value> {
    @arez.annotations.DefaultEqualityComparator(arez.ObjectsEqualsComparator.class)
    public static class Value {}

    public static class Child extends Value {}

    DefaultEqualityView(@Input Value immutable) {}

    @Input
    abstract Value value();

    @Input
    abstract Child childValue();

    @Input
    abstract Value[] array();

    @Input(require = Feature.ENABLE)
    abstract T bounded();

    @Input(equalityComparator = arez.ObjectsDeepEqualsComparator.class)
    abstract Value overridden();

    @Render
    @Nullable
    ReactNode render() {
        return null;
    }
}
