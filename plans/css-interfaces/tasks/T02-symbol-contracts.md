# T02 — Inherited symbol contracts

- Status: complete
- Blocked by: T01
- Spec coverage: R3/AC3, R4/AC4, R5/AC5 (symbols), R6/AC6

## Delivers

CSS IDs, animation and property annotations on interfaces work across all definition kinds with deterministic override/diamond behavior and validation only when consumed.

## Acceptance criteria

- [x] Unannotated overrides inherit metadata; explicit same-kind mappings refine names and incompatible kinds fail.
- [x] Matching diamonds merge; plain declarations do not erase annotations; unresolved name conflicts require explicit resolution, independent of traversal order.
- [x] Effective concrete/default symbol methods fail, while re-abstraction permits generation; resource and component occurrence/import checks remain.
- [x] Standalone interface compilation emits no CSS/bindings or contract errors; consuming definitions validate inherited contracts.
- [x] Processor constraint matrices and formatted/unformatted goldens cover all kinds, names, overrides and conflicts.

## Validation

Run both processor modes and focused coverage for the new resolver. Inspect all expected resource/Java/diagnostic changes, including inherited getter inference, owner-dependent keyframe names, duplicate exports and source/binary parity.

## Evidence

Both assembled processor and implementation suites pass, including all existing fixtures and the new interface-symbol and conflicting-diamond fixtures in formatted/unformatted modes. tools/update_goldens.sh css regenerated and verified 26 new expected outputs; emitted bindings, CSS identities, dependencies and conflict diagnostics were inspected. Focused bazel coverage on implementation_tests passes: CssProcessor 439/442 lines and 275/286 branches; CssMethods 58/60 lines and 28/40 branches. T03 will harden method-resolution branches and cross-compilation evidence before the unchanged full coverage gate. Java format and buildifier checks pass.
