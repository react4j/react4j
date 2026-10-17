package react4j.css.integration;

import react4j.css.Css;
import react4j.css.CssImport;
import react4j.css.CssIncludes;

@Css("""
    border: 1px solid blue;
    animation-name: pulse;
    """)
@CssImport(name = "pulse", value = SharedPulse.class)
@CssIncludes(SharedDefaults.class)
abstract class PeerStyles {
    abstract String style();
}
