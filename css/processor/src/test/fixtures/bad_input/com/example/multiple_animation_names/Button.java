package com.example.multiple_animation_names;

import org.jspecify.annotations.NullMarked;
import react4j.css.Css;

@NullMarked
@Css("""
    @keyframes pulse {}
    @keyframes glow {}
    animation: pulse glow 1s;
    """)
abstract class Button {
    abstract String style();
}
