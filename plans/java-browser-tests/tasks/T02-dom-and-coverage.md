# T02 — DOM probes and coverage drivers

- Status: `pending`
- Blocked by: T01
- Spec coverage: R1,R2,R3,R5; AC1,AC2

## Delivers

Migrate DOM runtime, fluent-property and event expectations into Java; keep generated-overlay discovery/invocation and event construction as native interop. Move verification runtime probe expectations into Java and audit the measurement driver, retaining only deliberate interop fixture behavior.

## Acceptance criteria

- [ ] Delivered behavior and relevant spec criteria preserve the original cases.
- [ ] Focused checks and formatting pass; the Java/native boundary is inspected.
- [ ] Evidence and task state are committed together.

## Validation

All property/event/runtime probes, runtime coverage and attribution/omission regression suites; native identity, aliases, false values, all discovered APIs, skipped/throwing/deferred paths and unchanged coverage floors.

## Evidence

- pending
