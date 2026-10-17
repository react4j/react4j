package demo;
import org.realityforge.css.Css;
@org.jspecify.annotations.NullMarked
final class Owner {
    @Css("""
        &._ready { color: green; }
        """)
    static abstract class Inner {
        abstract String style();
        abstract String _ready();
    }
}
