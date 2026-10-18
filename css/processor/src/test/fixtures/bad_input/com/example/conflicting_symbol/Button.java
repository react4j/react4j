package com.example.conflicting_symbol;

import org.jspecify.annotations.NullMarked;
import react4j.css.Css;
import react4j.css.CssAnimation;
import react4j.css.CssId;

@NullMarked
@Css("""
    filter: url(#control);
    """)
abstract class Button {
    abstract String style();

    @CssId
    @CssAnimation
    abstract String control();
}
