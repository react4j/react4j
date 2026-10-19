# T01 — Core runtime and browser support

- Status: `complete`
- Blocked by: None
- Spec coverage: R1,R2,R5; AC1

## Delivers

Migrate core runtime assertions through a small checked, test-only native browser/async support module. Preserve React component/native roundtrips and scheduler waits. Keep the JS driver as the existing Closure entry point.

## Acceptance criteria

- [x] Delivered behavior and relevant spec criteria preserve the original cases.
- [x] Focused checks and formatting pass; the Java/native boundary is inspected.
- [x] Evidence and task state are committed together.

## Validation

Core runtime coverage browser target/collector in both configurations; checked Java builds, formatter and unchanged production coverage inventory.

## Evidence

- RuntimeCoverageTest passed in default and -c opt configurations, including mapped native element probes and skipped/throwing attribution regressions.
- `bazel run //tools/verification:runtime_coverage -- "$PWD/reports/coverage/jvm.lcov" "$PWD/reports/coverage/runtime.lcov"` passed, executing the migrated core scenarios in development and production. Production source/inventory inputs are unchanged.
- Java formatter, buildifier and diff checks passed; native core helpers contain only React fixture construction, property observation and render/unmount.
