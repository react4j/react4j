# T03 — Verify browser behavior and publish the contract

- Status: complete
- Blocked by: T02
- Spec coverage: R1-R6; AC4, AC5

## Delivers

Real browser tests verify generated helpers and shortcuts write the intended values, finish or continue correctly, and propagate declared failures. Component authors have durable guidance and the complete repository gate passes.

## Acceptance criteria

- [x] Browser scenarios exercise advancing/staying/terminal helpers and first-stage shortcuts against real React in development and optimized production modes.
- [x] Accepted durable behavior is promoted to its canonical specification after candidate classification; author Javadoc/examples and Unreleased notes explain usage and restrictions.
- [x] Explicit source/build/site inventories cover changed artifacts; domain links validate.
- [x] `tools/check.sh` passes and exact evidence is recorded in TASKMAP before the task commit.

## Validation

- Browser development and production checks, documentation-link validation, site validation, product archives, then `tools/check.sh`.
- Inspect implementation and specification-only diffs against the accepted scope, especially unchanged children behavior.

## Evidence

- Pinned Bazel 9.3.1: `tools/check.sh` passed on the frozen final implementation, including all processor/browser suites, optimized browser tests, coverage, site checks, archives and unsigned bundle validation (12 artifacts, 60 entries).
- Real React/J2CL scenarios passed for first-stage/static, advancing, staying and terminal helpers; checked exceptions and user side effects are asserted.
- Compiler regressions cover explicit unmarked helper scopes, deprecated enhancer/signature types, and synthetic Object bounds in multiple-interface intersections. Standalone source/precompiled compiler probes verified the bound normalization.
- Domain link validation and specification-only diff audit passed. Existing children goldens are unchanged.
