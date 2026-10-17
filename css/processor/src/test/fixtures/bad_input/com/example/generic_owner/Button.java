package com.example.generic_owner;

import org.jspecify.annotations.NullMarked;
import react4j.css.Css;

@NullMarked
@Css("""
    color: red;
    """)
abstract class Button<T> {
    abstract String style();
}
