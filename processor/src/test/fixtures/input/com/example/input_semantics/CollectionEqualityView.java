package com.example.input_semantics;

import java.util.List;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.Render;
import react4j.annotations.View;

@NullMarked
@View(type = View.Type.STATEFUL)
abstract class CollectionEqualityView {
    @Input(equalityComparator = arez.ObjectsEqualsComparator.class)
    abstract List<String> value();

    @Render
    @Nullable
    ReactNode render() {
        return null;
    }
}
