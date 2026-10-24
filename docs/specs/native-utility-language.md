# Native Utility Language Specification

## Purpose

Define declarative rules for a [Utility CSS library](../glossary/README.md#utility-css-library), authored by agents and selected through checked Java declarations using [Utility candidate](../glossary/README.md#utility-candidate) values.

## Requirements

1. A native source starts with `@utility-rules;` after optional CSS whitespace/comments. The marker selects the grammar before parsing; malformed native input never falls back to finite CSS.
2. All parsing, preparation, binding, expansion and delivery run on the JVM. No application scanner, watcher, executable rule callback, JavaScript generator or additional required process participates.
3. Rule preparation, provider binding, description and explicit batch expansion expose immutable data and perform no implicit I/O. Reusing a checked provider never accumulates prior candidate requests.
4. Sources declare tokens, positive scalar scales, literal utilities, one-parameter utility families and variants. Supporting CSS is limited to keyframes, font faces, property registrations and custom-property initialization.
5. Parameter sources are named tokens, nonnegative decimal scale multipliers, admitted bare values or bracketed arbitrary values. Combined sources have compatible types and disjoint accepted spellings. Explicit negative utilities supply their own meaning.
6. Matching uses an exact literal or the longest declared family prefix. Invalid arguments do not fall through to shorter families. Duplicate heads, source overlaps, literal/family overlaps and hidden token spellings fail provider validation, including unused definitions.
7. Candidate lists split only on HTML whitespace. Colons separate variants only outside balanced groups and strings. In bracket payloads every unescaped underscore becomes a space, including inside strings; escaped underscores remain literal.
8. The value types are integer, number, percentage, length, length-percentage, angle, time, ratio, string, color, box-shadow, image, position, grid-tracks and an+b. Checked CSS structure rejects known wrong dimensions, syntax and unsupported functions. Values cannot inject declarations, selectors or at-rules.
9. Supported numeric math includes calc, min, max and clamp with consistent dimensions. Unitless zero is a length outside math. Browser-dependent variable substitution remains explicit uncertainty; it does not excuse statically invalid structure.
10. Inclusive numeric bounds check all finite tokens and comparable literal/scale values. Scale arithmetic and fixed-ratio conversions are exact. Comparisons requiring browser/layout information or unevaluated CSS math remain deferred and are reported as such; the compiler injects no clamp or runtime evaluator.
11. Holes fill complete admitted value positions. Property names, owned names, operators and whole conditions are static. An an+b hole is permitted only as the complete nth-child argument. Named-token uses receive contextual checks even when unrequested.
12. A variant contains exactly one slot surrounded by selectors or checked media, supports, container or starting-style wrappers, and contributes no declarations. Utilities have no slot. Selectors require one positive guard in their first compound and no rule-level selector list; literal class hooks require explicit permission.
13. Container conditions admit boolean combinations and nesting of standard size features, custom-property style tests and stuck, snapped, scrollable and scrolled features. Length and ratio holes may fill complete matching size-feature values. Style/scroll-state tests are static; unknown features/functions and comma-separated query lists are rejected.
14. Starting-style wrappers take no condition and preserve their written position. Transitions, final values and other browser behavior remain explicit authored CSS.
15. Written variant order determines nesting, with the first variant outermost. Nested container queries remain separate because they may choose different ancestors. Selector specificity and authored declaration order are preserved.
16. Expanded CSS is ordered by variant count, written variant-rank sequence, utility definition order and full candidate spelling in Unicode code-point order. Arbitrary selector variants rank after named variants and use their decoded spelling as a secondary key. Class-list order does not change precedence.
17. Named anchors obey the separate [CSS names contract](css-names.md). Anchor function names and side/size selectors are static; selector percentages are literal. Offset/fallback values may use supported browser expressions. Dynamic name substitutions and statically visible custom-property carriers are rejected.
18. Anchor position functions are admitted only in supported inset declarations. Anchor size functions are admitted only in sizing, inset and margin declarations. Check literal and substituted values, including nested math/fallbacks; custom-property assignments cannot hide their declaration context.
19. Applying container setup, anchor triggers, per-instance scope and panel placement remains explicit application behavior. Queries and valid name references neither prove DOM existence nor instantiate setup utilities. Native browser syntax is not a browser compatibility or placement guarantee.
20. Checked provider descriptors preserve defining-owner bindings, permitted external hooks, stability and a definition/binding fingerprint. Explicit inputs and prepared descriptors support header-only compilation; published providers require a consistent checked descriptor. Unknown versions and stale source/binding snapshots fail clearly.
21. Independent consumers may request previously unseen candidates. Equal contributions deduplicate, differing contributions or provider fingerprints fail, and the finite union of requested candidates reaches linking without enumerating the language.
22. Agent authoring operations and Java compilation share checked behavior and diagnostic codes. Diagnostics expose primary/related origins, available payload spans and decoded offsets, expected sources/types, useful suggestions and browser-dependent checks. Batch failures retain every error and successful peer.
23. Preparation without provider bindings reports pending ownership checks rather than claiming a checked library. Description includes ordered families/wrappers, token entries, infinite source forms and bounds; expansion includes canonical identities, ordering, emitted CSS and origin traces.
24. Generic regex/multi-capture rules, apply macros, generic slash modifiers, arbitrary properties/at-rule variants, executable plugins, synthesized group/peer markers, dynamic owned names and named position-try resources are outside this grammar.

## Architectural basis

[Compile declarative rules on the JVM](../adr/0005-compile-declarative-utility-rules-on-jvm.md) and [publish checked expansions](../adr/0006-publish-checked-utility-expansions.md).

## Related contracts

[Utility CSS](utility-css.md) owns input, cascade and reset behavior. [Bindings](css-bindings.md), [liveness](css-liveness.md), [names](css-names.md), [source maps](css-source-maps.md) and [checked assets](web-assets.md) retain their obligations. The [author reference](../../css/utility-language/README.md) gives concrete syntax and commands.
