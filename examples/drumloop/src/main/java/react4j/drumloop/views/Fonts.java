package react4j.drumloop.views;

import react4j.css.Asset;
import react4j.css.Assets;
import react4j.css.CssImport;
import react4j.css.GlobalCss;

@CssImport(value = Fonts.Media.class, name = "media")
@GlobalCss("""
    @font-face {
      font-family: righteous;
      font-style: normal;
      font-weight: 400;
      font-display: swap;
      src: url("asset:media.righteous") format("truetype");
    }
    """)
abstract class Fonts {
    @Assets
    abstract static class Media {
        @Asset("Righteous-Regular.ttf")
        abstract String righteous();
    }
}
