package com.example.bare_conditional;

import org.jspecify.annotations.NullMarked;
import react4j.css.GlobalCss;

@NullMarked
@GlobalCss("""
    @media (width > 1px) { color: red; }
    """)
final class Button {}
