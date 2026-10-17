package com.example.global;

import org.jspecify.annotations.NullMarked;
import react4j.css.GlobalCss;

@NullMarked
@GlobalCss("""
    :root { --accent: blue; }
    body > button { color: var(--accent); }
    """)
public final class Defaults {}
