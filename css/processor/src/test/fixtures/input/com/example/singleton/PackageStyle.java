package com.example.singleton;

import org.jspecify.annotations.NullMarked;
import react4j.css.Css;

@NullMarked
@Css("""
    &.selected { color: green; }
    """)
abstract class PackageStyle {
    abstract String style();

    abstract String selected();

    final String forSelection(boolean selected) {
        return selected ? selected() : style();
    }

    public static PackageStyle get() {
        return Styled_PackageStyle.get();
    }
}
