package com.example.shared;

import org.jspecify.annotations.NullMarked;
import react4j.css.Css;
import react4j.css.CssImport;

@NullMarked
@CssImport(name = "beat", value = Pulse.class)
@CssImport(name = "unused", value = Pulse.class)
@Css("""
    animation: beat 1s linear infinite;
    """)
abstract class Button {
    abstract String style();
}
