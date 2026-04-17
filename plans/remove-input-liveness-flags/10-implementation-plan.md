# Implementation Plan

## Delivery Sequence
1. Remove the three liveness members from `@Input` and update API/docs fixtures for the source break.
2. Introduce a handwritten-member observation model in the processor, including scan and validation logic.
3. Rebuild tracked-render prelude generation on top of handwritten candidates and delete input-derived liveness branches.
4. Refresh fixtures, api diff assets, and docs/changelog, then run validation gates.

## Delivery Approach
- Treat this as a standalone Phase 4 initiative with no dependency on edits to the earlier migration plan tree.
- Model handwritten observation candidates separately from `InputDescriptor` to avoid preserving obsolete input-liveness concepts.
- Reuse existing inherited-member scanning patterns in `React4jProcessor` for fields and methods wherever possible.
- Preserve current direct-observe vs runtime-check behavior, but swap nullable direct observation to `ComponentObservable.maybeObserve(...)`.

## High-Risk Areas
- Defining candidate member accessibility and shape rules tightly enough that generation is predictable.
- Removing `hasDependencyInput()`-driven lifecycle/render behavior without leaving stale branches in `ViewGenerator` and `BuilderGenerator`.
- Distinguishing observable-capable candidates from impossible candidates in a way that produces useful processor errors.
- Updating fixtures that currently rely on `@Input` liveness flags without conflating Phase 4 behavior with Arez-only semantics.

## Mitigations
- Add a dedicated descriptor for handwritten observation candidates rather than overloading input metadata.
- Restrict candidate members to subclass-accessible instance fields and zero-arg instance methods with return values.
- Keep render-prelude observation limited to tracked and maybe-tracking views to preserve the current Arez observation model.
- Treat impossible explicit candidates as compile-time errors instead of silently degrading behavior.
- Add focused fixtures for inherited members, private-member failures, nullable candidates, and runtime-check candidates before broader fixture refresh.

## Decision Log
- `Q-01`: Phase 4 is immediate removal, not a deprecation bridge.
  - Plan impact: delete `Input` members in the same change set that introduces handwritten-member scanning.
- `Q-02`: `arez.annotations.AutoObserve` is a render-prelude candidate annotation.
  - Plan impact: candidate scan and validation cover `@AutoObserve`.
- `Q-03`: Only subclass-accessible members participate.
  - Plan impact: private fields/methods are invalid as Phase 4 observation candidates.
- `Q-04`: Render-prelude observation is tracking-only.
  - Plan impact: non-tracking fixtures must show no observation prelude.
- `Q-05`: Candidate annotations are `@ComponentDependency` and `@AutoObserve`, not `@CascadeDispose`.
  - Plan impact: no scan or generation path for `@CascadeDispose`.
- `Q-06`: Candidate methods are zero-arg instance methods with accessor-like purity assumptions.
  - Plan impact: processor validates method shape and documentation must call out the invocation contract.

## Phase Details

### Phase 4A
- Remove `disposable`, `observeOnRender`, and `dependency` from `react4j.annotations.Input`.
- Remove related Javadoc and replace it with guidance that handwritten Arez members drive liveness/observation behavior.
- Update api-diff fixtures for the removed annotation members.

### Phase 4B
- Add a new processor model for handwritten render-prelude candidates on `ViewDescriptor`.
- Add processor discovery for declared and inherited:
  - subclass-accessible instance fields annotated with `@ComponentDependency` or `@AutoObserve`
  - subclass-accessible zero-arg instance methods returning a value and annotated with `@ComponentDependency` or `@AutoObserve`
- Validate candidate observability:
  - directly assignable to `ComponentObservable`
  - nullable direct observable type
  - runtime-checkable type such as `@ArezComponentLike` or other non-final/reference scenarios
- Reject candidates that can never be observed.

### Phase 4C
- Rewrite the tracked-render prelude in `ViewGenerator` to iterate handwritten candidate descriptors instead of inputs.
- Generate:
  - `ComponentObservable.observe(value)` for known non-null observable candidates
  - `ComponentObservable.maybeObserve(value)` for known nullable observable candidates
  - `value instanceof ComponentObservable && !ComponentObservable.observe(value)` for runtime-check candidates
- Return `null` from render when a candidate fails observation.
- Remove input-derived render-prelude logic for `disposable`, `observeOnRender`, and `dependency`.
- Remove input-derived lifecycle/render wrapper behavior and native-view constructor disposed checks that are gated by dependency-input metadata.
- Remove input-derived dependency assertions in `BuilderGenerator`.

### Phase 4D
- Refresh processor success/failure fixtures and expected output.
- Update docs/changelog to describe the new handwritten-member contract and the intentional reduction for forwarded unretained constructor inputs.
- Run fast, compatibility, and full validation gates.

## Required Full-Gate Command
- `bundle exec buildr test`
