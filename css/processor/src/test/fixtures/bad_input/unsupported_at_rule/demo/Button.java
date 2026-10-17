package demo;
import org.realityforge.css.Css;
@org.jspecify.annotations.NullMarked
@Css("""
    @container (width > 1px) { &:hover { color: red; } }
    """)
abstract class Button {
    abstract java.lang.String style();

}
