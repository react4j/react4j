package react4j.css.integration;

import react4j.css.Css;
import react4j.css.CssImport;

@Css("""
    border: 1px solid blue;
    animation-name: pulse;
    &:hover + :css(label) { color: blue; }
    """)
@CssImport(name = "pulse", value = SharedPulse.class)
@CssImport(name = "label", value = SharedLabel.class)
abstract class PeerStyles {
    abstract String style();
}
