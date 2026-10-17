package demo;

import org.jspecify.annotations.NullMarked;
import react4j.css.Css;

@NullMarked
@Css("""
    color: red;\
    background-color: white;
    &.sucess { color: blue; }
    """)
abstract class Button {
    abstract String style();

    abstract String success();
}
