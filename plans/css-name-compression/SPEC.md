# CSS Name Compression Spec

## Source

- Outcome authority: user asked to follow up with implementation of “Compression and source maps” from thread `01a148ba-06d0-7d91-a36e-426a057c4e12`, using grill-me then structured delivery. The full interview's final shared-understanding confirmation was received on 2026-10-19. Earlier direction authorizes pushing delivery to existing PR112; no merge.
- Scope: accepted interview answers and corrections include effective-declaration inheritance, stable authored global names, selected ID imports and provider liveness. No human plan checkpoint was requested.
- Evidence: current `CssResource` v3 only inventories getter values; `CssLink` v1 binds those values to catalogue/mode/JS digest. `StyleCompiler` already preserves symbolic values through one JVM Closure compilation and lowers after optimization. The bundler independently prunes variants. `SourceMap.project` handles kept spans, not replacements. Companion J2CL maps omit generated Java content.
- Inheritance evidence: main React4j and Sting use normal Proton `ElementsUtil.getMethods`; current CSS code merges ancestor annotations and must change. Keep its independent signature machinery.
- Existing remote Verify run 39129634424: 37/38 targets passed; assembled processor suite timed out at 300 seconds with repeated SLF4J initialization output and no assertion failure in the workflow log. Cause is not yet established.

## Problem

Readable generated names increase production output. Applications need shorter names without breaking CSS/JavaScript agreement, imported identities, intentional public names or debugging. Existing interface metadata semantics also differ from the suite's conventions.

## Required outcome

Optimized applications use one deterministic checked name assignment across JavaScript and all CSS variants. Authors can deliberately retain names; IDs can be shared explicitly. Maps and a reverse-name artifact explain the emitted result. Source and binary authoring contracts agree.

## Scope

- In scope: [CSS names](../../docs/specs/css-names.md), [CSS bindings](../../docs/specs/css-bindings.md), and affected [liveness](../../docs/specs/css-liveness.md) requirements; example migrations; permanent docs; full delivery verification.
- Out of scope: general CSS/value minification, assets/font-face, font-family shortening, persistent names across releases, per-chunk allocation, [exact token mapping](../../docs/deferred/css-identifier-token-mapping.md), [JS-to-authored-CSS mapping](../../docs/deferred/javascript-authored-css-mapping.md).

## Constraints

- JVM-only; one Closure compilation; preserve independent CSS variants and existing conservative specificity/cascade/native-nesting behavior.
- Explicit package-local Java/Closure sources, no globs; Yoda equality; leave `etc/secrets` untouched.
- Hard cut of unshipped metadata and inheritance behavior; no compatibility machinery. Preserve Java generics/throws/Object/default correctness, singleton/facade rules, shading and optional Palantir formatting.
- Preserve current keyframe placement: component-local `@keyframes` and standalone `@Keyframes`; this work does not introduce local keyframes inside `@GlobalCss`.
- Primary implements sequentially. Independent reviewers are read-only. Full gate and existing root coverage thresholds (95% lines/85% branches) must pass without weakening policy.

## Requirements

- R1: Apply ordinary React4j effective-declaration metadata semantics in source and binaries while retaining Java signature correctness (bindings 1–2).
- R2: Expose CLASS-retained `@CssStable` on supported definitions/abstract accessors with owned-symbol scope, modifier-only token behavior and canonical importing policy. Stable globals use authored names in all modes (names 5–7; bindings 3).
- R3: Selected ID imports support aliases/re-exports/source/binaries/all definition kinds/Java-only getters, reject invalid or unused imports, and require checked global ID selectors (bindings 4–7).
- R4: Live importing ID getters activate consumer and provider chain; CSS-only references neither activate providers nor establish DOM presence. Shared stable actual spelling affects only already active owners. Binding identity, composition and provider semantics remain distinct when different getters return equal readable text (liveness 4,6).
- R5: Compress every generated symbol, including CSS-only names, by default for ADVANCED. Retain readable development/BUNDLE/library/JVM behavior and provide a separate disable switch and optional per-link prefix (names 1–3).
- R6: One deterministic collision-safe table applies to all getters/CSS occurrences and all variants. Preserve unchanged/raw names and report late variant collisions. Canonical identities survive aliases; no whole-text replacement (names 2–4,7).
- R7: Emit one complete deterministic reverse-name artifact outside runtime assets and bind policy/table/catalogue/mode to actual JS. Reject invalid/mismatched evidence (names 10–11).
- R8: Preserve authored CSS anchor mappings/content through pruning and replacement; supply generated CSS-binding Java content in JS maps with existing origins (names 8–9).
- R9: Deliver migrated examples, source/binary/golden/unit/integration/browser tests, permanent documentation, full gate, simplify and independent implementation review, cleanup and push PR112.

## Acceptance criteria

