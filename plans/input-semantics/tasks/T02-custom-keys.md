# T02 — Typed custom immutable keys

- Status: pending
- Blocked by: T01
- Spec coverage: R2, R6, AC2

## Delivers

Input.keyGenerator selects a generic InputKeyGenerator<T> implementation, with compile-time contract validation and correct invocation from effective stored values.

## Acceptance criteria

- [ ] Explicit provider overrides builtins and receives declared Java values/nulls.
- [ ] Generic inheritance, incompatible input, nullness, invalid class/construction and mutable-target misuse are diagnosed or compiled as appropriate.
- [ ] Browser counts demonstrate equal custom keys retain identity and changed keys remount.

## Validation

Processor compile/diagnostic tests and relevant browser scenarios in both modes.

## Evidence

Pending.
