package com.example.interface_symbols;

import org.jspecify.annotations.NullMarked;
import react4j.css.Css;
import react4j.css.CssAnimation;
import react4j.css.CssCustomProperty;
import react4j.css.CssId;
import react4j.css.CssImport;
import react4j.css.GlobalCss;
import react4j.css.Keyframes;

@NullMarked
public final class Owner {
    public interface Symbols<T> {
        @CssId
        T getControl();

        @CssCustomProperty
        String getAccent();

        @CssAnimation
        String getPulse();
    }

    interface Refined extends Symbols<String> {
        @Override
        @CssId(name = "widget")
        String getControl();
    }

    @Keyframes("""
        to { filter: url(#control); color: var(--accent); }
        """)
    public abstract static class Pulse implements Symbols<String> {
        public static Pulse get() {
            return Owner_Styled_Pulse.get();
        }
    }

    @CssImport(value = Pulse.class, name = "pulse")
    @GlobalCss("""
        #control { color: var(--accent); }
        """)
    public abstract static class Defaults implements Symbols<String> {
        public static Defaults get() {
            return Owner_Styled_Defaults.get();
        }
    }

    @CssImport(value = Pulse.class, name = "pulse")
    @Css("""
        filter: url(#widget);
        color: var(--accent);
        """)
    abstract static class Button implements Refined {
        static Button get() {
            return Owner_Styled_Button.get();
        }

        abstract String style();

        @Override
        public abstract String getAccent();
    }
}
