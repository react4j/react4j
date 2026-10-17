package demo;

import org.jspecify.annotations.NullMarked;
import react4j.css.Css;

@NullMarked
@Css("""
    color: blue;
    """)
abstract class Override {
    abstract java.lang.String style();
}
