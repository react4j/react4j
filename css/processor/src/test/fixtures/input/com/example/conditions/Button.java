package com.example.conditions;

import org.jspecify.annotations.NullMarked;
import react4j.css.Css;

@NullMarked
@Css("""
    @media (width > 1px) { &.success { color: green; } }
    &:hover, &.success { &:disabled { opacity: 0.5; } }
    @supports (display: grid) and (color: red) { &.success { display: grid; } }
    """)
abstract class Button {
    abstract String style();

    public abstract String success();
}
