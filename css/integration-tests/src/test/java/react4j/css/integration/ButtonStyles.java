package react4j.css.integration;

import react4j.css.Css;

@Css("""
    color: white;
    &.success { background-color: green; }
    &.cancel { background-color: red; }
    @keyframes pulse { to { opacity: 0.5; } }
    &:hover { animation-name: pulse; }
    """)
abstract class ButtonStyles {
    static ButtonStyles of() {
        return new Styled_ButtonStyles();
    }

    final String forState(StyleState state) {
        return StyleState.SUCCESS == state ? success() : cancel();
    }

    abstract String style();

    abstract String success();

    abstract String cancel();
}
