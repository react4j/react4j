package react4j.css.integration;

import react4j.css.GlobalCss;

@GlobalCss("""
    body { --defaults: shared; --accent: red; }
    """)
public abstract class SharedDefaults implements StyleContracts.Defaults {
    public static SharedDefaults get() {
        return react4j.css.integration.Styled_SharedDefaults.get();
    }
}
