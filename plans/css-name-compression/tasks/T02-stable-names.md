# T02 — Stable names in every mode

- Status: `pending`
- Blocked by: T01
- Spec coverage: R2; AC2.

## Delivers

Authors can mark definitions and abstract accessors stable. Scoped values remain readable and stable globals expose authored names consistently on the JVM and in generated CSS, including imported canonical symbols.

## Acceptance criteria

- [ ] CLASS-retained marker is shipped by core Java/J2CL libraries and is validated on supported definition types/abstract accessors, including interface contracts and overrides.
- [ ] Definition-level policy covers owned CSS-only symbols; modifier policy covers only its token. Root, ID, property, animation, global and standalone keyframe cases have explicit tests.
- [ ] Canonical owner policy survives imports, aliases and separately compiled providers. Consumers cannot stabilize foreign symbols via definition-level annotation; contradictory explicit getter promises fail.
- [ ] Stable global-owned IDs/properties use authored spellings in generated CSS and JVM accessors in every mode. Imported animations keep their provider's policy; standalone keyframes retain their class-derived name. Existing keyframe placement restrictions remain.
- [ ] Stability alone never retains a definition; existing optimized links remain behaviorally correct before compression is added.
- [ ] Preserve individual getter identity and canonical provider semantics through exact generated-method symbolization, extending binding metadata as needed. Equal literals must not merge distinct bindings. Real-link tests distinguish different kinds/policies sharing text; T03 extends these to distinct ID provider chains.

## Validation

- Focused compiler and source/binary processor matrices for legal/illegal placements and policies; golden generation and strict checkers.
- Existing linker/bundler tests and actual links with stable owner/unused-owner and equal-readable distinct-binding cases.
- Format and package-local explicit BUILD validation.

## Evidence

Pending.
