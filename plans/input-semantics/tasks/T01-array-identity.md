# T01 — Content-based immutable array identity

- Status: pending
- Blocked by: None
- Spec coverage: R1, R6, AC1

## Delivers

Supported declared arrays use lossless scalar-equivalent content keys at finalization, including defaults/context and combined inputs.

## Acceptance criteria

- [ ] String and eight primitive array kinds compile and preserve correct ordered identity.
- [ ] Nulls, element/combined boundaries, long precision, NaN and signed zero behave correctly.
- [ ] Real React reuses equal arrays and remounts changed arrays across required, default and context paths.

## Validation

Processor goldens plus core browser tests in development and optimized modes; format affected sources.

## Evidence

Pending.
