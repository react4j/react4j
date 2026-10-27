# T01 — Core and DOM composition

- Status: complete
- Blocked by: None
- Spec coverage: R1, R2, R3, R6; AC1

## Delivers

Hand-written Buildable values compose through all core, context and DOM node boundaries, with raw nodes preserved and eager conversion on each occurrence.

## Acceptance criteria

- [x] Public Renderable and functional Buildable compile under J2CL and Java checkers; build results remain non-null.
- [x] Core factories, fragment/strict/suspense, providers/callbacks, DOM factories/props children, roots, portals and legacy rendering consume materialized nodes.
- [x] Tests preserve native primitives/arrays, nullness, order, single stream traversal, exceptions, caller-owned arrays, props precedence and repeated-use behavior.
- [x] Core and DOM browser checks pass in development and optimized production; source lists and generated DOM templates remain authoritative.

## Validation

- `bazel test //core:tests //dom:tests` — dependency boundaries.
- `bazel test //:browser_tests` and `bazel test -c opt //:browser_tests` — actual React composition and optimized JsInterop.
- `tools/java_format.sh check` and changed Bazel syntax/format checks.

## Evidence

- Pinned Bazel 9.3.1: core/DOM dependency tests and all development browser checks passed (19 tests); optimized `//:browser_tests` passed (17 tests).
- New checked browser scenarios cover mixed arrays, raw identity, nullable values, eager order/counts, unchanged caller arrays/props, context callbacks, fragments/strict/suspense, DOM streams, legacy rendering, portals and both root render overloads.
- `bazel run //tools/buildifier:buildifier`, `tools/java_format.sh check`, and `git diff --check` passed.
- Native root conversion uses a package-private native bridge because interface overlay methods cannot declare private native instance calls.
