package react4j.css.integration;

import react4j.css.Css;
import react4j.css.CssImport;

@CssImport(value = PropertyTheme.class, property = "ink", name = "foreground")
@CssImport(value = SharedPulse.class, name = "pulse")
@Css("""
    color: var(--foreground);
    filter: url(#mask);
    &#control { opacity: .8; }
    """)
abstract class SymbolStyles implements StyleContracts.Symbols<String> {
    static SymbolStyles get() {
        return Styled_SymbolStyles.get();
    }
}
