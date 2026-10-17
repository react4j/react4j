package demo;

import org.jspecify.annotations.NullMarked;
import react4j.css.Css;

@NullMarked
@Css("""
    /* .fake is not an accessor */
    content: ".fake";
    &:is(.success, :not(.cancel))[data-label=".fake"] { color: green; }
    &:where(.cancel) { color: red; }
    color: blue;
    """)
abstract class Button {
    abstract String style();

    abstract String success();

    abstract String cancel();
}
