package com.example.static_field;

import org.jspecify.annotations.NullMarked;
import react4j.css.Css;

@NullMarked
@Css("""
    color: red;
    """)
abstract class Button {
    static final int VALUE = 1;

    abstract String style();
}
