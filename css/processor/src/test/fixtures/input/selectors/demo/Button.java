package demo;
import org.realityforge.css.Css;
@org.jspecify.annotations.NullMarked
@Css("""
    /* .fake is not an accessor */
    content: ".fake";
    &:is(.success, :not(.cancel))[data-label=".fake"] { color: green; }
    &:where(.cancel) { color: red; }
    color: blue;
    """)
abstract class Button {
    abstract java.lang.String style();
    abstract String success();
    abstract String cancel();
}
