package react4j.css.integration;

import react4j.css.GlobalCss;

@GlobalCss("""
    body { --defaults: explicitly-selected; }
    """)
final class UnselectedDefaults {}
