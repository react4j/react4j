package com.example.instance_initializer;

import org.jspecify.annotations.NullMarked;
import react4j.css.Css;

@NullMarked
@Css("""
    color: red;
    """)
abstract class Button {
    {
        System.out.println("initialized");
    }

    abstract String style();
}
