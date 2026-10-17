package demo;

import react4j.css.Css;

class Outer {
    @Css("""
        color: red;
        """)
    private abstract static class Button {
        abstract String style();
    }
}
