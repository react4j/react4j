package demo;
import org.realityforge.css.Css;
@org.jspecify.annotations.NullMarked
@Css("""
    color: red !important;
    """)
abstract class Button {
    abstract java.lang.String style();

}
