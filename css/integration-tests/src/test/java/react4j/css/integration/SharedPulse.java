package react4j.css.integration;

import react4j.css.CssAnimation;
import react4j.css.Keyframes;

@Keyframes("""
    to { opacity: 0; }
    """)
public abstract class SharedPulse {
    public static SharedPulse get() {
        return Styled_SharedPulse.get();
    }

    @CssAnimation
    public abstract String name();
}
