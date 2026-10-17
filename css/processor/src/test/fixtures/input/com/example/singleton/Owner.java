package com.example.singleton;

import org.jspecify.annotations.NullMarked;
import react4j.css.Css;

@NullMarked
public final class Owner {
    public static final class Middle {
        @Css("""
            color: red;
            """)
        public abstract static class Style {
            public abstract String style();
        }
    }
}
