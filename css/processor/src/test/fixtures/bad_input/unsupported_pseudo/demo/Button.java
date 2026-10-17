package demo;
import org.realityforge.css.Css;
@org.jspecify.annotations.NullMarked
@Css("""
    &:has(.success) { color: red; }
    """)
abstract class Button {
    abstract java.lang.String style();
    abstract String success();
}
