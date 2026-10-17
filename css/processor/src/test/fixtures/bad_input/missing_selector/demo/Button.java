package demo;
import react4j.css.Css;
@org.jspecify.annotations.NullMarked
@Css("""
    /* &.success {} */
    content: "&.success";
    """)
abstract class Button {
    abstract java.lang.String style();
    abstract String success();
}
