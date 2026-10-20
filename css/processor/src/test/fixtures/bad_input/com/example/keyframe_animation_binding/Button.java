package com.example.keyframe_animation_binding;

import org.jspecify.annotations.NullMarked;
import react4j.css.Css;

@NullMarked
@Css("""
    @keyframes pulse {
      to { animation: none; }
    }
    """)
abstract class Button {
    abstract String style();
}
