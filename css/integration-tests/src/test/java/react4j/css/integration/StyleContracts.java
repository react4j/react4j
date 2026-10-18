package react4j.css.integration;

import react4j.css.CssAnimation;
import react4j.css.CssCustomProperty;
import react4j.css.CssId;

public final class StyleContracts {
    private StyleContracts() {}

    public interface Button {
        String style();

        String success();

        String cancel();

        default String forState(StyleState state) {
            return StyleState.SUCCESS == state ? success() : cancel();
        }
    }

    public interface Symbols<T> {
        String style();

        @CssCustomProperty
        String foreground();

        @CssAnimation(name = "pulse")
        String animation();

        @CssId
        T control();

        @CssId(name = "mask")
        String filterId();
    }

    public interface Defaults {
        @CssCustomProperty
        String accent();
    }

    public interface Pulse {
        @CssAnimation
        String name();
    }
}
