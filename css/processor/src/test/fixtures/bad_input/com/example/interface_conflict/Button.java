package com.example.interface_conflict;

import org.jspecify.annotations.NullMarked;
import react4j.css.Css;
import react4j.css.CssCustomProperty;

interface First {
    @CssCustomProperty(name = "first")
    String value();
}

interface Second {
    @CssCustomProperty(name = "second")
    String value();
}

@NullMarked
@Css("""
    --first: red;
    --second: blue;
    """)
abstract class Button implements First, Second {
    abstract String style();
}
