package demo;

import org.jspecify.annotations.NullMarked;
import react4j.css.Css;

@NullMarked
@Css("""
    color: red;
    """)
abstract sealed class Button permits Child {
    abstract String style();
}

@NullMarked
final class Child extends Button {
    String style() {
        return "";
    }
}
