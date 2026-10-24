# T06 — Deliver checked named anchors and scoped popovers

- Status: `pending`
- Blocked by: `T05`
- Spec coverage: `R4` anchor contexts, `R9`, `AC6`

## Delivers

The language expresses reusable scoped popover relationships with checked named anchors while default-anchor inputs remain usable.

## Acceptance criteria

- [ ] Add a distinct shared anchor symbol kind and full-spelling externalAnchors declaration, with static ownership collected across all provider templates.
- [ ] Bind/rewrite anchor-name, position-anchor, named anchor-scope, anchor()/anchor-size() and nested fallback/math occurrences; retain keyword/default semantics.
- [ ] Reject dynamic/unknown names and static carriers/hole positions that conceal names; check supported functions in valid declaration contexts, including literal templates.
- [ ] Preserve identities, raw-name reservations, compression, stability/maps and reports. Name references do not force setup class liveness or imply DOM existence.
- [ ] Implement a real example with scope/trigger/panel classes, built-in flips, size limits and scrolling; repeated rendered instances use their own trigger in development and production.
- [ ] Keep component IDs/open/close/focus responsibilities explicit; leave Rose and custom named position-try resources untouched.

## Validation

- Shared syntax/token/name/renamer/resource plus processor/linker/bundler tests for owned/external/default/dynamic cases and all anchor occurrence forms.
- Browser geometry tests for repeated scopes and edge/oversized-content examples through linked accessors. Record limits; do not claim all Rose placement bugs fixed.

## Evidence

- `pending`
