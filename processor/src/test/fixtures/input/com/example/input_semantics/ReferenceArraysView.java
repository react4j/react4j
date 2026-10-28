package com.example.input_semantics;

import java.util.ArrayList;
import java.util.List;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import react4j.Keyed;
import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.Render;
import react4j.annotations.View;

@NullMarked
@View(type = View.Type.STATEFUL)
abstract class ReferenceArraysView<T extends @Nullable Object> {
    public enum Choice {
        ONE,
        TWO
    }

    public interface Ref extends arez.component.Identifiable<Integer> {}

    @arez.annotations.ArezComponentLike
    public static final class Like {}

    public static final class Value {}

    ReferenceArraysView(
            @Input Choice[] choices,
            @Input Keyed[] keys,
            @Input Ref[][] refs,
            @Input Like[] likes,
            @Input Value[] values,
            @Input List<String>[] lists,
            @Input ArrayList<String>[] concrete,
            @Input Object[][] objects,
            @Input T[][] generic,
            @Input @Nullable String @Nullable [] @Nullable [] nested,
            @Input Boolean[] booleans,
            @Input Byte[] bytes,
            @Input Short[] shorts,
            @Input Integer[] ints,
            @Input Long[] longs,
            @Input Character[] chars,
            @Input Float[] floats,
            @Input Double[] doubles) {}

    @Render
    @Nullable
    ReactNode render() {
        return null;
    }
}
