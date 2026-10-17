package com.example.basic;

import org.jspecify.annotations.NullMarked;
import react4j.css.Css;

@NullMarked
@Css("""
    color: var(--on-accent);
    &:focus-visible { outline: 2px solid var(--focus); }
    &.success { background-color: var(--success); }
    &.cancel { background-color: var(--cancel); border: 2px dotted currentColor; }
    """)
abstract class Button {
    abstract String style();

    abstract String success();

    protected abstract String cancel();
}
