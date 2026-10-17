package com.example.private_enclosing;

import org.jspecify.annotations.NullMarked;
import react4j.css.Css;

@NullMarked
class Outer {
    private static class Hidden {
        @Css("""
            color: red;
            """)
        abstract static class Button {
            abstract String style();
        }
    }
}
