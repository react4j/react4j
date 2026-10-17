package react4j.css.integration;

import react4j.css.Css;
import react4j.css.CssImport;

@Css("""
    border: 1px solid blue;
    animation-name: pulse;
    """)
@CssImport(name = "pulse", value = SharedPulse.class)
abstract class PeerStyles {
    abstract String style();
}
