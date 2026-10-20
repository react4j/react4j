package com.example.assets;

import org.jspecify.annotations.NullMarked;
import react4j.css.Asset;
import react4j.css.Assets;
import react4j.css.Css;
import react4j.css.CssImport;
import react4j.css.GlobalCss;

@NullMarked
public final class Owner {
    @Assets
    public abstract static class Media implements AssetContract {
        public static Media get() {
            return Owner_Assets_Media.get();
        }

        @Asset("card/portrait.png")
        abstract String portrait();

        @Asset("card/heading.ttf")
        abstract String font();

        String decorated() {
            return "icon: " + icon();
        }
    }

    @GlobalCss("""
        @font-face {
          font-family: "Shared Heading";
          src: url("asset:media.font") format("truetype");
          font-display: swap;
        }
        """)
    @CssImport(value = Media.class, name = "media")
    static final class Fonts {}

    @Css("""
        background: image-set("asset:media.icon?variant=1#shape" 1x, url("asset:media.portrait") 2x);
        font: italic 700 1.5rem/1.2 heading, sans-serif;
        """)
    @CssImport(value = Media.class, name = "media")
    @CssImport(value = Fonts.class, font = "Shared Heading", name = "heading")
    abstract static class Button {
        static Button get() {
            return Owner_Styled_Button.get();
        }

        abstract String style();
    }
}

@NullMarked
interface AssetContract {
    @Asset("card/edit.svg")
    String icon();
}
