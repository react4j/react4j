package react4j.css.integration;

import jsinterop.annotations.JsMethod;
import react4j.css.Css;

public final class LivenessEntry {
    private LivenessEntry() {}

    @JsMethod
    public static String classes() {
        return throughInterface(ButtonStyles.get()) + " " + MapStyles.get().active();
    }

    private static String throughInterface(StyleContracts.Button styles) {
        return styles.success();
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
