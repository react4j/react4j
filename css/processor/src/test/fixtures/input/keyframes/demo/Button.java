package demo;

import org.jspecify.annotations.NullMarked;
import react4j.css.Css;

@NullMarked
@Css("""
    @keyframes pulse {
      from, 50% { opacity: 0.5; }
      to { opacity: 1; }
    }
    @keyframes linear {
      0% { transform: scale(1); }
      100% { transform: scale(0.9); }
    }
    animation-name: pulse;
    animation-duration: var(--duration);
    &.active { animation: pulse 1s ease infinite, 2s linear linear; }
    @media (prefers-reduced-motion: reduce) {
      &.active { animation: none; }
    }
    """)
abstract class Button {
    abstract String style();

    abstract String active();
}
