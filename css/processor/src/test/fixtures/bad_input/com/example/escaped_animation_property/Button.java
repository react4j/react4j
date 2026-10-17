package com.example.escaped_animation_property;

import org.jspecify.annotations.NullMarked;
import react4j.css.Css;

@NullMarked
@Css("""
    anim\\61 tion-name: pulse;
    """)
abstract class Button {
    abstract String style();
}
