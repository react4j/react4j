package com.example.instance_field;

import org.jspecify.annotations.NullMarked;
import react4j.css.Css;

@NullMarked
@Css("""
    color: red;
    """)
abstract class Button {
    int value;

    abstract String style();
}
