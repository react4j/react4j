# T02 — Browser-only coverage evidence

- Status: pending
- Blocked by: T01
- Spec coverage: R4, AC4

## Delivers

A complete class-archive line/method inventory supplies the browser coverage denominator without JVM runtime execution. Chromium alone supplies hits. Remove mixed-platform attribution, fixtures and obsolete tests/build references while retaining fail-closed inventory/map/counter validation.

## Acceptance criteria

- [ ] Runtime coverage no longer reads JVM runtime execution results.
- [ ] Complete class and method inventory is independently collected from existing publication archives; missing sources/modules/methods still fail.
- [ ] Tests retain uncovered, throwing and deferred browser obligations and invalid evidence checks; no synthetic GwtIncompatible or mixed-platform fixtures remain.
- [ ] Coverage collection and existing 95/85 floors pass without lowered thresholds or exclusions.

## Validation

- Runtime coverage/instrumenter/LCOV unit and Chromium fixture tests.
- Collect browser coverage, collect remaining JVM tool coverage, run configured coverage check.

## Evidence

- Pending.
