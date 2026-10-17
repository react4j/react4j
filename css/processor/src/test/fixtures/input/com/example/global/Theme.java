package com.example.global;

import org.jspecify.annotations.NullMarked;
import react4j.css.CssIncludes;
import react4j.css.GlobalCss;

@NullMarked
@CssIncludes(Defaults.class)
@GlobalCss("""
    @media screen {
      @supports (display: grid) {
        body .panel { display: grid; }
      }
    }
    """)
final class Theme {}
