package demo;
import org.realityforge.css.Css;
class Outer {
    @Css("""
        color: red;
        """)
    abstract class Button { abstract String style(); }
}
