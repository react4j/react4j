# T05 — Compress every symbol and protect variants

- Status: `complete`
- Blocked by: T04
- Spec coverage: R5–R7 remaining kinds/occurrences/collisions; AC4–AC5.

## Delivers

The shared table shortens IDs, animation names and custom properties, including CSS-only candidates, and detects collisions introduced by independently bundled global/shared CSS.

## Acceptance criteria

- [x] Canonical IDs update getters/selectors/fragment references; animation names update declarations/shorthand/keyframe headers without touching grammar keywords; properties update declaration names/var references/supports and selected provider aliases.
- [x] Every occurrence resolves through canonical inventory; ordinary authored global selectors/raw shared CSS/URLs/unrelated strings and values retain spelling. Stable imported names obey owner policy.
- [x] Prefix applies to all compressed kinds with required `--` property syntax. Deterministic individual-token names avoid grammar ambiguities and unchanged known-name collisions.
- [x] Variants share assignments regardless of roots/shared inputs; conflicting additional raw/shared names fail clearly without variant-specific remapping. Independent prefixes avoid cross-link generated-name collisions.
- [x] Reverse artifact covers unused/all-kind candidates, shared stable-name multiple origins and relevant getters; tampered/foreign settings/table/evidence are rejected against actual JS/catalogue.
- [x] Liveness still runs against readable canonical identities, with conservative selectors/native nesting, property/animation dependencies and imported ID provider distinctions preserved.

## Validation

- Unit/parser/reachability matrices for all occurrence forms, escaped/unsupported syntax boundaries, keywords, stable duplicates, provider chains and collision failures.
- Real J2CL/Closure/bundler/browser assertions for all four kinds in compressed/off/prefixed/development modes and two variants sharing one JS.
- Deterministic identical-input output comparison and exact reverse-origin checks; focused map queries for nonclass replacements.

## Realization notes

- Preserve canonical identity for CSS occurrences, not just getters: a stable authored ID/property can equal another definition's readable scoped name, and aliases can have different provider chains. A text-to-name dictionary cannot distinguish these cases. Use unique per-local-alias intermediate names while compiling, resolve parsed occurrences to canonical symbols/provider requirements in processor metadata, and emit ordinary readable CSS. This is generated-token identity metadata, not the deferred exact authored token mapping.
- Project occurrence offsets through pruning before final renaming. Typed references retain the existing distinction between classes/IDs proving DOM presence and property/animation resources requiring providers. Keep selector/fragment-only IDs from activating their providers.
- Reuse the existing parser/animation validation rather than invent a second shorthand grammar. Reserve/check names by parsed context; preserve strings and external URLs. Allocate all kinds in the common link table, then use recorded canonical occurrences for edits.

## Evidence

- Resource v7 records typed generated-token occurrences, canonical owners and per-occurrence provider requirements. Per-alias intermediate names preserve identity when stable global names equal another definition's readable scoped names. Getter requirements cover selected property/animation providers as well as full ID chains. Removed the obsolete string-keyed provider index and shallow pruning result wrapper.
- All four kinds, including CSS-only names, allocate in the common table. Properties keep `--`; animation candidates reject case-insensitive grammar keywords. Parser-derived reservations protect known authored names, and raw/shared variant collisions fail against the immutable assignment. Unit/CLI comparisons prove deterministic outputs and preserve multiple stable origins/getters.
- A source processor regression distinguishes equal-readable ID/property occurrences and their canonical providers. Graph and real browser regressions show a removed modifier's equal-spelling property alias does not activate its provider or unrelated global declarations. Source/binary ID, property and animation matrices remain green.
- Parsed token tests cover selectors/functions/native nesting, declarations/var/calc/supports, shorthand/header names, same-document fragments, CSS escapes and percent-encoded fragments without plus-to-space decoding. Strings/external URLs stay unchanged; raw source spans and decoded names are separate. Bundle-level tests prove collisions fail without remapping and prefixes/compression-off work.
- All three optimized Chromium suites passed with class/ID/animation/property assertions for default, disabled and prefixed naming, including independently rooted themes. All three development suites passed too. Both application and test browser wrappers forward normal compiler defs.
- `tools/update_goldens.sh css` passed 527 assembled cases (111.9s). Audited 68 snapshots: 60 metadata files and eight maps; generated readable CSS and Java are unchanged. All 2,560 UTF-16 lookups across changed maps preserve prior authored anchors/content. Focused nonclass replacement queries also pass before/inside/after edits.
- Final broad focused run passed all 14 targets: full implementation suite, processor units, internal, linker, bundler, integration and development browser modes. Additional escaped-fragment tests and final optimized rebuilds passed. Integration assertions query exact parsed class tokens, avoiding compressed `.c` matching raw `.child`. Domain links, formatter, buildifier and diff checks passed.

- Implementation review round 1 identified attribute-reference and escaped grammar collision gaps. Fixes reserve literal class/ID/IDREF/fragment attribute names (including lists, CSS escapes and ASCII i flags), check all six attribute operators against finite assigned/readable names, and decode animation/var/url contexts while retaining raw spans. Attribute values remain outside managed rewrite occurrences. Scoped var()/fragment peers use the same grammar recognition; source processor and actual bundle regressions cover those paths. Ten focused targets passed, including the actual 526-case implementation suite. A subsequent complete gate passed with all coverage thresholds intact. A final refinement explicitly allows broad comparisons whose matches are unchanged; its four focused targets pass, and the complete gate is rerunning for the exact final tree.
