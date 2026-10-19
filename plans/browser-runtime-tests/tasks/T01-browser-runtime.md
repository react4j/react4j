# T01 — Browser runtime verification

- Status: pending
- Blocked by: None
- Spec coverage: R1–R3, AC1–AC3

## Delivers

Core and DOM runtime tests execute against real browser objects. Configuration tests verify Closure defines, and browser test/support libraries use one checked J2CL compile. Remove the JVM configuration provider branch, annotation, obsolete tests and context mock injection when unused. Preserve JVM bytecode dependency checks.

## Acceptance criteria

- [ ] Browser scenarios preserve all meaningful prior runtime assertions and exercise context defaults/qualifiers and invalid operations, keys and HTML class-name behavior.
- [ ] Separate browser configuration cases verify mode defaults and explicit true/false/invalid overrides with active Java assertions in development and optimized builds.
- [ ] No production GwtIncompatible annotation or JVM configuration overrides remain; browser semantics stay unchanged.
- [ ] Browser-only test/support companions are removed without weakening Error Prone/NullAway or formatter coverage. Keep deliberate JVM consumers intact.

## Validation

- Focused core/DOM browser tests in both modes; JVM dependency tests.
- Focused real compiler failures for Error Prone and NullAway on the new checked J2CL path.
- Buildifier, Java formatter, affected example browser scenarios.

## Evidence

- Pending.
