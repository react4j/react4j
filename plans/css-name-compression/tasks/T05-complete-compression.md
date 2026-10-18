# T05 — Compress every symbol and protect variants

- Status: `pending`
- Blocked by: T04
- Spec coverage: R5–R7 remaining kinds/occurrences/collisions; AC4–AC5.

## Delivers

The shared table shortens IDs, animation names and custom properties, including CSS-only candidates, and detects collisions introduced by independently bundled global/shared CSS.

## Acceptance criteria

- [ ] Canonical IDs update getters/selectors/fragment references; animation names update declarations/shorthand/keyframe headers without touching grammar keywords; properties update declaration names/var references/supports and selected provider aliases.
- [ ] Every occurrence resolves through canonical inventory; ordinary authored global selectors/raw shared CSS/URLs/unrelated strings and values retain spelling. Stable imported names obey owner policy.
- [ ] Prefix applies to all compressed kinds with required `--` property syntax. Deterministic individual-token names avoid grammar ambiguities and unchanged known-name collisions.
- [ ] Variants share assignments regardless of roots/shared inputs; conflicting additional raw/shared names fail clearly without variant-specific remapping. Independent prefixes avoid cross-link generated-name collisions.
- [ ] Reverse artifact covers unused/all-kind candidates, shared stable-name multiple origins and relevant getters; tampered/foreign settings/table/evidence are rejected against actual JS/catalogue.
- [ ] Liveness still runs against readable canonical identities, with conservative selectors/native nesting, property/animation dependencies and imported ID provider distinctions preserved.

## Validation

- Unit/parser/reachability matrices for all occurrence forms, escaped/unsupported syntax boundaries, keywords, stable duplicates, provider chains and collision failures.
- Real J2CL/Closure/bundler/browser assertions for all four kinds in compressed/off/prefixed/development modes and two variants sharing one JS.
- Deterministic identical-input output comparison and exact reverse-origin checks; focused map queries for nonclass replacements.

## Evidence

Pending.
