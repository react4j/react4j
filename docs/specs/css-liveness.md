# CSS Liveness Specification

## Purpose

Reduce generated CSS for a [Complete link](../glossary/README.md#complete-link) while preserving the behavior of reachable styles and their authored origins.

## Requirements

1. Development builds and libraries without a Complete link retain every component and generated binding, together with explicitly selected global styles and their dependencies. Discovery alone does not activate an otherwise unused global definition.
2. An optimized Complete link removes unused style owners and selectors proven unable to match. Uncertain selectors remain. Retained rules preserve specificity, cascade order and declaration values.
3. Liveness follows generated accessor values through inlining, interface dispatch and ordinary Java helpers. A modifier value keeps both its component root and modifier [CSS symbol](../glossary/README.md#css-symbol) live.
4. Every live checked accessor activates its [Style owner](../glossary/README.md#style-owner) and that owner's unconditional global inclusions, including when the accessor returns an imported CSS symbol. Activation alone does not make the owner's other CSS symbols live.
5. Retained rules keep the animation and custom-property dependencies they require, including intermediate property providers and their overrides. Dependencies used only by removed rules may disappear.
6. Referencing another component in a selector does not establish that component's presence. An ID fragment reference does not establish the presence of a matching DOM element.
7. A [Liveness report](../glossary/README.md#liveness-report) can serve multiple [CSS variant](../glossary/README.md#css-variant) bundles. Each variant applies its own explicit global roots and shared CSS without changing the JavaScript link's liveness evidence.
8. Pruning requires compatible evidence matching the link and its style catalogue. Missing, malformed or mismatched evidence fails the build; it must not be interpreted as an empty live set or as an unlinked library.
9. Pruned CSS source maps resolve surviving selectors and declarations to their original authored locations, including after earlier rules are removed.
10. Generated names used by a Complete link originate from checked accessors compiled within that link. Constructing or hardcoding generated names outside those accessors is unsupported for pruning.
11. Generated Java accessors remain usable on the JVM and return ordinary strings. Compiler-only symbols do not appear in shipped JavaScript.

## Architectural basis

[Preserve symbolic style values through Closure optimization](../adr/0001-css-symbol-liveness.md).
