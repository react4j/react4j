# T01 — Content-based immutable array identity

- Status: complete
- Blocked by: None
- Spec coverage: R1, R6, AC1

## Delivers

Supported declared arrays use lossless scalar-equivalent content keys at finalization, including defaults/context and combined inputs.

## Acceptance criteria

- [x] String and eight primitive array kinds compile and preserve correct ordered identity.
- [x] Nulls, element/combined boundaries, long precision, NaN and signed zero behave correctly.
- [x] Real React reuses equal arrays and remounts changed arrays across required, default and context paths.

## Validation

Processor goldens plus core browser tests in development and optimized modes; format affected sources.

## Evidence

Passed: input_semantics strict compilations (formatted/unformatted), jspecify golden regeneration/check (four affected goldens), core browser tests in development and -c opt, Java and Bazel formatting. Browser evidence covers reuse/remount, defaults/context, composite framing, all primitive encodings, long precision, NaN/zero and J2CL extra float precision.
