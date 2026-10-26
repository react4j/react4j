# T01 — Enhance generated steps

- Status: pending
- Blocked by: None
- Spec coverage: R1, R2, R3, R5, R6; AC1, AC2 and required-call portion of AC3

## Delivers

Consumers configure enhancer interfaces on method and constructor inputs and invoke fluent helpers through generated steps. Invalid configurations produce useful diagnostics while unconfigured builders and children remain unchanged.

## Acceptance criteria

- [ ] Configuration, model, member validation and step inheritance preserve all input transitions and nullness.
- [ ] Source/precompiled and inherited enhancers compile under strict checkers, including primitive and renamed inputs.
- [ ] Negative fixtures cover the invalid contracts and shared-builder collisions in AC2; invalid early completion fails compilation.
- [ ] Existing generated children fixtures remain byte-for-byte unchanged.

## Validation

- Affected fixture tests and `bazel test //processor:all_tests --test_output=errors`.
- Review formatted/unformatted golden additions and unchanged children goldens.

## Evidence

- pending
