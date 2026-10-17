package com.example.naming_biz.ui;

import org.jspecify.annotations.NullMarked;
import react4j.css.Css;

@NullMarked
@Css("""
    color: blue;
    """)
abstract class Button {
    abstract String style();
}
