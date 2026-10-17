package demo;
import react4j.css.Css;
@org.jspecify.annotations.NullMarked
@Css("""
    @media (width > 1px) { &.success { color: green; } }
    &:hover, &.success { &:disabled { opacity: 0.5; } }
    @supports (display: grid) and (color: red) { &.success { display: grid; } }
    """)
abstract class Button {
    abstract java.lang.String style();
    public abstract String success();
}
