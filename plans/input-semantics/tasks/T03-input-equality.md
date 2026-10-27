# T03 — Mutable input equality policies

- Status: pending
- Blocked by: T02
- Spec coverage: R3, R4, R5, AC3

## Delivers

Explicit Arez comparators and exact-type defaults determine all mutable input change consequences, with known-class direct generation and custom dispatch.

## Acceptance criteria

- [ ] Explicit > exact-type default > strict fallback precedence is checked in source and compiled declarations, including exclusions.
- [ ] Every comparison site uses the same policy; builtin goldens have direct calls without allocations.
- [ ] Custom policies receive declared Java values; invalid classes and immutable-target misuse diagnose.
- [ ] Browser counts cover rendering, observation, PRE/POST callbacks, latest equal replacement and unrelated scheduled updates.

## Validation

All processor tests and core browser tests in both modes; inspect emitted bodies and specialization.

## Evidence

Pending.
