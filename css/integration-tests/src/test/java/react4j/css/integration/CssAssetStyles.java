package react4j.css.integration;

import jsinterop.annotations.JsMethod;
import react4j.css.Css;
import react4j.css.CssImport;
import react4j.css.GlobalCss;

public final class CssAssetStyles {
    private CssAssetStyles() {}

    @JsMethod
    public static String style() {
        return Style.get().style();
    }

    @GlobalCss("""
        @font-face {font-family:heading;src:url(https://example.com/heading.woff2);font-weight:400;font-display:swap;}
        @font-face {font-family:heading;src:url(https://example.com/bold.woff2);font-weight:700;font-display:swap;}
        @font-face {font-family:unused;src:url(https://example.com/unused.woff2);}
        """)
    static final class Fonts {}

    @CssImport(value = AssetFixtures.Media.class, name = "media")
    @GlobalCss("""
        body {background:url(asset:media.unused);}
        """)
    static final class Additional {}

    @CssImport(value = AssetFixtures.Media.class, name = "media")
    @CssImport(value = Fonts.class, font = "heading", name = "title")
    @CssImport(value = Fonts.class, font = "unused", name = "spare")
    @Css("""
        background:image-set("asset:media.icon?variant=1#shape" 1x,url(asset:media.icon) 2x);
        font:italic 700 2rem/1.2 title,sans-serif;
        &.unused {background:url(asset:media.unused);font-family:var(--unusedFont,spare);}
        """)
    abstract static class Style {
        static Style get() {
            return CssAssetStyles_Styled_Style.get();
        }

        abstract String style();

        abstract String unused();
    }
}
