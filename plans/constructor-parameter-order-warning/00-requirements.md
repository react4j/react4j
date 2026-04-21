# Constructor Parameter Order Warning

## Mission
- Add a suppressible React4j processor warning when an explicit `@View` constructor declares parameters outside the canonical order.

## Scope
- `processor` warning logic and suppression key.
- Processor fixtures/tests for warned, suppressed, and valid cases.

## Scope Boundaries
- Do not change generated constructor or factory argument order.
- Do not turn the condition into a compile error.
- Do not update public docs or Javadoc.
- Do not edit `AGENTS.md`.

## Non-Negotiables
- Apply the rule only to explicit `@View` constructors.
- Use one warning per constructor, attached to the constructor element.
- Respect normal React4j warning suppression via `@SuppressWarnings` and `@SuppressReact4jWarnings` on the constructor or enclosing type.
- Ignore parameter-level suppression.
- Canonical group order is fixed as `inject`, `tree`, `input`.

## Behavior Expectations
- Group parameters as:
  - `inject`: parameters without `@Input`
  - `tree`: parameters with `@Input(fromTreeContext = true)`
  - `input`: parameters with `@Input` and `fromTreeContext = false`
- Emit a warning if constructor parameter group indices decrease during a left-to-right scan.
- Warning text includes:
  - fixed expected order `inject, tree, input`
  - actual source-order group sequence using the same labels
- Constructors with zero or one parameter do not warn.
- Constructors already in canonical order compile without warnings.

## Quality Gates
- Fast gate: `bundle exec buildr react4j:processor:test`
- Full gate for reference only: `bundle exec buildr test`

## Intentional Divergences
- This is a style/maintainability warning even when current code generation would still work.
- No user-facing documentation update is included in this change.

## Open Questions Register
- `Q-01`
  - status: resolved
  - question: Should this be a warning-only validation with no generation behavior changes?
  - context: Existing code generation preserves declared constructor order, so silent reordering would be invasive.
  - options:
    - warning-only validation
    - rewrite generated argument ordering
  - tradeoffs:
    - warning-only matches current architecture and avoids behavior changes
    - rewriting generation introduces hidden semantics and more risk
  - recommended_default: warning-only validation
  - user_decision: warning-only validation with no generation behavior changes
  - artifacts_updated: `00-requirements.md`, `10-implementation-plan.md`, `20-task-board.yaml`
- `Q-02`
  - status: resolved
  - question: What is the canonical constructor parameter order?
  - context: The warning needs an exact rule and message.
  - options:
    - `inject`, then `tree`, then `input`
    - some other ordering
  - tradeoffs:
    - `inject`, `tree`, `input` matches constructor injection semantics and tree-context precedence
    - other orderings would be less aligned with current processor behavior
  - recommended_default: `inject`, `tree`, `input`
  - user_decision: `inject`, `tree`, `input`
  - artifacts_updated: `00-requirements.md`, `10-implementation-plan.md`, `20-task-board.yaml`
- `Q-03`
  - status: resolved
  - question: How should warnings be emitted and suppressed?
  - context: The project already has a consistent warning model.
  - options:
    - one constructor-level warning with standard suppression
    - per-parameter warnings or custom suppression scope
  - tradeoffs:
    - one constructor-level warning is concise and matches the source of the problem
    - per-parameter warnings create noise and ambiguous suppression behavior
  - recommended_default: one constructor-level warning with standard suppression
  - user_decision: one constructor-level warning, suppressible via standard React4j warning suppression on the constructor or enclosing type
  - artifacts_updated: `00-requirements.md`, `10-implementation-plan.md`, `20-task-board.yaml`
- `Q-04`
  - status: resolved
  - question: What should the warning message include?
  - context: The diagnostic should be actionable without dumping full signatures.
  - options:
    - expected order only
    - expected order plus actual compact group sequence
  - tradeoffs:
    - including the compact actual sequence makes the violation obvious
    - expected-only messaging is less informative during triage
  - recommended_default: include expected order and actual compact group sequence
  - user_decision: include expected order `inject, tree, input` and actual compact group sequence labels
  - artifacts_updated: `00-requirements.md`, `10-implementation-plan.md`, `20-task-board.yaml`
- `Q-05`
  - status: resolved
  - question: How should verification be structured?
  - context: The repo has an established processor-warning test style.
  - options:
    - dedicated warned/suppressed/valid fixtures in the existing providers
    - ad hoc assertions or folded coverage
  - tradeoffs:
    - dedicated fixtures keep the rule obvious and align with project conventions
    - folded coverage is harder to read and maintain
  - recommended_default: dedicated fixtures integrated into existing providers
  - user_decision: add dedicated warned, suppressed, and valid fixtures using the existing warning test providers
  - artifacts_updated: `00-requirements.md`, `10-implementation-plan.md`, `20-task-board.yaml`
