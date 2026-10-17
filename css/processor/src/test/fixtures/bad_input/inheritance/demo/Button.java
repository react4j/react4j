package demo;
import org.realityforge.css.Css;
@org.jspecify.annotations.NullMarked
@Css("""
    color: red;
    """)
abstract class Button extends Base {
    abstract java.lang.String style();

}
abstract class Base {}
