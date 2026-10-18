package com.example.properties;

import org.jspecify.annotations.NullMarked;
import react4j.css.Css;
import react4j.css.CssCustomProperty;
import react4j.css.CssImport;
import react4j.css.GlobalCss;
import react4j.css.Keyframes;

@NullMarked
final class Owner {
    @GlobalCss("""
        :root { --accent: green; }
        """)
    abstract static class Defaults {
        static Defaults get() {
            return Owner_Styled_Defaults.get();
        }

        @CssCustomProperty
        abstract String getAccent();
    }

    @CssImport(value = Defaults.class, property = "accent", name = "color")
    @Keyframes("""
        to { color: var(--color); opacity: var(--opacity); }
        """)
    abstract static class Pulse {
        static Pulse get() {
            return Owner_Styled_Pulse.get();
        }

        @CssCustomProperty
        abstract String color();

        @CssCustomProperty
        abstract String opacity();
    }

    @CssImport(value = Pulse.class, property = "color", name = "accent")
    @Css("""
        --accent: gold;
        color: var(--accent);
        @supports ((--runtime-width: 1px) and (width: var(--runtime-width))) {
          &:hover { width: calc(var(--runtime-width) * 2); }
        }
        """)
    abstract static class Button {
        static Button get() {
            return Owner_Styled_Button.get();
        }

        abstract String style();

        @CssCustomProperty
        abstract String accent();

        @CssCustomProperty(name = "runtime-width")
        abstract String width();
    }
}
