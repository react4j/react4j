# T02 — Generated composition and enhancer migration

- Status: complete
- Blocked by: T01
- Spec coverage: R4, R5, R6; AC2

## Delivers

Ready generated builders can be nested without build(), including render methods and node-valued inputs; enhancer signatures follow the widened builder contract.

## Acceptance criteria

- [x] Exactly the existing build-capable step interfaces extend Buildable; required-step ordering and terminal child setters remain unchanged.
- [x] Scalar and array node-input setters accept Renderable values, preserve declared nullness and store materialized nodes. Generic and custom enhancer contracts match projected setter types.
- [x] Standard ChildrenEnhancer and affected fixtures/examples migrate directly; first-step helper forwarding, collision checks and stream behavior remain correct.
- [x] Render methods accept Renderable/subtypes and generated bridges normalize nullable results and exceptions, including context-generated rendering.
- [x] Focused fixture, complete processor and integrated development/production browser checks pass; expected output sets are regenerated and inspected.

## Validation

- `tools/update_goldens.sh generated`, `jspecify`, and/or `regressions` as affected — regenerate authoritative processor output and rerun families.
- `bazel test //processor:all_tests` — exact outputs, diagnostics and full checker compilation.
- `bazel test //:browser_tests` and `bazel test -c opt //:browser_tests` — optional builder nesting, input conversion and render bridges with real React.

## Evidence

- All three affected golden families regenerated and passed; reviewed 380 fixture changes (378 builder outputs and two formatted/unformatted view outputs). Most changes add Buildable to existing build-capable steps.
- `bazel test //processor:all_tests //:browser_tests --keep_going --test_output=errors` passed (25 targets), including strict generated checker compilation and source/precompiled enhancer contracts.
- Optimized `//:browser_tests` passed (17 targets). Final constructor-node scenario passed core browser tests in development and production; Java format check passed.
- Browser scenarios cover optional/generic builders, method and constructor inputs, scalar/array and nullable nodes, defaults, tree-context nodes, Renderable/Buildable render results and post-render ordering. Negative compilation excludes incomplete steps and rejects incompatible enhancer nullness/signatures.
- Existing exact-return validation was replaced by resolved return-type assignability. Effectful node setters and factories with node defaults omit purity claims.
- Review correction: every authored render result is normalized, including a ReactNode declaration whose runtime value also implements Buildable. A real checked-browser hybrid regression reproduced skipped build/post-render ordering before the fix and passed afterward. All three golden families passed regeneration; inspected 458 additional view fixture changes, limited to Renderable imports and conversion around authored render calls.
