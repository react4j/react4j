package com.example.extra_animation_time;

import org.jspecify.annotations.NullMarked;
import react4j.css.Css;

@NullMarked
@Css("""
    @keyframes pulse {}
    animation: pulse 1s 2s 3s;
    """)
abstract class Button {
    abstract String style();
}
