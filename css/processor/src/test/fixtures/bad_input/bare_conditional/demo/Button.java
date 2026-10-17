package demo;
import org.realityforge.css.Css;
@org.jspecify.annotations.NullMarked
@Css("""
    @media (width > 1px) { color: red; }
    """)
abstract class Button {
    abstract java.lang.String style();

}
