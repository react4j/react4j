package react4j.css.integration;

import react4j.css.Css;
import react4j.css.CssImport;

@CssImport(SharedDefaults.class)
@Css("""
    color: black;
    """)
abstract class SharedLabel {
    abstract String style();
}
