package demo;

import org.jspecify.annotations.NullMarked;
import react4j.css.Css;

@NullMarked
@Deprecated(forRemoval = true)
@Css("""
    @keyframes pulse { to { opacity: 0.5; } }
    &.active { animation: pulse 1s; }
    """)
abstract class Button {
    @Deprecated
    Button() {}

    abstract String style();

    @Deprecated(forRemoval = true)
    abstract String active();
}
