package react4j.css.integration;

import react4j.css.Css;

@Css("""
    border: 1px solid blue;
    @keyframes pulse { to { opacity: 0; } }
    animation-name: pulse;
    """)
abstract class PeerStyles {
    abstract String style();
}
