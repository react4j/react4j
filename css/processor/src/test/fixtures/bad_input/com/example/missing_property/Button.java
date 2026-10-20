package com.example.missing_property;

import org.jspecify.annotations.NullMarked;
import react4j.css.Css;
import react4j.css.CssCustomProperty;

@NullMarked
@Css("""
    color: red;
    """)
abstract class Button {
    abstract String style();

    @CssCustomProperty
    abstract String accent();
}
