package demo;
import org.realityforge.css.Css;
@org.jspecify.annotations.NullMarked
@Css("""
    color: var(--on-accent);
    &:focus-visible { outline: 2px solid var(--focus); }
    &.success { background-color: var(--success); }
    &.cancel { background-color: var(--cancel); border: 2px dotted currentColor; }
    """)
abstract class Button {
    abstract java.lang.String style();
    abstract String success();
    protected abstract String cancel();
}
