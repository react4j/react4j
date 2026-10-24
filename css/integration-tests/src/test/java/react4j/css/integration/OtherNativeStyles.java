package react4j.css.integration;

import react4j.css.UtilityCss;
import react4j.css.UtilityStyles;

@UtilityStyles(NativeUtilityLibrary.class)
abstract class OtherNativeStyles {
    static OtherNativeStyles get() {
        return Styled_OtherNativeStyles.get();
    }

    abstract String style();

    @UtilityCss("gap-137 pulsing")
    abstract String active();
}
