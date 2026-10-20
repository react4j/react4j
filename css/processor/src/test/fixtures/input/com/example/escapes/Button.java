package com.example.escapes;

import org.jspecify.annotations.NullMarked;
import react4j.css.Css;

@NullMarked
@Css("""
    content: "\141\s\\a";\
    color: red;
    &:hover { opacity: 0.5; }
    """)
abstract class Button {
    abstract String style();
}
