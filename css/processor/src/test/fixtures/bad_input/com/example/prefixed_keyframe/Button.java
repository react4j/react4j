package com.example.prefixed_keyframe;

import org.jspecify.annotations.NullMarked;
import react4j.css.Css;

@NullMarked
@Css("""
    @-webkit-keyframes pulse {}
    """)
abstract class Button {
    abstract String style();
}
