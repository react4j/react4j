# T01 — Core runtime and browser support

- Status: `pending`
- Blocked by: None
- Spec coverage: R1,R2,R5; AC1

## Delivers

Migrate core runtime assertions through a small checked, test-only native browser/async support module. Preserve React component/native roundtrips and scheduler waits. Keep the JS driver as the existing Closure entry point.

## Acceptance criteria

- [ ] Delivered behavior and relevant spec criteria preserve the original cases.
- [ ] Focused checks and formatting pass; the Java/native boundary is inspected.
- [ ] Evidence and task state are committed together.

## Validation

Core runtime coverage browser target/collector in both configurations; checked Java builds, formatter and unchanged production coverage inventory.

## Evidence

- pending
