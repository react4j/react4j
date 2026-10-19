# T02 — Browser-only coverage evidence

- Status: complete
- Blocked by: T01
- Spec coverage: R4, AC4

## Delivers

A complete class-archive line/method inventory supplies the browser coverage denominator without JVM runtime execution. Chromium alone supplies hits. Remove mixed-platform attribution, fixtures and obsolete tests/build references while retaining fail-closed inventory/map/counter validation.

## Acceptance criteria

- [x] Runtime coverage no longer reads JVM runtime execution results.
- [x] Complete class and method inventory is independently collected from existing publication archives; missing sources/modules/methods still fail.
- [x] Tests retain uncovered, throwing and deferred browser obligations and invalid evidence checks; no synthetic GwtIncompatible or mixed-platform fixtures remain.
- [x] Coverage collection and existing 95/85 floors pass without lowered thresholds or exclusions.

## Validation

- Runtime coverage/instrumenter/LCOV unit and Chromium fixture tests.
- Collect browser coverage, collect remaining JVM tool coverage, run configured coverage check.

## Evidence

- Runtime coverage, instrumenter and LCOV suites passed; runtime coverage suite passed again after the fixture cleanup/Yoda correction.
- Runtime collector ran successfully with only its output path as a CLI argument, deriving locations from publication jars and testing raw Closure defaults before mutable test configuration.
- All 22 remaining JVM coverage suites passed under the new processor/CSS-only instrumentation filter. The configured coverage check passed every root and override without edits to coverage.json.
- Runtime results: core 246/252 lines (97.62%), 53/58 branches (91.38%); DOM 1745/1784 lines (97.81%), 14/14 branches (100%).
- Buildifier check, Java format check and Bash syntax check passed. No mixed-platform source fixtures or GwtIncompatible declarations remain in core/DOM/verification.
