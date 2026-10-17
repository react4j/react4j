package com.example.keyframe_important;

import org.jspecify.annotations.NullMarked;
import react4j.css.Css;

@NullMarked
@Css("""
    @keyframes pulse {
      to { opacity: 0 !important; }
    }
    """)
abstract class Button {
    abstract String style();
}
