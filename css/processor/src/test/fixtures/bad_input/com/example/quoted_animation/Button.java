package com.example.quoted_animation;

import org.jspecify.annotations.NullMarked;
import react4j.css.Css;

@NullMarked
@Css("""
    @keyframes pulse {}
    animation-name: "pulse";
    """)
abstract class Button {
    abstract String style();
}
