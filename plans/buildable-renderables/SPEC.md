# Buildable Renderables Spec

## Source

- Accepted scope: [issue #101](https://github.com/react4j/react4j/issues/101), the grill-me answers `1A, 2A, 3A`, and the final design confirmed in this chat on 2026-10-27.
- Execution authority: user instruction, “Confirmed. Implement issue #101 using the structured delivery workflow, then push to a PR.” No additional human plan checkpoint; stop at a ready-for-review PR, without merging.
- Repository evidence: native `ReactNode`, `ReactElement`, `Context`, DOM source templates, `ReactDOM`, `ReactRoot`, processor builder/render generators, exact enhancer signature validation, and the existing [input-step contract](../../docs/specs/input-step-enhancers.md).

## Problem

Adding an optional input changes a completed builder expression into a builder step and forces callers to append `.build()` wherever a node is consumed.

## Required outcome

A builder whose required calls are complete can be supplied anywhere a typed React4j composition API accepts a node. It is built at that boundary, giving the same behavior as an explicit `.build()` there.

## Scope

- In scope: node factories; fragment, strict mode and suspense; DOM tags and typed props children; roots, legacy render and portals; context providers and callbacks; generated ready steps, scalar/array node inputs, `@Render`, and custom node-input enhancers; arrays, streams, nullness, documentation and publication checks.
- Out of scope: interpreting untyped Object/JsPropertyMap inputs as nodes, recursive rewriting of arbitrary user data, new React features, builder caching/freezing, release publication, and PR merging.

## Constraints

- Preserve required-call ordering, terminal child setters, scalar-child array storage, custom enhancer selection and stream encounter order from the existing input-step contract.
- `ReactNode` is a native JavaScript union, including primitive strings/numbers and arrays. Pinned J2CL prohibits native interfaces extending ordinary Java interfaces; the common marker must be native. Detect ordinary `Buildable`, never the native union, at runtime.
- Leave `etc/secrets` untouched. Use explicit source lists and Yoda equality. Edit generators and regenerate fixtures rather than hand-editing generated outputs.
- Use direct API migration; add no obsolete-signature compatibility overloads.

## Requirements

The [composition contract](../../docs/specs/renderable-composition.md) owns R1–R6. Enhancer-specific constraints remain in the [input-step contract](../../docs/specs/input-step-enhancers.md).

- `R1`: Public native `Renderable` is the common parent of `ReactNode` and public functional `Buildable`; `Buildable.build()` returns a non-null `ReactNode`. Existing raw nodes retain their representation and identity.
- `R2`: Core node composition and context APIs accept renderables, materialize builders at consumption, and preserve existing nullable positions. Context callbacks materialize their result on every invocation.
- `R3`: DOM composition, typed props children, suspense fallback, roots, portals and legacy rendering materialize renderables before calling React. Existing explicit-children precedence over props children remains unchanged.
- `R4`: Only generated step interfaces already exposing `build()` extend `Buildable`; required steps remain unavailable for typed composition. Explicit builds and terminal child behavior remain supported.
- `R5`: Generated scalar/array node input setters accept renderables and store materialized nodes. Custom enhancers match that widened setter contract. `@Render` accepts any renderable result and its bridge returns a materialized nullable node.
- `R6`: Each supplied builder occurrence is evaluated once at its boundary without caching, freezing or swallowing errors. Arrays preserve order, each position's nullness and caller data; streams traverse once. Existing build reuse semantics remain unchanged.
- `R7`: Checked examples, migration guidance, changelog and canonical domain artifacts describe the accepted public behavior and impacts.
- `R8`: Runtime behavior is exercised with real React in development and optimized production; processor outputs/diagnostics compile with the checker policy; the complete repository gate and independent reviews pass before the ready-for-review PR is published.

## Acceptance criteria

- `AC1` (`R1`, `R2`, `R3`, `R6`): Browser tests compose hand-written Buildable values, mixed nodes/builders, nulls, arrays and streams through core, context and DOM boundaries, checking rendered output, eager counts, ordering, props precedence and exception propagation in both modes.
- `AC2` (`R4`, `R5`, `R6`): Processor fixtures and integrated browser tests cover optional builders, required-step exclusion, scalar and array node inputs, custom enhancers, generic/nullness contracts, and renderable `@Render`/context results. Complete generated output sets and diagnostics are regenerated and compiled.
- `AC3` (`R7`): Published guide examples compile; canonical requirements and architectural rationale have one owner; migration notes identify changed enhancer signatures; document links and the site pass validation.
- `AC4` (`R8`): `tools/check.sh` passes under pinned Bazel 9.3.1, independent planning/implementation reviews report `Findings: none`, temporary plans are removed in the final closeout commit, and the branch is pushed to an attached ready-for-review PR.

## Significant decisions

| ID | Decision | Rationale | Impact | User verification |
| --- | --- | --- | --- | --- |
| D1 | Cover all typed composition boundaries. | The issue's benefit should not depend on the nesting location. | Includes render callbacks and node inputs, not just children. | Builders can be used at each documented boundary. |
| D2 | Migrate directly to Renderable consumers. | Avoid overload ambiguity and duplicate API paths. | Custom node-input enhancer setter signatures may require migration. | Migration example and public signatures. |
| D3 | Eager build per occurrence. | Match an explicit build at the supplied position. | No caching; context callbacks convert on invocation; errors propagate. | Evaluation-count and exception scenarios. |
| D4 | Native marker plus ordinary functional Buildable. | Preserve primitive/native nodes within J2CL constraints; see the [native boundary decision](../../docs/adr/0008-preserve-native-renderables.md). | No instance-method calls on native nodes; custom Buildable implementations work. | Real J2CL builds and optimized browser checks. |

## Technical decisions

- Centralize scalar and array conversion beside the public renderable/node contract; preserve each consumer's nullness without widening all inputs to nullable.
- Normalize only typed node boundaries; maintain raw node return types from actual materialization and native calls.
- Preserve array contents owned by callers. DOM converts the effective children after existing props/explicit-children selection.
- Preserve authored node input types while projecting builder-facing scalar/array signatures and enhancer validation to renderables; retain input nullness in projection.
- Use existing exposed build intrinsics to identify ready steps; do not add readiness state or per-step object wrappers.
- Use the cached Bazel 9.3.1 binary via PATH for all checks; do not modify repository version policy.

## Testing decisions

- Add meaningful scenarios to the existing checked Java browser suites and thin suite runners; use actual React assets.
- Extend focused processor fixture families, regenerate affected expected outputs with the repository updater, and inspect complete output changes.
- Start with focused runtime/processor checks, then run `tools/check.sh`. Keep durable coverage tests and remove temporary delivery evidence at closeout.
- Planning budget: three rounds plus one final confirmation. Implementation budget: five rounds plus one final confirmation; use separate read-only reviewers for the phases.

## Open questions

None.
