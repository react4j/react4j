# Constructor-Based Immutable Inputs Migration

## Mission
- Migrate immutable inputs from method-only declarations to constructor parameters while preserving generated behavior and allowing staged downstream adoption.

## Scope
- Annotation API changes in `core`.
- Annotation processor parsing, model, validation, and generation changes in `processor`.
- Processor fixtures, warning coverage, and API diff updates.
- Docs/changelog updates for each migration phase.

## Non-Negotiables
- Keep immutable-input behavior parity for builders, validation, synthetic keys, dependency/disposable handling, Sting integration, and lifecycle timing.
- Deliver three releaseable phases with clean stopping points.
- Support mixed old/new immutable input declarations on the same view during Phase 1 and Phase 2.
- Reject duplicate immutable input names across methods and constructor parameters.
- Restrict constructor `@Input` to immutable inputs until Phase 3.
- Maintain extensive annotation processor test coverage.

## Behavior Expectations
- Phase 1:
  - `@Input` can target constructor parameters.
  - Constructor parameters annotated with `@Input` are valid only when `immutable = true`.
  - Method-based immutable inputs continue to work unchanged.
  - Generated creation paths correctly combine injected services with immutable constructor inputs.
- Phase 2:
  - Method-based immutable inputs compile with a suppressible warning.
- Phase 3:
  - Method-based immutable inputs are removed.
  - `immutable` is removed from `@Input`.
  - Constructor-parameter `@Input` becomes the immutable-input form.

## Quality Gates
- Fast gate per phase: `bundle exec buildr react4j:processor:test`
- Full gate before final completion: `bundle exec buildr test`
- Compatibility gate for Phase 1 and Phase 3 surface changes: `bundle exec buildr test_api_diff`

## Intentional Defaults
- Mixed-mode migration is allowed in Phase 1 and Phase 2.
- Name-based features (`@InputDefault`, `@InputValidate`) continue to bind by resolved input name regardless of origin.
- `@OnInputChange` remains invalid for immutable inputs of either origin.

## Open Questions Register
- `Q-01`
  - status: resolved
  - question: Can a single view mix method-based and constructor-parameter immutable inputs during Phase 1 and Phase 2?
  - user_decision: Yes; allow mixing and reject only duplicate input names.
  - artifacts_updated: `00-requirements.md`, `10-implementation-plan.md`
- `Q-02`
  - status: resolved
  - question: Should constructor-parameter `@Input` in Phase 1 accept mutable inputs?
  - user_decision: No; constructor-parameter `@Input` is restricted to immutable inputs until Phase 3.
  - artifacts_updated: `00-requirements.md`, `10-implementation-plan.md`
