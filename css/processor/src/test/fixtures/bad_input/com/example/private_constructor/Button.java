package com.example.private_constructor;

import org.jspecify.annotations.NullMarked;
import react4j.css.Css;

@NullMarked
@Css("""
    color: red;
    """)
abstract class Button {
    abstract String style();

    private Button() {}
}
