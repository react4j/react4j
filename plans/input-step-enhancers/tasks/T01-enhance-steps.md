# T01 — Enhance generated steps

- Status: complete
- Blocked by: None
- Spec coverage: R1, R2, R3, R5, R6; AC1, AC2 and required-call portion of AC3

## Delivers

Consumers configure enhancer interfaces on method and constructor inputs and invoke fluent helpers through generated steps. Invalid configurations produce useful diagnostics while unconfigured builders and children remain unchanged.

## Acceptance criteria

- [x] Configuration, model, member validation and step inheritance preserve all input transitions and nullness.
- [x] Source/precompiled and inherited enhancers compile under strict checkers, including primitive and renamed inputs.
- [x] Negative fixtures cover the invalid contracts and shared-builder collisions in AC2; invalid early completion fails compilation.
- [x] Existing generated children fixtures remain byte-for-byte unchanged.

## Validation

- Affected fixture tests and `bazel test //processor:all_tests --test_output=errors`.
- Review formatted/unformatted golden additions and unchanged children goldens.

## Evidence

- Pinned Bazel 9.3.1: `tools/update_goldens.sh jspecify` regenerated 12 new goldens; all existing children goldens unchanged.
- `bazel test //processor:all_tests --test_output=errors`: all 8 targets passed, including 27 enhancer cases and source/precompiled strict goldens.
- Renamed constructor input coverage exposed an existing name-based lookup bug; declaration-identity lookup fixes that accepted use case.
- Review round 1: add declaration-erasure checks for result parameters, arrays, result-bounded method variables and inherited setters; legal overloads remain covered. Check every intersection bound for accessibility.
