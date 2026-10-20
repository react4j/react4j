package com.example.important;

import org.jspecify.annotations.NullMarked;
import react4j.css.Css;

@NullMarked
@Css("""
    color: red !important;
    """)
abstract class Button {
    abstract String style();
}
