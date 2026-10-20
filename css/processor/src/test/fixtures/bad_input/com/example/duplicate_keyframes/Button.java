package com.example.duplicate_keyframes;

import org.jspecify.annotations.NullMarked;
import react4j.css.Css;

@NullMarked
@Css("""
    @keyframes pulse {}
    @keyframes pulse {}
    """)
abstract class Button {
    abstract String style();
}
