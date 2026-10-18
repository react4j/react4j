package react4j.css.integration;

import react4j.css.Keyframes;

@Keyframes("""
    to { opacity: 0; }
    """)
public abstract class SharedPulse implements StyleContracts.Pulse {
    public static SharedPulse get() {
        return Styled_SharedPulse.get();
    }
}