- AC1 (R1): Source/binary variations prove untouched annotations survive, overrides use their own defaults/kind/name, ordinary sibling selection has no CSS-only conflict, helpers/re-abstraction behave correctly, and generic/covariant/throws/Object/default/facade regressions still pass.
- AC2 (R2): JVM and generated outputs prove stable type/method/interface/CSS-only/modifier/import behavior and authored global spelling; invalid placements and contradictory explicit importing stability fail meaningfully.
- AC3 (R3,R4): Processor positives/negatives and real links cover source/header providers, aliases/cycles/namespaces, Java-only IDs, keyframe fragment IDs, unused imports, unbound globals, eliminated/live getters, active shared stable IDs and inactive same-spelling owners. Equal-readable getters with different canonical providers and different symbol kinds/stability policies activate and lower their own binding only.
- AC4 (R5,R6): Real J2CL/Closure/bundler/browser modes show JS/CSS agreement for all four kinds, root+modifier values, CSS-only symbols, imports and independent variants. Compression disabled preserves pruning; prefixes isolate independent links. Identical inputs reproduce identical tables.
- AC5 (R6,R7): Allocation tests and actual links cover unchanged reservations, grammar/keywords, stable duplicates, conflicting raw/shared names, forged/stale/incompatible reports, and complete reverse origins including dead candidates. Runtime assets exclude the reverse artifact.
- AC6 (R8): Query actual maps before/inside/after length-changing edits and pruning, including tabs, UTF-16, CRLF, nested selectors, imported references and keyframes/property/fragment edits. CSS resolves to authored Java; JS resolves to generated binding Java with complete content. Assert locations/content, not merely valid JSON.
- AC7 (R9): Existing gate including goldens, coverage, browser, consumer, docs and release validation passes; processor suite timeout has an evidence-based disposition; simplify/review pass; plan removed only after promoted knowledge and evidence are preserved; commit/push delivery without merging.

## Significant decisions

| ID | Decision | Rationale | Impact | User verification |
| --- | --- | --- | --- | --- |
| D1 | Lower names at existing symbolic boundary; allocate once per link | Inlining is already handled; bundles must agree with emitted JS | One JVM compilation and common variants | Browser agreement, inlined getter and variant tests |
| D2 | Consume `--define=react4j.css.compress_names=false` and optional string `react4j.css.name_prefix` from normal `defs` | Matches existing linker configuration; no parallel Bazel flag model | Selectable per link; only the recognized CSS defines are exempted from unknown-define warnings | Compression-off/prefix/type-error tests |
| D3 | Stable global owned names are authored; others remain scoped | Supports intentional external global contracts | Same JVM/development/optimized values; shared names may cascade | Stable global examples and same-name-owner tests |
| D4 | Effective declaration owns metadata, using normal Proton selection | Matches existing suite conventions | Overrides may erase/change metadata; no special diamond conflict | Source/binary inheritance tests |
| D5 | All global ID selectors are checked; import usage is separate from DOM liveness | Compression requires identity; references do not create elements | Existing raw global IDs must migrate; unused selected imports fail | Example migration and presence tests |
| D6 | Deterministic allocation may renumber across application changes; optional prefix provides explicit isolation | Avoids persistent mapping machinery without an accepted use case | Independent links need distinct prefixes for isolation | Determinism and independent-link tests |
| D7 | Preserve current CSS anchors and generated Java JS origins | Fits the accepted debugging guarantees | Exact token and cross-language attribution remain explicitly deferred | Real map position/content queries |

## Technical decisions

- Follow the [symbolic compilation ADR](../../docs/adr/0001-css-symbol-liveness.md). Extend versioned resource metadata to inventory individual owned symbols, canonical imported identities, stable policy and getter composition; include CSS-only candidates. Distinguish ID provider requirements activated by live getters from animation/property CSS dependencies.
- Preserve each getter's identity through symbolization, rather than matching/coalescing solely by returned string. Add required canonical binding metadata during the stable-name slice and identify generated methods precisely before optimization. Coalescing is allowed only if canonical composition and provider activation semantics are identical; avoiding coalescing is simpler. Subsequent compression rebuilds each binding from its own symbols even when readable spellings coincide.
- Keep signature validation in CssMethods; select annotation metadata from normal Proton method collection. Update every affected sticky-inheritance test/fixture and README promise.
- Resolve stable spelling in processor canonical owner bindings so JVM and separately compiled consumers agree. A class-derived standalone `@Keyframes` animation has no authored animation name and keeps its scoped name when stable.
- Selected IDs use `@CssImport(id="control", name="alias", value=Owner.class)`; explicit empty selections are invalid, `id` and `property` mutually exclusive. Alias namespace is independent. Existing raw external URL semantics remain unchanged; the new binding requirement covers global ID selectors.
- Pass resolved `defs` through a configuration-rule string-list attr (including selects), validate and consume the exact CSS defines in compiler options as well. Avoid new parallel Bazel switches. Unknown unrelated defines retain normal warnings.
- Rebuild multi-token getters from mapped symbols. Use parser/token-aware CSS edits, after reachability operates on readable identities. Reserve unchanged catalogue names before allocation; check extra variant raw/shared names against immutable assignments, never remap per variant.
- Extend evidence with the full mapping, policy and origins; use the report as the public reverse-name JSON if its schema remains clear. Keep it out of runtime filegroups/assets. Hard-cut old unshipped schema versions.
- Replacement-aware map projection preserves UTF-16 coordinates and existing unmapped spans. Enrich only actual catalogue-matched generated binding companion maps, following their listed Java source paths rather than guessing from nested binary names. Use public Closure input-source-map APIs before normal map reset/composition.

## Testing decisions

- Preserve meaningful existing processor and pruning regression suites. Add focused unit matrices, source/binary compilation and generated goldens; do not mock the symbolic compiler boundary.
- Extend SaveButton integration as needed to exercise all name kinds, stable globals/imports, prefix/off/development variants and reverse artifacts in actual Chromium.
- Diagnose baseline processor timeout using test-level timing/reproduction before choosing bounded test partitioning, sizing or a root implementation fix. Do not hide functional failure or weaken coverage.
- Validate maps using consumers at concrete emitted positions, including positions after replacements. Independently verify report/JS/catalogue mismatch failures and deterministic assignment.
- Run narrow tests per task and `tools/check.sh` for delivery; review exact diff, simplify, promote knowledge, independent review, then closeout/push.

## Open questions

None requiring user direction. Allocation ordering, schema field layout and bounded parser edit representation are implementation choices constrained above.
