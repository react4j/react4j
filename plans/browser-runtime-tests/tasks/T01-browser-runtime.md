# T01 — Browser runtime verification

- Status: complete
- Blocked by: None
- Spec coverage: R1–R3, AC1–AC3

## Delivers

Core and DOM runtime tests execute against real browser objects. Configuration tests verify Closure defines, and browser test/support libraries use one checked J2CL compile. Remove the JVM configuration provider branch, annotation, obsolete tests and context mock injection when unused. Preserve JVM bytecode dependency checks.

## Acceptance criteria

- [x] Browser scenarios preserve all meaningful prior runtime assertions and exercise context defaults/qualifiers and invalid operations, keys and HTML class-name behavior.
- [x] Separate browser configuration cases verify mode defaults and explicit true/false/invalid overrides with active Java assertions in development and optimized builds.
- [x] No production GwtIncompatible annotation or JVM configuration overrides remain; browser semantics stay unchanged.
- [x] Browser-only test/support companions are removed without weakening Error Prone/NullAway or formatter coverage. Keep deliberate JVM consumers intact.

## Validation

- Focused core/DOM browser tests in both modes; JVM dependency tests.
- Focused real compiler failures for Error Prone and NullAway on the new checked J2CL path.
- Buildifier, Java formatter, affected example browser scenarios.

## Evidence

- Passed core/DOM dependency checks and all 14 browser suites in both development and optimized modes.
- New configuration profiles exercise default mode settings and explicit true/false/invalid values, including non-production environment spelling. Context tests render real defaults for all registration overloads and verify diagnostic failures and duplicate preservation.
- Both modes rejected deliberate ArrayEquals and NullAway failures through CSS links of the new checked J2CL wrapper; temporary probes were removed.
- Java formatting/check and buildifier passed. Formatter targets now name the J2CL test/support libraries directly; their nine companion JVM compilations are absent.
- Newly optimized core execution required corrected native test-helper types and external property spelling. Overlay discovery remains in the existing unoptimized browser coverage suite; standalone optimized tests exercise runtime behavior without reflection over optimized method names.
