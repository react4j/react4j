package com.example.interfaces;

import org.jspecify.annotations.NullMarked;
import react4j.css.Css;

@NullMarked
public final class Owner {
    public interface Styles<T> {
        int Internal = 1;

        class Owner_Styled_First {}

        T style();

        String success();

        default String selected(boolean successful) {
            return successful ? success() : style().toString();
        }
    }

    @Css("""
        color: red;
        &.success { color: green; }
        """)
    public abstract static class First implements Styles<String> {
        public static First get() {
            return com.example.interfaces.Owner_Styled_First.get();
        }
    }

    @Css("""
        color: blue;
        &.success { color: purple; }
        """)
    abstract static class Second implements Styles<String> {
        static Second get() {
            return Owner_Styled_Second.get();
        }
    }
}
