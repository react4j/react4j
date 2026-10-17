package demo;

import org.jspecify.annotations.NullMarked;
import react4j.css.Css;

@NullMarked
@Css("""
    @media (width > 1px) { color: red; }
    """)
abstract class Button {
    abstract String style();
}
