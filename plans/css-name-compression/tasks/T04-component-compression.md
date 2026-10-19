# T04 — Component compression through a real link

- Status: `complete`
- Blocked by: T03
- Spec coverage: R5–R7 class/control/evidence portions; AC4–AC5 corresponding portions.

## Delivers

Component roots and modifiers shorten consistently in actual optimized JS and CSS using one versioned deterministic table, with independently selectable compression and prefix controls and a public reverse artifact.

## Acceptance criteria

- [x] Use the owned/CSS-only inventory and stable policy established in T02; extend getter metadata with canonical symbol composition and hard-cut obsolete schema. Start allocation with component class symbols, retaining other kinds until T05.
- [x] Normal resolved `defs` configure both catalogue and compiler, including select; defaults ADVANCED on, BUNDLE/development off, explicit false preserves optimization/pruning, optional string prefix applies only to compressed names.
- [x] Validate boolean/string types, invalid prefixes and conflicting settings; consume only the exact recognized CSS defines without muting unrelated unknown-define warnings.
- [x] Root and modifier tokens map independently then getters rebuild values. Preserve imports, aliases and inlining; stable class tokens remain readable.
- [x] Preserve canonical binding composition when different getters share readable text; do not merge their compression policies, kinds or activation dependencies.
- [x] Allocate deterministically with reserved names/grammar protection; collision checking uses immutable table. Evidence binds policy/table/JS/catalogue/mode; versioned reverse JSON includes every candidate/accessor/origin and is excluded from browser assets.
- [x] Parser-aware replacements preserve existing CSS anchor maps and shifting positions; never globally replace matching text.

## Validation

- Unit tests for metadata round trips/invalid data, allocation/reservations/determinism, switches/prefix types and tampered evidence.
- Actual development, optimized, optimized-uncompressed and prefixed SaveButton builds; browser root/modifier application, independent variants and reverse artifact inspection.
- Class replacement map queries before/inside/after emitted changes; keep later kinds/maps under explicit T05/T06 ownership.

## Evidence

- Resource v6 records each getter's canonical symbol composition. Link/report v3 carries one deterministic name table, owned/CSS-only candidates, stable policy, canonical origins and all corresponding getters, including dead candidates.
- Normal resolved Closure `defs` feed both configuration and compiler (including selects). Strict policy parsing and actual compiler-option checks reject type errors, duplicates, invalid prefixes and policy mismatches; only the two CSS controls are exempted from unknown-define warnings.
- Root/modifier assignments are independent. The linker computes compiled values once, then lowers surviving symbolic bindings after optimization. A real compiler regression distinguishes equal-readable class and stable ID getters with different canonical compositions/policies.
- Parsed selector edits run after readable reachability. Replacement projection preserves UTF-16 coordinates, authored anchors, declaration mappings, complete source content and unmapped spans. Integration map queries use actual compiled identifiers from the reverse artifact.
- Three optimized SaveButton Chromium suites passed: default compression, compression disabled with pruning retained, and `app-` prefix. Each exercises default/alternate/component-only CSS variants sharing its JS. All three development browser suites also passed. Added them to the full browser gate; reverse reports remain outside runtime assets.
- `tools/update_goldens.sh css` passed all 526 assembled processor cases (108.1s). Audited all 60 snapshots: schema version and canonical getter composition only. Full implementation suite (524 cases), all processor units, internal/name/renaming/map tests, linker, bundler and integration passed across the final focused runs. Domain links, formatter, buildifier and diff checks passed.
- T05 owns all remaining kinds and late raw/shared collision checks. Its token representation must retain canonical occurrence identity when readable spellings coincide, rather than infer identity from emitted text.
