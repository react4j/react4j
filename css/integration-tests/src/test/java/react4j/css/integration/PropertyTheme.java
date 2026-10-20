package react4j.css.integration;

import react4j.css.CssImport;
import react4j.css.GlobalCss;

@CssImport(value = SharedDefaults.class, property = "accent", name = "ink")
@GlobalCss("""
    body { --ink: red; }
    """)
public final class PropertyTheme {}
