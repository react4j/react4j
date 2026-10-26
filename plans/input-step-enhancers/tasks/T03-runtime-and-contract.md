# T03 — Verify browser behavior and publish the contract

- Status: pending
- Blocked by: T02
- Spec coverage: R1-R6; AC4, AC5

## Delivers

Real browser tests verify generated helpers and shortcuts write the intended values, finish or continue correctly, and propagate declared failures. Component authors have durable guidance and the complete repository gate passes.

## Acceptance criteria

- [ ] Browser scenarios exercise advancing/staying/terminal helpers and first-stage shortcuts against real React in development and optimized production modes.
- [ ] Accepted durable behavior is promoted to its canonical specification after candidate classification; author Javadoc/examples and Unreleased notes explain usage and restrictions.
- [ ] Explicit source/build/site inventories cover changed artifacts; domain links validate.
- [ ] `tools/check.sh` passes and exact evidence is recorded in TASKMAP before the task commit.

## Validation

- Browser development and production checks, documentation-link validation, site validation, product archives, then `tools/check.sh`.
- Inspect implementation and specification-only diffs against the accepted scope, especially unchanged children behavior.

## Evidence

- pending
