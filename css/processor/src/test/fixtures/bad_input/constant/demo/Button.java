package demo;

import org.jspecify.annotations.NullMarked;
import react4j.css.Css;

@NullMarked
@Css(Button.CSS)
abstract class Button {
    static final String CSS = "color: red;";

    abstract String style();
}
