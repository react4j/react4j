# T06 — Retain animation and font variable dependencies

- Status: in_progress
- Blocked by: T05
- Spec coverage: R5 (animations/fonts), R6 (imports/assets), R10, R11 (support), R12 (assets); AC6

## Delivers

Standard Tailwind-shaped animation and owned-font utilities work when names are supplied through static custom properties and aliases, including checked font files and optimized removal.

## Acceptance criteria

- [ ] A live animation utility retains its variable chain and keyframes; every resolved animation occurrence, including the name inside a property value, uses the same canonical name.
- [ ] A live font utility follows property aliases to an owned family, keeps all faces of that family and finalizes its checked files. Dead utility-only families/frames/files disappear.
- [ ] Analyze all potentially applicable static definitions without inlining variables or predicting the winning browser cascade.
- [ ] Reject ambiguous/mixed name-carrier uses, including a font-name property also rendered as visible text, with a source/path diagnostic. Do not rewrite identifier-looking data or shorthand keywords by spelling alone.
- [ ] Existing runtime checked animation/property getters, external names, font fallback/shorthand semantics and imported-provider ownership retain their contracts.
- [ ] Dependency pruning removes providers used only by removed declarations and preserves intermediate overrides.

## Validation

- Focused processor/parser tests cover static and aliased animation/font values, multiple scopes, fallbacks, ambiguous mixed uses and dynamic non-name fields.
- Optimized Java/J2CL integration verifies renamed variable values, retained/dead keyframes/faces/assets and correct finalized URLs.
- Browser assertions verify animation-name and font-family computed values with an actual declared font asset.

## Evidence

- pending
