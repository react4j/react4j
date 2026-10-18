package react4j.css.integration;

import java.io.IOException;
import react4j.css.CssAnimation;
import react4j.css.CssCustomProperty;
import react4j.css.CssId;

public final class StyleContracts {
    private StyleContracts() {}

    public interface GenericRoot {
        <T> String style();
    }

    public interface ThrowingRoot {
        String style() throws IOException;
    }

    // Deliberate collisions exercise generated references through separately compiled header jars.
    public interface Button extends GenericRoot, ThrowingRoot {
        int Internal = 1;

        class Styled_ButtonStyles {}

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
        int Internal = 1;
        int Styled_SharedDefaults = 2;

        @CssCustomProperty
        String accent();
    }

    public interface Pulse {
        class Styled_SharedPulse {}

        @CssAnimation
        String name();
    }
}
