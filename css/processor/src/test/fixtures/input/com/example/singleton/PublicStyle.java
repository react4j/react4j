package com.example.singleton;

import org.jspecify.annotations.NullMarked;
import react4j.css.Css;

@NullMarked
@Css("""
    &.selected { color: green; }
    """)
public abstract class PublicStyle {
    public abstract String style();

    public abstract String selected();

    public final String forSelection(boolean selected) {
        return selected ? selected() : style();
    }

    public static PublicStyle get() {
        return Styled_PublicStyle.get();
    }
}
