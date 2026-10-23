# T04 — Activate checked globals with correct precedence

- Status: complete
- Blocked by: T03
- Spec coverage: R6 (cross-owner imports), R7, R8, R9 (global permissions); AC3 (boundary/reset), AC4 (precedence)

## Delivers

A separately supplied file or inline reset uses checked theme imports and activates only when explicitly selected. The bundled cascade preserves component precedence while allowing important declarations in global inputs.

## Acceptance criteria

- [x] Utility definitions reject unguarded ordinary element styles but allow support declarations, registrations and custom-property initialization; selector-function branches cannot bypass the boundary.
- [x] Selecting a utility does not activate reset/default globals automatically. Explicit global roots/imports preserve deduplication, inclusion order and cycle validation.
- [x] File-backed and inline globals import library-owned properties/resources using the same canonical identities.
- [x] Ordinary component rules beat utility rules; authored utility layer ordering remains intact. Independent libraries do not accidentally merge internal layer identities.
- [x] Standard important declarations work in explicit checked/raw global inputs and continue failing in utility/component definitions.
- [x] Browser computed styles prove reset selection, utility-to-component precedence and the explicit global important exception.

## Validation

- Processor/bundler boundary tests include selector lists, is/where/not/has, mixed guarded and unguarded branches, support-only blocks and unsupported constructs.
- Browser fixture asserts actual computed styles for selected/unselected reset and normal/important cascade.
- Existing global-root, inclusion-order, raw-name-collision and multi-variant tests remain green.

## Evidence

- UtilitySelectors proves a positive managed class on every ordinary selector branch, with explicit initialization/support exceptions. Dynamic javac cases cover selector lists, is/where/has/negation, mixed branches, typed library property imports, checked asset URLs and modern global wrappers.
- Bundles use shared/utilities/components precedence and give each utility provider a collision-free private layer. Inline and file-backed globals resolve library-owned properties to the same canonical identity. Existing bundle inclusion, cycle, variant and raw-name-collision checks passed.
- Browser tests passed in development and optimized modes. Computed styles verify normal component precedence, opposite internal layer orders in two independent libraries, explicitly selected reset/important globals and restored defaults when the global is removed. The prior component-reordering assertion was updated for the new hybrid owner and now includes utility nodes in its snapshot.
- An empty anonymous layer exposed a further pinned grammar/model defect: the grammar required a child and its renderer emitted an invalid anonymous statement. Bounded repairs plus parse/render regression tests preserve that layer identity; this retains D6's chosen extension boundary.
- Final refined internal/compiler/bundler/integration run passed all 12 targets. Java formatting, buildifier, documentation links and git diff --check passed. Reader documentation now describes actual global permissions and layer precedence.
