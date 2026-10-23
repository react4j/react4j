package react4j.css.example;

import jsinterop.annotations.JsMethod;
import org.jspecify.annotations.NullMarked;
import react4j.css.Asset;
import react4j.css.Assets;
import react4j.css.Css;
import react4j.css.CssImport;
import react4j.css.UtilityCss;
import react4j.css.UtilityCssLibrary;
import react4j.css.UtilityStyles;
import react4j.dom.DOM;
import react4j.dom.ReactDOM;
import react4j.dom.proptypes.html.HtmlProps;
import react4j.dom.proptypes.html.ImgProps;

@NullMarked
public final class AssetsExample {
    private AssetsExample() {}

    @JsMethod
    public static void render(Object container) {
        var media = Media.get();
        ReactDOM.render(
                DOM.div(
                        new HtmlProps().id("card").className(Styles.get().style()),
                        DOM.h1("Checked web assets"),
                        DOM.img(new ImgProps().id("icon").src(media.icon()).alt("Saved")),
                        DOM.img(new ImgProps().id("photo").src(media.photo()).alt("React4j")),
                        DOM.img(new ImgProps()
                                .id("prepared")
                                .src(media.prepared())
                                .alt("Prepared SVG"))),
                container);
    }

    @Assets
    abstract static class Media {
        static Media get() {
            return AssetsExample_Assets_Media.get();
        }

        @Asset("card/icon.svg")
        abstract String icon();

        @Asset("card/react4j.png")
        abstract String photo();

        @Asset("card/prepared.svg")
        abstract String prepared();

        @Asset("card/background.svg")
        abstract String background();

        @Asset("card/unused.svg")
        abstract String unused();

        @Asset("Righteous-Regular.ttf")
        abstract String font();
    }

    @CssImport(value = Media.class, name = "media")
    @UtilityCssLibrary("""
        @layer support {
          @keyframes pulse {from{opacity:.7;}to{opacity:1;}}
          @keyframes unused {to{opacity:.13;}}
          @font-face {font-family:heading;src:url("asset:media.font") format("truetype");font-weight:400;font-style:normal;font-display:swap;}
          @font-face {font-family:unused;src:url("asset:media.unused");}
        }
        :root {--motion:pulse 100s linear infinite;--animation:var(--motion);--family:heading;--font:var(--family);--unused-font:unused;--unused-motion:unused 1s;}
        .heading {font-family:var(--font),sans-serif;}
        .animated {animation:var(--animation);}
        .unused {font-family:var(--unused-font);animation:var(--unused-motion);}
        """)
    static final class UtilityLibrary {}

    @CssImport(value = Media.class, name = "media")
    @UtilityStyles(UtilityLibrary.class)
    @Css("""
        padding: 24px;
        background-image: url("asset:media.background");
        &.unused { background-image: url("asset:media.unused"); }
        """)
    abstract static class Styles {
        static Styles get() {
            return AssetsExample_Styled_Styles.get();
        }

        @UtilityCss("heading animated")
        abstract String style();

        abstract String unused();
    }
}
