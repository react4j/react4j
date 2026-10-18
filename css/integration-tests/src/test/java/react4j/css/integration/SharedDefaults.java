package react4j.css.integration;

import react4j.css.CssCustomProperty;
import react4j.css.GlobalCss;

@GlobalCss("""
    body { --defaults: shared; --accent: red; }
    """)
public abstract class SharedDefaults {
    public static SharedDefaults get() {
        return Styled_SharedDefaults.get();
    }

    @CssCustomProperty
    public abstract String accent();
}
