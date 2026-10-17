package demo;
import react4j.css.Css;
class Outer {
    @Css("""
        color: red;
        """)
    private static abstract class Button { abstract String style(); }
}
