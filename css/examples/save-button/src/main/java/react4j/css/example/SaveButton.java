package react4j.css.example;

import react4j.css.Css;

@Css("""
    color: var(--on-accent);

    &:focus-visible {
      outline: 2px solid var(--focus);
    }

    &:disabled {
      opacity: 0.5;
    }

    &.success {
      background-color: var(--success);
    }

    &.cancel {
      background-color: var(--cancel);
      border: 2px dotted currentcolor;
    }
    """)
abstract class SaveButton {
    static SaveButton of() {
        return new Styled_SaveButton();
    }

    abstract String style();

    abstract String success();

    abstract String cancel();
}
