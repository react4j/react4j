package react4j.css.integration;

import react4j.css.Css;

@Css("""
    color: white;
    &.success { background-color: green; }
    &.cancel { background-color: red; }
    @keyframes pulse { to { opacity: 0.5; } }
    &:hover { animation-name: pulse; }
    """)
abstract class ButtonStyles implements StyleContracts.Button {
    static ButtonStyles get() {
        return react4j.css.integration.Styled_ButtonStyles.get();
    }
}
