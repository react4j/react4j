package demo;

import org.jspecify.annotations.NullMarked;
import react4j.css.Css;

@NullMarked
@Css("""
    @keyframes pulse {}
    animation: pulse var(--duration);
    """)
abstract class Button {
    abstract String style();
}
