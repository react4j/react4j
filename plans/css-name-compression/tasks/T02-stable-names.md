# T02 — Stable names in every mode

- Status: `complete`
- Blocked by: T01
- Spec coverage: R2; AC2.

## Delivers

Authors can mark definitions and abstract accessors stable. Scoped values remain readable and stable globals expose authored names consistently on the JVM and in generated CSS, including imported canonical symbols.

## Acceptance criteria

- [x] CLASS-retained marker is shipped by core Java/J2CL libraries and is validated on supported definition types/abstract accessors, including interface contracts and overrides.
- [x] Definition-level policy covers owned CSS-only symbols; modifier policy covers only its token. Root, ID, property, animation, global and standalone keyframe cases have explicit tests.
- [x] Canonical owner policy survives imports, aliases and separately compiled providers. Consumers cannot stabilize foreign symbols via definition-level annotation; contradictory explicit getter promises fail.
- [x] Stable global-owned IDs/properties use authored spellings in generated CSS and JVM accessors in every mode. Imported animations keep their provider's policy; standalone keyframes retain their class-derived name. Existing keyframe placement restrictions remain.
- [x] Stability alone never retains a definition; existing optimized links remain behaviorally correct before compression is added.
- [x] Preserve individual getter identity and canonical provider semantics through exact generated-method symbolization, extending binding metadata as needed. Equal literals must not merge distinct bindings. Real-link tests distinguish different kinds/policies sharing text; T03 extends these to distinct ID provider chains.

## Validation

- Focused compiler and source/binary processor matrices for legal/illegal placements and policies; golden generation and strict checkers.
- Existing linker/bundler tests and actual links with stable owner/unused-owner and equal-readable distinct-binding cases.
- Format and package-local explicit BUILD validation.

## Evidence

- Added CLASS-retained marker and owned symbol inventory (metadata v4) for roots/modifiers/IDs/properties/local and standalone animations, including CSS-only candidates. Global stable names are authored on JVM/CSS; foreign aliases keep canonical policy through source and binary transitive providers.
- Red/green equal-text linker regression: old code reported `Button#id` for the surviving animation getter. Returns now use qualified Java method identities from J2CL companion maps. A direct query of real SaveButton mappings confirmed those identities; malformed/missing/unidentified maps fail closed. No dependence on JS mangling or unique literal text.
- `bazel test` across processor assembled/implementation/unit, internal, linker, bundler and development browser: all 12 targets passed. Full processor suites passed 515 assembled cases (91.3s) and 513 implementation cases (28.6s). After expanding transitive stable-import cases, the stable test class passed again through both assembled and implementation paths.
- Real optimized SaveButton browser passed after the final changes, including inherited renamed `@JsMethod`, stable globals, an unused stable owner, and equal ID/animation text with only the ID getter live. Development browser passed the same behavior with retained definitions. Live IDs no longer activate an equal-named animation/provider.
- `tools/update_goldens.sh css` passed. Audited all 60 changed snapshots: only schema version and owned-symbol inventory changed; prior fields remain identical. Buildifier, Java format check, domain links and diff whitespace checks passed.
- Owned inventory moved into this slice because its stable policy is now observable; T04 consumes it and adds canonical getter composition/allocation. Exact identifier attribution remains deferred.
