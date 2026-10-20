package com.example.attribute_id;

import org.jspecify.annotations.NullMarked;
import react4j.css.Css;
import react4j.css.CssId;

@NullMarked
@Css("""
    &[id="control"] { color: red; }
    """)
abstract class Button {
    abstract String style();

    @CssId
    abstract String control();
}
