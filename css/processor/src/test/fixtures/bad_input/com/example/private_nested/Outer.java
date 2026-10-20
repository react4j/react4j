package com.example.private_nested;

import react4j.css.Css;

class Outer {
    @Css("""
        color: red;
        """)
    private abstract static class Button {
        abstract String style();
    }
}
