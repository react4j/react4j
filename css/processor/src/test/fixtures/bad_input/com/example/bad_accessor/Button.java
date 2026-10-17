package com.example.bad_accessor;

import org.jspecify.annotations.NullMarked;
import react4j.css.Css;

@NullMarked
@Css("""
    &.success { color: red; }
    """)
abstract class Button {
    abstract String style();

    abstract int success();
}
