package org.realityforge.css.integration;

import org.realityforge.css.Css;

@Css("""
    color: white;
    &.success { background-color: green; }
    &.cancel { background-color: red; }
    """)
abstract class ButtonStyles {
    static ButtonStyles of() {
        return new Styled_ButtonStyles();
    }

    final String forState(StyleState state) {
        return state == StyleState.SUCCESS ? success() : cancel();
    }

    abstract String style();

    abstract String success();

    abstract String cancel();
}
