package com.example.shared;

import org.jspecify.annotations.NullMarked;
import react4j.css.Css;
import react4j.css.CssImport;

@NullMarked
@CssImport(name = "beat", value = Pulse.class)
@CssImport(name = "unused", value = Pulse.class)
@CssImport(name = "peer", value = Button.class)
@Css("""
    animation: beat 1s linear infinite;
    &:hover + :css(peer) { color: red; }
    """)
abstract class Button {
    abstract String style();
}
