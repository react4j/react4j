package com.example.css_wide_animation_list;

import org.jspecify.annotations.NullMarked;
import react4j.css.Css;

@NullMarked
@Css("""
    @keyframes pulse {}
    animation-name: pulse, inherit;
    """)
abstract class Button {
    abstract String style();
}
