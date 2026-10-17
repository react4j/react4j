package com.example.import_wrong_kind;

import react4j.css.Css;
import react4j.css.CssImport;

@CssImport(name = "pulse", value = String.class)
@Css("""
    color: red;
    """)
abstract class Button {
    abstract String style();
}
