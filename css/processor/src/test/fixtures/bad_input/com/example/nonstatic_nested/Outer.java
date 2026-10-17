package com.example.nonstatic_nested;

import react4j.css.Css;

class Outer {
    @Css("""
        color: red;
        """)
    abstract class Button {
        abstract String style();
    }
}
