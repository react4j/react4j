# T03 — Coverage, documentation and full validation

- Status: pending
- Blocked by: T02
- Spec coverage: R7/AC7 and final verification of AC1–AC6

## Delivers

Permanent downstream documentation and comprehensive evidence for all accepted symbol contracts with unchanged coverage thresholds and a simplified implementation ready for independent review.

## Acceptance criteria

- [ ] Constraint matrices cover inference/invalid names, illegal method/type shapes, aliases/retention/cycles, identifier context boundaries and separate compilation.
- [ ] Unit coverage of newly hazardous logic is high; configured roots pass unchanged thresholds.
- [ ] README/changelog explain actual user behavior and migration boundaries without process narration.
- [ ] Simplify pass and tools/check.sh pass; evidence is recorded before the task commit.

## Validation

Run focused tests for added cases, formatter/buildifier, then tools/check.sh. Inspect all diffs and generated goldens. Do not weaken policy to pass coverage. Independent implementation review follows this task; closeout deletes exactly this plan tree after review passes.

## Evidence

Pending.
