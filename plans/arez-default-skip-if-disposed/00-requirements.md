# Emit Arez `defaultSkipIfDisposed`

## Mission
- Update generated `@ArezComponent(...)` annotations to emit `defaultSkipIfDisposed = Feature.ENABLE`.

## Scope
- Generator logic in `processor`.
- Processor expected fixtures.
- Planning artifacts for this change.

## Scope Boundaries
- Do not change `@Observe(...)` generation.
- Do not change public APIs in `core` or `dom`.
- Do not edit `AGENTS.md`.

## Non-Negotiables
- Every generated `@ArezComponent(...)` explicitly sets `defaultSkipIfDisposed = Feature.ENABLE`.
- Generated classes suppress `Arez:UnnecessaryDefault`.
- Non-tracking generated classes continue suppressing `Arez:UnnecessaryAllowEmpty`.
- Tracking/non-tracking behavior besides the new Arez default remains unchanged.

## Behavior Expectations
- Tracking generated classes emit `@SuppressArezWarnings("Arez:UnnecessaryDefault")`.
- Non-tracking generated classes emit one `@SuppressArezWarnings(...)` annotation containing both `Arez:UnnecessaryDefault` and `Arez:UnnecessaryAllowEmpty`.
- Existing conditional `allowEmpty` and `defaultPriority` emission remains intact.

## Quality Gates
- Fast gate: `bundle exec buildr react4j:processor:test`
- Full gate for reference only: `bundle exec buildr test`

## Intentional Divergences
- No dependency version changes are included.
- No additional Arez annotation defaults are changed beyond `@ArezComponent.defaultSkipIfDisposed`.

## Open Questions Register
- None. The implementation plan was user-specified and approved in-thread before execution.
