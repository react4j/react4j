package react4j.css.integration;

import react4j.css.Css;
import react4j.css.UtilityCss;
import react4j.css.UtilityStyles;

@UtilityStyles(NativeUtilityLibrary.class)
@Css("""
    outline-width: 6px;
    """)
abstract class NativeStyles {
    static NativeStyles get() {
        return Styled_NativeStyles.get();
    }

    abstract String style();

    @UtilityCss("gap-137 gap-2 pulsing")
    abstract String active();

    @UtilityCss("gap-999")
    abstract String dead();
}
