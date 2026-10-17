package demo;

import org.jspecify.annotations.NullMarked;
import react4j.css.Css;

@NullMarked
@Css("""
    color: red;
    """)
abstract class Button extends Base {
    abstract String style();
}
