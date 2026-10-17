package demo;

import org.jspecify.annotations.NullMarked;
import react4j.css.Css;

@NullMarked
@Css("""
    &:has(.success) { color: red; }
    """)
abstract class Button {
    abstract String style();

    abstract String success();
}
