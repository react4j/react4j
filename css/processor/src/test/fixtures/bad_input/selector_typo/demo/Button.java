package demo;
import org.realityforge.css.Css;
@org.jspecify.annotations.NullMarked
@Css("""
    color: red;\
    background-color: white;
    &.sucess { color: blue; }
    """)
abstract class Button {
    abstract java.lang.String style();
    abstract String success();
}
