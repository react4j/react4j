# T02 — Forward first-step helpers

- Status: pending
- Blocked by: T01
- Spec coverage: R2, R4, R5; static-forwarding portion of AC3

## Delivers

First-stage helpers are usable directly on the generated builder class with the same generic signatures, nullness, varargs, exceptions and result as the corresponding instance helper.

## Acceptance criteria

- [ ] Inherited generic helpers, checked exceptions, ordinary and generic-view signatures and varargs produce compilable shortcuts.
- [ ] Method and view type-variable name collisions are resolved without changing contracts.
- [ ] Shortcuts exist only for first-stage helpers and do not claim user-method purity.
- [ ] Shortcut collisions produce diagnostics; later required steps remain enforced.

## Validation

- Focused call-site compilations and formatted/unformatted goldens.
- `bazel test //processor:all_tests --test_output=errors`.

## Evidence

- pending
