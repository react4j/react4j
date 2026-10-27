# T02 — Typed custom immutable keys

- Status: complete
- Blocked by: T01
- Spec coverage: R2, R6, AC2

## Delivers

Input.keyGenerator selects a generic InputKeyGenerator<T> implementation, with compile-time contract validation and correct invocation from effective stored values.

## Acceptance criteria

- [x] Explicit provider overrides builtins and receives declared Java values/nulls.
- [x] Generic inheritance, incompatible input, nullness, invalid class/construction and mutable-target misuse are diagnosed or compiled as appropriate.
- [x] Browser counts demonstrate equal custom keys retain identity and changed keys remount.

## Validation

Processor compile/diagnostic tests and relevant browser scenarios in both modes.

## Evidence

Passed: input_semantics formatted/unformatted strict compilations and diagnostics (including nullable/inherited generic contracts and separately compiled provider), plus development and optimized core browser tests. Browser regression proves interface dispatch despite overloaded provider methods and normal primitive boxing. Java formatting applied.
