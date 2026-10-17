package demo;

import org.jspecify.annotations.NullMarked;
import react4j.css.Css;

@NullMarked
@Css("""
    @keyframes pulse {}
    animation: pulse 1s 2s 3s;
    """)
abstract class Button {
    abstract String style();
}
