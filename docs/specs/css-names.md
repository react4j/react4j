# CSS Names Specification

## Purpose

Keep JavaScript and CSS names consistent when a [Complete link](../glossary/README.md#complete-link) shortens a managed [CSS symbol](../glossary/README.md#css-symbol), while preserving authored origins and intentional public names.

## Requirements

1. An optimized Complete link compresses generated class, ID, animation and custom-property names by default, including owned symbols without Java accessors. Compression can be disabled independently of JavaScript optimization and CSS pruning. Development builds, unlinked libraries and JVM accessors retain readable names.
2. All [CSS variant](../glossary/README.md#css-variant) outputs of one Complete link use the same [CSS name table](../glossary/README.md#css-name-table). Identical inputs and settings produce identical assignments. Application changes may change assignments; compressed names have no cross-release identity guarantee.
3. An optional per-link prefix applies only to compressed names, preserving required custom-property syntax. Distinct prefixes permit independent links to avoid generated-name collisions; links without distinct prefixes have no automatic isolation guarantee.
4. Allocation preserves unchanged managed names and known ordinary names, avoids collisions and CSS grammar ambiguities, and rewrites only resolved symbol occurrences. Ordinary authored global selectors and raw shared CSS retain their spelling. A later CSS variant introducing a conflicting ordinary name fails clearly rather than changing the table.
5. `@CssStable` marks a definition's owned symbols or an abstract accessor's symbol as a [Stable CSS symbol](../glossary/README.md#stable-css-symbol). Definition-level stability includes CSS-only symbols. Stability does not retain otherwise unused CSS. A modifier accessor stabilizes its modifier token; its root follows the root's own policy.
6. Stable component and locally scoped symbols retain their readable scoped spelling. Stable symbols owned by a global definition retain their authored spelling in every build mode and on the JVM. Independent owners may intentionally share stable authored names and normal CSS cascade semantics.
7. A symbol's defining owner controls its spelling and stability through aliases and transitive imports. Consumers cannot change that policy; an explicit stability promise on an importing accessor must agree with its canonical owner. Definition-level stability covers owned symbols only.
8. CSS maps preserve selector and declaration origins in authored Java and complete authored source content after pruning and name replacement, including changes in line and UTF-16 column positions.
9. JavaScript maps preserve origins in generated CSS-binding Java, including the content of those generated sources. Mapping directly to authored CSS and exact identifier-token attribution are outside this contract.
10. One deterministic link-level reverse-name artifact records the complete table, including unused candidates, kinds, canonical identities, readable/authored/compiled spellings, owners and exposing accessors. Shared stable spellings preserve every origin. This artifact is available for debugging without being a browser runtime asset.
11. Link evidence binds the name table and compression settings to the actual JavaScript and style catalogue. Incompatible or malformed evidence fails before emitting a bundle.

## Architectural basis

[Preserve symbolic style values through Closure optimization](../adr/0001-css-symbol-liveness.md).
