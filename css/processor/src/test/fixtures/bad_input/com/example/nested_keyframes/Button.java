package com.example.nested_keyframes;

import org.jspecify.annotations.NullMarked;
import react4j.css.Css;

@NullMarked
@Css("""
    &:hover {
      @keyframes pulse {}
    }
    """)
abstract class Button {
    abstract String style();
}
