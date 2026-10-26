# T02 — Forward first-step helpers

- Status: complete
- Blocked by: T01
- Spec coverage: R2, R4, R5; static-forwarding portion of AC3

## Delivers

First-stage helpers are usable directly on the generated builder class with the same generic signatures, nullness, varargs, exceptions and result as the corresponding instance helper.

## Acceptance criteria

- [x] Inherited generic helpers, checked exceptions, ordinary and generic-view signatures and varargs produce compilable shortcuts.
- [x] Method and view type-variable name collisions are resolved without changing contracts.
- [x] Shortcuts exist only for first-stage helpers and do not claim user-method purity.
- [x] Shortcut collisions produce diagnostics; later required steps remain enforced.

## Validation

- Focused call-site compilations and formatted/unformatted goldens.
- `bazel test //processor:all_tests --test_output=errors`.

## Evidence

- Pinned Bazel 9.3.1: `tools/update_goldens.sh jspecify` passed with identical source/precompiled output.
- `bazel test //processor:all_tests --test_output=errors`: all 8 targets passed.
- Focused enhancer cases cover generic/name substitution, checked exceptions, reifiable and generic varargs, inherited/nested nullness, legal shared defaults and signature/type conflicts.
- Shortcut parameter names are normalized because classfile inputs do not reliably retain source parameter names.
