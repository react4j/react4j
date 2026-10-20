package com.example.unknown_animation;

import org.jspecify.annotations.NullMarked;
import react4j.css.Css;

@NullMarked
@Css("""
    @keyframes pulse {}
    animation: pluse 1s ease;
    """)
abstract class Button {
    abstract String style();
}
