# T02 — Generated composition and enhancer migration

- Status: pending
- Blocked by: T01
- Spec coverage: R4, R5, R6; AC2

## Delivers

Ready generated builders can be nested without build(), including render methods and node-valued inputs; enhancer signatures follow the widened builder contract.

## Acceptance criteria

- [ ] Exactly the existing build-capable step interfaces extend Buildable; required-step ordering and terminal child setters remain unchanged.
- [ ] Scalar and array node-input setters accept Renderable values, preserve declared nullness and store materialized nodes. Generic and custom enhancer contracts match projected setter types.
- [ ] Standard ChildrenEnhancer and affected fixtures/examples migrate directly; first-step helper forwarding, collision checks and stream behavior remain correct.
- [ ] Render methods accept Renderable/subtypes and generated bridges normalize nullable results and exceptions, including context-generated rendering.
- [ ] Focused fixture, complete processor and integrated development/production browser checks pass; expected output sets are regenerated and inspected.

## Validation

- `tools/update_goldens.sh generated`, `jspecify`, and/or `regressions` as affected — regenerate authoritative processor output and rerun families.
- `bazel test //processor:all_tests` — exact outputs, diagnostics and full checker compilation.
- `bazel test //:browser_tests` and `bazel test -c opt //:browser_tests` — optional builder nesting, input conversion and render bridges with real React.

## Evidence

- Pending.
