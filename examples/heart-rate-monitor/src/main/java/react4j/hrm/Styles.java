package react4j.hrm;

import react4j.css.Css;

final class Styles {
    static final Container CONTAINER = new Styled_Styles_0024_Container();
    static final Panel PANEL = new Styled_Styles_0024_Panel();
    static final Heart HEART = new Styled_Styles_0024_Heart();

    private Styles() {}

    @Css("""
        display: flex;
        justify-content: center;
        """)
    abstract static class Container {
        abstract String style();
    }

    @Css("""
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: space-between;
        """)
    abstract static class Panel {
        abstract String style();
    }

    @Css("""
        width: 10em;
        """)
    abstract static class Heart {
        abstract String style();
    }
}
