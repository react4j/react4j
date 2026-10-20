package com.example.missing_selector;

import org.jspecify.annotations.NullMarked;
import react4j.css.Css;

@NullMarked
@Css("""
    /* &.success {} */
    content: "&.success";
    """)
abstract class Button {
    abstract String style();

    abstract String success();
}
