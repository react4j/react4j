package demo;
import org.realityforge.css.Css;
@org.jspecify.annotations.NullMarked
@Css(Button.CSS)
abstract class Button {
    static final String CSS = "color: red;";
    abstract java.lang.String style();

}
