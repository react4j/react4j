package demo;
import org.realityforge.css.Css;
@org.jspecify.annotations.NullMarked
@Css("""
    content: "\141\s\\a";\
    color: red;
    &:hover { opacity: 0.5; }
    """)
abstract class Button {
    abstract java.lang.String style();

}
