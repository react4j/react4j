# T02 — DOM probes and coverage drivers

- Status: `complete`
- Blocked by: T01
- Spec coverage: R1,R2,R3,R5; AC1,AC2

## Delivers

Migrate DOM runtime, fluent-property and event expectations into Java; keep generated-overlay discovery/invocation and event construction as native interop. Move verification runtime probe expectations into Java and audit the measurement driver, retaining only deliberate interop fixture behavior.

## Acceptance criteria

- [x] Delivered behavior and relevant spec criteria preserve the original cases.
- [x] Focused checks and formatting pass; the Java/native boundary is inspected.
- [x] Evidence and task state are committed together.

## Validation

All property/event/runtime probes, runtime coverage and attribution/omission regression suites; native identity, aliases, false values, all discovered APIs, skipped/throwing/deferred paths and unchanged coverage floors.

## Evidence

- Runtime coverage collector passed in development and production; RuntimeCoverageTest attribution/omission regressions passed after migration.
- CoverageCheck passed all unchanged root/class floors (95% lines / 85% branches). Java formatter, buildifier and diff checks passed.
- Native helpers retain only overlay enumeration/invocation, raw JS values and real event construction/dispatch. Java checks every discovered property/event method, fluent identity, aliases and false updates.
- Verification map-probe expectation moved to Java. fixture_test.js and CSS integration entry.js/asset_entry.js contain deliberate measurement/link fixtures, no assertion logic; retained to preserve native callgraph and attribution failures.
