package demo;
import react4j.css.Css;
@org.jspecify.annotations.NullMarked
@Css("""
    color: red;
    """)
abstract class Button {
    abstract java.lang.String style();
    private Button() {}
}
