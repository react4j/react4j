# T02 — Remove combined annotation and prove runtime behavior

- Status: complete
- Blocked by: T01
- Spec coverage: R1, R5, R6; AC1 (browser), AC3, AC4 (full gate)

## Delivers

The final public annotation API, migration guidance and verified runtime behavior in both browser configurations.

## Acceptance criteria

- [x] Removed annotation, descriptor slot, processor constant, explicit source entry and obsolete fixtures are gone; remaining fixtures are migrated intentionally.
- [x] Checked Java browser scenarios assert mount/pre-update/post-update order, dual callbacks and Arez action interception; retained lifecycle guards are covered.
- [x] Javadocs, Unreleased changelog and qualifying canonical domain artifacts describe the accepted behavior and source migration.
- [x] Changed documentation links validate; the complete gate passes; the final diff contains only intentional changes.

## Validation

- Processor tests after migration; development and optimized browser tests; domain-document link validation.
- `tools/check.sh`, including coverage and distribution/site checks.

## Evidence

- `tools/update_goldens.sh generated`: passed after hard removal and source/compiled interface-override cases; reviewed complete output and explicit order assertions.
- Development and `-c opt` core browser suites passed: exact mount/update/pre-update order, dual/triple annotations once per phase, Arez action interception, input-change timing, immutable disposed-input/view guards, and unchecked exception propagation.
- The optimized browser check exposed the old zero-argument snapshot extern. Verified React 16.6.0's pinned call with previous props/state and corrected the extern to match the existing generated interface.
- `tools/check.sh`: passed completely with pinned Bazel 9.3.1: 57 repository tests, 17 optimized browser tests, 25 JVM coverage suites, real browser coverage thresholds, site build/check, and validation of 12 artifacts / 60 unsigned bundle entries.
- Processor coverage: 2958/3077 lines and 1453/1688 branches, above the configured thresholds.
- Domain classification: durable participation/order/timing/failure/guard contracts belong to the new specification; the registration-order rationale qualifies for its ADR. Migration belongs to the changelog/Javadocs; implementation and verification stay with code/tests and this temporary plan.
- Specification-only audit and domain link validation passed; all statements describe accepted behavior independent of the selected collector. No live old-annotation references remain in core, processor or docs. `git diff --check` passed.
- Build environment: an isolated output base avoided another project's active server. The temporary launcher retained the real owned output-user root for Ahab's nested query; no repository check was skipped or weakened.
- Implementation review correction: qualified the disposed-update contract to views with disposable inputs, preserving R6's existing guard boundary. Specification-only audit, documentation-link validation and `git diff --check` passed after the wording change; no runtime change required another build.
