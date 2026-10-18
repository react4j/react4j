# T02 — Inherited symbol contracts

- Status: pending
- Blocked by: T01
- Spec coverage: R3/AC3, R4/AC4, R5/AC5 (symbols), R6/AC6

## Delivers

CSS IDs, animation and property annotations on interfaces work across all definition kinds with deterministic override/diamond behavior and validation only when consumed.

## Acceptance criteria

- [ ] Unannotated overrides inherit metadata; explicit same-kind mappings refine names and incompatible kinds fail.
- [ ] Matching diamonds merge; plain declarations do not erase annotations; unresolved name conflicts require explicit resolution, independent of traversal order.
- [ ] Effective concrete/default symbol methods fail, while re-abstraction permits generation; resource and component occurrence/import checks remain.
- [ ] Standalone interface compilation emits no CSS/bindings or contract errors; consuming definitions validate inherited contracts.
- [ ] Processor constraint matrices and formatted/unformatted goldens cover all kinds, names, overrides and conflicts.

## Validation

Run both processor modes and focused coverage for the new resolver. Inspect all expected resource/Java/diagnostic changes, including inherited getter inference, owner-dependent keyframe names, duplicate exports and source/binary parity.

## Evidence

Pending.
