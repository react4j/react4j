package com.example.factory_conflict;

import org.jspecify.annotations.NullMarked;
import react4j.css.Css;

@NullMarked
@Css("""
    color: red;
    """)
abstract class Button {
    static String get() {
        return "";
    }

    abstract String style();
}
