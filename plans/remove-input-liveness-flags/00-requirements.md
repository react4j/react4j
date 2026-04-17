# Remove Input Liveness Flags

## Mission
- Remove React4j liveness configuration from `@Input` and replace tracked-render observation with explicit handwritten Arez members on the view type hierarchy.

## Scope
- Annotation API changes in `core`.
- Annotation processor parsing, validation, model, and generation changes in `processor`.
- Processor fixtures, api-diff fixtures, and documentation/changelog updates.

## Non-Negotiables
- Phase 4 is an immediate breaking change; there is no deprecation bridge.
- Remove `Input.disposable()`, `Input.observeOnRender()`, and `Input.dependency()`.
- React4j MUST stop deriving liveness semantics from `@Input`.
- Tracked-render prelude observation MUST be driven only by explicit handwritten Arez members.
- Only subclass-accessible handwritten members participate; private members are ignored and rejected if explicitly relied upon.
- `@CascadeDispose` is not a render-prelude observation signal.
- Maintain annotation processor coverage for both success and failure cases.

## Behavior Expectations
- `@Input` continues to describe only input name/source/requiredness/observability concerns unrelated to liveness flags.
- React4j scans declared and inherited handwritten members for render-prelude observation candidates.
- Candidate annotations are:
  - `arez.annotations.ComponentDependency`
  - `arez.annotations.AutoObserve`
- Candidate member shapes are:
  - instance fields that are subclass-accessible
  - zero-arg instance methods returning a value and subclass-accessible
- Candidate methods are treated as accessor-like and side-effect free; React4j may invoke them during the render prelude.
- Render-prelude observation applies only to `View.Type.TRACKING` and `View.Type.MAYBE_TRACKING`.
- Non-tracking views do not observe handwritten candidates in render prelude.
- Observation generation rules are:
  - known non-null observable type: `ComponentObservable.observe(value)`
  - known nullable observable type: `ComponentObservable.maybeObserve(value)`
  - runtime-checkable type: `value instanceof ComponentObservable && !ComponentObservable.observe(value)`
- If the processor encounters an explicit candidate member that can never be observed, compilation fails.
- If an immutable constructor input is forwarded elsewhere and not retained in an analyzable handwritten member, React4j provides no special liveness behavior for that value.
- Arez remains responsible for component dependency/disposal semantics beyond React4j's tracked-render prelude.

## Quality Gates
- Fast gate: `bundle exec buildr react4j:processor:test`
- Compatibility gate: `bundle exec buildr test_api_diff`
- Full gate: `bundle exec buildr test`

## Intentional Divergences
- Phase 4 intentionally removes input-derived pre-construction disposed checks and input-derived lifecycle/render wrapper behavior.
- Phase 4 intentionally reduces behavior for forwarded-but-unretained immutable constructor inputs.

## Open Questions Register
- `Q-01`
  - status: resolved
  - question: Should Phase 4 remove `disposable`, `observeOnRender`, and `dependency` immediately or stage the migration?
  - context: This determines whether implementation must preserve legacy flags for a compatibility window.
  - options:
    - immediate removal
    - staged deprecation then removal
  - tradeoffs:
    - immediate removal simplifies processor design but is a direct source break
    - staged migration reduces breakage but adds transitional complexity
  - recommended_default: immediate removal to keep the Phase 4 contract explicit and avoid dual behavior
  - user_decision: immediate removal of `disposable`/`observeOnRender`/`dependency`
  - artifacts_updated: `00-requirements.md`, `10-implementation-plan.md`, `20-task-board.yaml`
- `Q-02`
  - status: resolved
  - question: Which handwritten Arez annotation should React4j treat as an explicit render-prelude observation signal alongside `@ComponentDependency`?
  - context: The new candidate scan needs a concrete annotation list.
  - options:
    - `arez.annotations.AutoObserve`
    - another Arez annotation
  - tradeoffs:
    - choosing `@AutoObserve` aligns the prelude with explicit handwritten observation semantics
    - choosing another annotation changes both scan rules and fixture coverage
  - recommended_default: `arez.annotations.AutoObserve`
  - user_decision: `arez.annotations.AutoObserve`
  - artifacts_updated: `00-requirements.md`, `10-implementation-plan.md`, `20-task-board.yaml`
- `Q-03`
  - status: resolved
  - question: Should private handwritten members participate in the new scan?
  - context: The generated `React4j_*` subclass can only access subclass-callable members without adding a new access strategy.
  - options:
    - only subclass-accessible members
    - support private members via an alternate access mechanism
  - tradeoffs:
    - subclass-accessible only keeps generation simple and explicit
    - private-member support adds complexity and additional validation rules
  - recommended_default: only subclass-accessible members participate
  - user_decision: only subclass-accessible members participate
  - artifacts_updated: `00-requirements.md`, `10-implementation-plan.md`, `20-task-board.yaml`
- `Q-04`
  - status: resolved
  - question: Should the new render-prelude observation apply to all views or only tracked renders?
  - context: `ComponentObservable.observe(...)` is currently meaningful inside tracked render observation.
  - options:
    - tracking and maybe-tracking only
    - all views
    - split behavior by view type
  - tradeoffs:
    - tracking-only preserves current render observation model
    - all-views requires specifying new non-tracking semantics
  - recommended_default: tracking and maybe-tracking only
  - user_decision: tracking only
  - artifacts_updated: `00-requirements.md`, `10-implementation-plan.md`, `20-task-board.yaml`
- `Q-05`
  - status: resolved
  - question: Which handwritten annotations qualify as render-prelude candidates?
  - context: The scan must distinguish observation signals from disposal-only annotations.
  - options:
    - `@ComponentDependency` and `@AutoObserve`
    - `@ComponentDependency`, `@AutoObserve`, and `@CascadeDispose`
    - other combinations
  - tradeoffs:
    - excluding `@CascadeDispose` avoids treating disposal-only ownership as an observation contract
    - including `@CascadeDispose` would conflate distinct semantics
  - recommended_default: `@ComponentDependency` and `@AutoObserve`
  - user_decision: dependency plus AutoObserve
  - artifacts_updated: `00-requirements.md`, `10-implementation-plan.md`, `20-task-board.yaml`
- `Q-06`
  - status: resolved
  - question: What method shapes may serve as handwritten render-prelude candidates?
  - context: The generator needs a safe invocation contract for annotated methods.
  - options:
    - zero-arg instance methods returning a value
    - accessor-named methods only
    - fields only
  - tradeoffs:
    - zero-arg instance methods are flexible while still safe to invoke predictably
    - more restrictive options reduce utility without materially reducing generator complexity
  - recommended_default: zero-arg instance methods returning a value
  - user_decision: zero-arg instance methods with accessor-like purity assumptions
  - artifacts_updated: `00-requirements.md`, `10-implementation-plan.md`, `20-task-board.yaml`
