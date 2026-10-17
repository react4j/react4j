package react4j.css.example;

import react4j.css.Css;

@Css("""
    color: var(--on-accent);

    &.success {
      border: 3px solid var(--success);
    }
    """)
abstract class Status {
    static Status of() {
        return new Styled_Status();
    }

    abstract String style();

    abstract String success();
}
