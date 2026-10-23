# T05 — Retain and rename property support

- Status: pending
- Blocked by: T04
- Spec coverage: R5 (properties), R6 (external properties), R9 (registration/conditions), R11 (property dependencies); AC3 (properties), AC6 (property chain)

## Delivers

Live utility classes retain the custom-property definitions, registrations and aliases they require, with consistent scoping/renaming and explicit external variable integration.

## Acceptance criteria

- [ ] Properties declared/read by the provider have canonical ownership unless explicitly imported/external; registrations, declaration names, var() references and condition-prelude references agree.
- [ ] Static alias chains, fallbacks, empty fallbacks, scoped alternative definitions and dependency cycles preserve browser semantics and terminate analysis.
- [ ] A dead utility releases sole support dependencies; shared support needed by live rules remains. Uncertain scope choices are conservative.
- [ ] Two providers with identical authored property names remain independent; explicit external properties retain authored spelling and integrate with literal external CSS/runtime values under the existing name-origin contract.
- [ ] Runtime custom-property access through existing checked getters remains usable; property-only accessors activate the correct provider without inventing class presence.

## Validation

- Processor/internal/bundler tests verify occurrence spans, property/registration rewriting, liveness fixed points and conditional references.
- Optimized integration fixture with overlapping provider names and external variables asserts correct values and pruned sole dependencies.

## Evidence

- pending
