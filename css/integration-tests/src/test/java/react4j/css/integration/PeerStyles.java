package react4j.css.integration;

import react4j.css.Css;

@Css("""
    border: 1px solid blue;
    """)
abstract class PeerStyles {
    abstract String style();
}
