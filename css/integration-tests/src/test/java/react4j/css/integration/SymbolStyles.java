package react4j.css.integration;

import react4j.css.Css;
import react4j.css.CssAnimation;
import react4j.css.CssCustomProperty;
import react4j.css.CssId;
import react4j.css.CssImport;

@CssImport(value = PropertyTheme.class, property = "ink", name = "foreground")
@CssImport(value = SharedPulse.class, name = "pulse")
@Css("""
    color: var(--foreground);
    filter: url(#mask);
    &#control { opacity: .8; }
    """)
abstract class SymbolStyles {
    static SymbolStyles get() {
        return Styled_SymbolStyles.get();
    }

    abstract String style();

    @CssCustomProperty
    abstract String foreground();

    @CssAnimation(name = "pulse")
    abstract String animation();

    @CssId
    abstract String control();

    @CssId(name = "mask")
    abstract String filterId();
}
