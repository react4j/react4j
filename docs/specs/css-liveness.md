# CSS Liveness Specification

## Purpose

Reduce generated CSS for a [Complete link](../glossary/README.md#complete-link) while preserving the behavior of reachable styles.

## Requirements

1. Development builds and libraries without a Complete link retain every component and generated binding, together with explicitly selected global styles and their dependencies. A global definition without a generated binding is included only when selected explicitly or required by a retained definition.
2. An optimized Complete link omits CSS belonging to unused [Style owner](../glossary/README.md#style-owner) definitions and removes selectors proven unable to match. Uncertain selectors remain. Retained rules preserve specificity, cascade order and declaration values.
3. Liveness follows generated accessor values through inlining, interface dispatch and ordinary Java helpers. A modifier value keeps both its component root and modifier [CSS symbol](../glossary/README.md#css-symbol) live.
4. Every live checked accessor activates its Style owner and that owner's unconditional global inclusions, including when the accessor returns an imported CSS symbol. A live importing ID accessor also activates its canonical provider chain. Activation alone does not make the owner's other CSS symbols live.
5. Retained rules keep the animation, custom-property and owned font-family dependencies they require, including intermediate property providers and their overrides. Dependencies used only by removed rules may disappear. A retained family keeps all its declared faces; unresolved dynamic family usage conservatively keeps possible family dependencies only while that usage survives.
6. Referencing another component in a selector does not establish that component's presence. An ID selector or fragment reference does not establish the presence of a matching DOM element or activate its provider. A live ID value establishes possible presence for matching actual names across already active definitions, including intentionally shared stable global names; equal spelling alone does not activate another definition.
7. A [Liveness report](../glossary/README.md#liveness-report) can serve multiple [CSS variant](../glossary/README.md#css-variant) bundles. Each variant applies its own explicit global roots and shared CSS without changing the JavaScript link's liveness evidence.
8. A Liveness report must match the actual linked JavaScript, style definitions, compression policy and [CSS name table](../glossary/README.md#css-name-table). Missing, malformed or mismatched evidence fails the build before a bundle is emitted; it must not be interpreted as an empty live set or as an unlinked library.
9. Generated names used by a Complete link originate from checked accessors compiled within that link. Constructing or hardcoding generated names outside those accessors is unsupported for pruning.
10. A live [Utility style](../glossary/README.md#utility-style) accessor activates its required library and establishes possible presence for its declared managed classes and local identity markers. The library's entire managed class inventory participates in absence proofs, including classes never exposed by a Java accessor. External class names are not proven absent through managed accessor analysis.
11. Retained utility rules keep the property registrations, static variable chains, animations, fonts and files they require. A selected variable-only utility retains its assignments and their required providers. Dependencies used only by removed rules may disappear. A container or anchor reference alone neither establishes its target's presence nor retains a utility that would establish it.

12. A live checked property accessor keeps its provider and required default definitions or registrations without establishing class presence. Variable alias analysis preserves all potentially applicable definitions, fallback behavior and dependency cycles; it does not replace browser variable evaluation.
13. Native development output retains requested expansions, including unused getters, but does not instantiate unrequested candidates. Optimized output prunes expansions through surviving getters while retaining full owned-name identities for naming and reports. Equal independently compiled candidate requests share one expansion.

## Architectural basis

[Preserve symbolic style values through Closure optimization](../adr/0001-css-symbol-liveness.md) and [bundle CSS variants from one link](../adr/0002-css-variant-bundling.md).

## Related contracts

[CSS names](css-names.md) governs emitted names and JVM values; [CSS source maps](css-source-maps.md) governs authored origins after pruning.
