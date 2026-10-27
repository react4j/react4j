# T01 — Core and DOM composition

- Status: pending
- Blocked by: None
- Spec coverage: R1, R2, R3, R6; AC1

## Delivers

Hand-written Buildable values compose through all core, context and DOM node boundaries, with raw nodes preserved and eager conversion on each occurrence.

## Acceptance criteria

- [ ] Public Renderable and functional Buildable compile under J2CL and Java checkers; build results remain non-null.
- [ ] Core factories, fragment/strict/suspense, providers/callbacks, DOM factories/props children, roots, portals and legacy rendering consume materialized nodes.
- [ ] Tests preserve native primitives/arrays, nullness, order, single stream traversal, exceptions, caller-owned arrays, props precedence and repeated-use behavior.
- [ ] Core and DOM browser checks pass in development and optimized production; source lists and generated DOM templates remain authoritative.

## Validation

- `bazel test //core:tests //dom:tests` — dependency boundaries.
- `bazel test //:browser_tests` and `bazel test -c opt //:browser_tests` — actual React composition and optimized JsInterop.
- `tools/java_format.sh check` and changed Bazel syntax/format checks.

## Evidence

- Pending.
