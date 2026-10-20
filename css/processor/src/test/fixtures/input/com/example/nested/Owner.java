package com.example.nested;

import org.jspecify.annotations.NullMarked;
import react4j.css.Css;

@NullMarked
final class Owner {
    @Css("""
        &._ready { color: green; }
        """)
    abstract static class Inner {
        abstract String style();

        abstract String _ready();
    }
}
