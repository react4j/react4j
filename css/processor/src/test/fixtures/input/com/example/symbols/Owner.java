package com.example.symbols;

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
    @GlobalCss("""
        #page { --accent: blue; color: var(--accent); }
        """)
    public abstract static class Page {
        public static Page get() {
            return Owner_Styled_Page.get();
        }

        @CssId
        public abstract String page();

        @CssCustomProperty
        public abstract String accent();
    }

    @Keyframes("""
        to { filter: url(#effect); }
        """)
    public abstract static class Pulse {
        public static Pulse get() {
            return Owner_Styled_Pulse.get();
        }

        @CssAnimation
        public abstract String name();

        @CssId
        public abstract String effect();
    }

    @CssImport(value = Pulse.class, name = "pulse")
    @CssImport(value = Page.class, property = "accent", name = "ink")
    @Css("""
        @keyframes local { to { opacity: .5; } }
        &#control {
          color: var(--ink);
          filter: url(#mask);
          animation-name: local;
        }
        """)
    abstract static class Button {
        static Button get() {
            return Owner_Styled_Button.get();
        }

        abstract String style();

        @CssId
        abstract String getControl();

        @CssId(name = "mask")
        abstract String filterId();

        @CssAnimation(name = "pulse")
        abstract String sharedAnimation();

        @CssAnimation
        abstract String local();

        @CssCustomProperty
        abstract String ink();
    }
}
