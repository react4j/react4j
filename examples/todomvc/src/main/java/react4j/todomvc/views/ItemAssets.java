package react4j.todomvc.views;

import org.jspecify.annotations.NullMarked;
import react4j.css.Asset;
import react4j.css.Assets;

@NullMarked
@Assets
public abstract class ItemAssets {
    @Asset("toggle.svg")
    public abstract String toggle();

    @Asset("toggle_checked.svg")
    public abstract String checked();
}
