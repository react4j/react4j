package com.example.unsupported_at_rule;

import org.jspecify.annotations.NullMarked;
import react4j.css.Css;

@NullMarked
@Css("""
    @container (width > 1px) { &:hover { color: red; } }
    """)
abstract class Button {
    abstract String style();
}
