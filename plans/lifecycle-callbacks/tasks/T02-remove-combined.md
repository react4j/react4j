# T02 — Remove combined annotation and prove runtime behavior

- Status: pending
- Blocked by: T01
- Spec coverage: R1, R5, R6; AC1 (browser), AC3, AC4 (full gate)

## Delivers

The final public annotation API, migration guidance and verified runtime behavior in both browser configurations.

## Acceptance criteria

- [ ] Removed annotation, descriptor slot, processor constant, explicit source entry and obsolete fixtures are gone; remaining fixtures are migrated intentionally.
- [ ] Checked Java browser scenarios assert mount/pre-update/post-update order, dual callbacks and Arez action interception; retained lifecycle guards are covered.
- [ ] Javadocs, Unreleased changelog and qualifying canonical domain artifacts describe the accepted behavior and source migration.
- [ ] Changed documentation links validate; the complete gate passes; the final diff contains only intentional changes.

## Validation

- Processor tests after migration; development and optimized browser tests; domain-document link validation.
- `tools/check.sh`, including coverage and distribution/site checks.

## Evidence

Pending.
