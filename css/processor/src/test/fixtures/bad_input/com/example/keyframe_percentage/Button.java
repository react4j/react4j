package com.example.keyframe_percentage;

import org.jspecify.annotations.NullMarked;
import react4j.css.Css;

@NullMarked
@Css("""
    @keyframes pulse {
      100.01% { opacity: 0; }
    }
    """)
abstract class Button {
    abstract String style();
}
