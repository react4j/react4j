package com.example.shared;

import org.jspecify.annotations.NullMarked;
import react4j.css.Keyframes;

@NullMarked
@Keyframes("""
    from { opacity: 0.4; }
    to { opacity: 1; }
    """)
public abstract class Pulse {}
