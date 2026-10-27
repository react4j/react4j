# T03 — Mutable input equality policies

- Status: complete
- Blocked by: T02
- Spec coverage: R3, R4, R5, AC3

## Delivers

Explicit Arez comparators and exact-type defaults determine all mutable input change consequences, with known-class direct generation and custom dispatch.

## Acceptance criteria

- [x] Explicit > exact-type default > strict fallback precedence is checked in source and compiled declarations, including exclusions.
- [x] Every comparison site uses the same policy; builtin goldens have direct calls without allocations.
- [x] Custom policies receive declared Java values; invalid classes and immutable-target misuse diagnose.
- [x] Browser counts cover rendering, observation, PRE/POST callbacks, latest equal replacement and unrelated scheduled updates.

## Validation

All processor tests and core browser tests in both modes; inspect emitted bodies and specialization.

## Evidence

Passed: all nine processor test targets, generated and jspecify golden regeneration/checks, and development plus optimized core browser tests. Source/classpath defaults, explicit overrides/exclusions, builtin direct calls/no provider fields, custom dispatch, generic/nullable values and all primitive compile forms are checked. Browser counts cover memoized invalidation, PRE/POST and multi-input callbacks, current equal replacement, independent Arez updates, wrapper/char/long conversion, and strict versus selected floating equality.
