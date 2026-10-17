package demo;

import org.jspecify.annotations.NullMarked;
import react4j.css.Css;

@NullMarked
@Css("""
    @keyframes none {}
    """)
abstract class Button {
    abstract String style();
}
