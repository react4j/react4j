package com.example.input_semantics;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.Render;
import react4j.annotations.View;

@NullMarked
@View(type = View.Type.STATEFUL)
abstract class NestedArraysView {
    NestedArraysView(
            @Input String[][][] strings,
            @Input boolean[][] booleans,
            @Input byte[][] bytes,
            @Input short[][] shorts,
            @Input int[][] ints,
            @Input long[][] longs,
            @Input char[][] chars,
            @Input float[][] floats,
            @Input double[][] doubles) {}

    @Render
    @Nullable
    ReactNode render() {
        return null;
    }
}
