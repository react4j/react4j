package demo;
import org.realityforge.css.Css;
class Outer {
    @Css("""
        color: red;
        """)
    private static abstract class Button { abstract String style(); }
}
