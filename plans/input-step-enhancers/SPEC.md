# Input Step Enhancers Spec

## Source

- Accepted scope: issue [76](https://github.com/react4j/react4j/issues/76), the interview in this chat, and the user's final `accept`.
- Execution authority: the user requested implementation using structured-delivery-workflow and a PR for review. There is no human plan checkpoint and no authority to merge or release.
- Correction: the user reversed 3B to 3A. Preserve the built-in children stream generation and behavior; do not migrate it to enhancers.
- Evidence: `Input`, both input-descriptor creation paths in `React4jProcessor`, `BuilderGenerator`, strict processor goldens, and standalone Java compiler/runtime probes. Input ordering and requiredness already have established contracts.

## Problem

Component authors cannot add typed presets or conversions to generated input builder steps. Callers must construct the exact input value before invoking the setter.

## Required outcome

An input can select a consumer-owned interface whose default methods delegate to its generated setter. Helpers preserve the setter's continuation, including terminal results, and first-step helpers have matching static shortcuts.

## Scope

- In scope: method and constructor inputs; inherited and precompiled enhancers; generic helper methods; checked exceptions; static first-step helpers; clear invalid-configuration diagnostics; Java/JSpecify and J2CL verification; user documentation.
- Out of scope: built-in children migration, generic payload binding, multiple configured enhancers per input, separate stage implementations, merging, publishing and releases.

## Constraints

- Retain one builder implementation and existing input ordering, requiredness, defaults, key handling and children behavior.
- Follow CONTRIBUTING, explicit Bazel source inventories, Yoda comparisons and existing nullness policy. Leave `etc/secrets` untouched.
- Do not infer purity for user-authored default methods.

## Requirements

- R1: `@Input(enhancer=Example.class)` selects one accessible interface with one unbounded result type parameter, one effective abstract setter matching the input's generated name and Java value type, and compatible JSpecify parameter nullness. Context-only inputs cannot select a builder enhancer.
- R2: Public effective default helpers return the result type parameter directly. Support inherited defaults, method type parameters, ordinary parameters and varargs, and declared checked exceptions. Extra abstract capabilities and incompatible signatures are rejected.
- R3: A helper has the canonical setter's continuation: next required stage, current optional stage, or `ReactNode` when terminal. It cannot remove remaining required calls from the caller's type contract.
- R4: Every helper on the first stage has a static builder shortcut preserving its signature, continuation, nullness and exceptions. Helpers on later stages have no static shortcut.
- R5: Reject conflicts against generated members and other enhancers, including erasure and incompatible generic ancestors across the shared builder implementation, with actionable processor diagnostics. Legal compatible inheritance and overloads remain usable.
- R6: Components without enhancers retain their generated behavior; existing child/children generation and stream termination remain unchanged.

## Acceptance criteria

- AC1 (R1-R3): Source and precompiled fixtures compile with strict checkers for method/constructor inputs, fixed primitives and reference payloads, renamed inputs, inherited defaults, and advancing/staying/terminal helpers.
- AC2 (R1-R2,R5): Negative fixtures diagnose malformed/inaccessible interfaces, incorrect setter/result/nullness, additional abstract methods, tree-context configuration, erasure/default conflicts and generic-parent conflicts.
- AC3 (R2-R4): Static-call fixtures compile and execute with generic helper methods, varargs, checked exceptions, inherited signatures and generic view/helper name collisions. Invalid early completion fails compilation.
- AC4 (R3,R6): Real browser scenarios in development and production prove helper-written values reach the rendered view and preserve continuation; existing children goldens remain unchanged.
- AC5 (R1-R6): Public author guidance, a canonical durable contract, changelog and explicit build inventories are current; `tools/check.sh` passes before implementation review.

## Significant decisions

| ID | Decision | Rationale | Impact | User verification |
| --- | --- | --- | --- | --- |
| D1 | Fixed payload, one result parameter | Matches the accepted issue use case | No generic value-binding convention | Review the author example |
| D2 | Direct result-returning defaults with full Java helper signatures | Keeps helpers fluent without forbidding normal typed conversions | Generic and exception-preserving forwarding is required | Review static and terminal examples |
| D3 | Keep one builder; diagnose conflicts | Avoids allocating stage adapters | Incompatible cross-stage helper signatures are rejected | Review conflict diagnostics |
| D4 | Preserve children intrinsic | Explicit user correction to 3A | Existing stream behavior remains | Confirm no children golden churn |

## Technical decisions

- Parse annotation type mirrors without loading consumer classes; resolve inherited enhancer members before validation.
- Generate step superinterfaces using the existing continuation calculation; retain canonical generated setters and their runtime paths.
- Derive static forwarding signatures with result substitution and type-variable hygiene. User default bodies stay in their interfaces.
- Promote the accepted public behavior to `docs/specs/input-step-enhancers.md` before implementation review; technical sequencing remains only in this temporary tree.

## Testing decisions

- Use complete formatted/unformatted goldens and strict generated-body compilation, plus source/precompiled enhancer tests and invalid call-site compilations.
- Exercise real J2CL/React in the browser, not only JVM interface surrogates. Preserve existing children goldens as compatibility evidence.
- Run focused task checks, then the project's complete gate; retain final evidence in the PR and git history before deleting this tree.

## Open questions

None.
