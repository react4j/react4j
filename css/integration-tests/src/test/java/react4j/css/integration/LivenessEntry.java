package react4j.css.integration;

import jsinterop.annotations.JsMethod;
import react4j.css.Css;
import react4j.css.CssId;
import react4j.css.GlobalCss;
import react4j.css.UtilityCss;
import react4j.css.UtilityCssLibrary;
import react4j.css.UtilityStyles;

public final class LivenessEntry {
    private LivenessEntry() {}

    @JsMethod
    public static String classes() {
        return throughInterface(ButtonStyles.get()) + " " + MapStyles.get().active();
    }

    @JsMethod
    public static String utilityClasses() {
        return UtilityStylesContract.instance().active();
    }

    interface UtilityStylesContract {
        @UtilityCss("gap-2 flex text-red")
        String active();

        static UtilityStylesContract instance() {
            return LivenessEntry_Styled_UtilityBindings.get();
        }
    }

    @UtilityCssLibrary("""
        .flex { display:flex; }
        .gap-2 { gap:8px; }
        .text-red { color:rgb(201, 21, 41); }
        .never-selected { outline-width:37px; }
        """)
    static class Utilities {}

    @UtilityStyles(Utilities.class)
    abstract static class UtilityBindings implements UtilityStylesContract {
        abstract String style();
    }

    private static String throughInterface(StyleContracts.Button styles) {
        return styles.success();
    }

    @GlobalCss("""
        .ordinary, #bound { & .child { color: red; } }
        .ordinary .child { color: blue; }
        #negative { :not(&) { outline-width: 11px; } }
        .ordinary, #bound { @media screen { & .child { color: red; } } }
        #negative { @supports (display: grid) { :not(&) { outline-width: 11px; } } }
        """)
    abstract static class NestingDefaults {
        @CssId
        abstract String bound();

        @CssId
        abstract String negative();
    }

    @Css("""
        &[data-label='prefix	 suffix'] { letter-spacing: 3px; }
        &.dead { content: '😀'; }
        @media screen {
          &.dead,
          &.active { color: rgb(1, 2, 3); }
        }
        """)
    abstract static class MapStyles {
        static MapStyles get() {
            return LivenessEntry_Styled_MapStyles.get();
        }

        abstract String style();

        abstract String dead();

        abstract String active();
    }
}
