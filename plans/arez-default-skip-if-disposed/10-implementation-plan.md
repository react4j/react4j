# Implementation Plan

## Delivery Sequence
1. Add planning artifacts for the Arez generator update.
2. Update `ViewGenerator` to emit the new `@ArezComponent` member and consolidated Arez suppressions.
3. Refresh processor expected fixtures.
4. Run processor verification and record evidence.

## Delivery Approach
- Keep the code change localized to `ViewGenerator`.
- Refresh expected outputs from the existing processor fixture suite rather than hand-editing generated files.
- Limit verification to the processor gate because the change only affects generated processor output.

## High-Risk Areas
- Rendering multi-value `@SuppressArezWarnings` correctly.
- Updating all expected fixtures consistently without touching unrelated generated output.

## Mitigations
- Use the existing fixture suite as the source of truth for generated output.
- Verify both tracking and non-tracking generated classes after the refresh.

## Phase Details

### Phase 1
- Add `defaultSkipIfDisposed = Feature.ENABLE` to the generated `@ArezComponent(...)`.
- Emit a single generated `@SuppressArezWarnings(...)` annotation per class with the right warning set by tracking mode.
- Keep generated boolean-returning `@Action` methods valid by explicitly emitting `skipIfDisposed = Feature.DISABLE` where the component-level default would otherwise apply.

### Phase 2
- Refresh processor expected fixtures to capture the generated annotation changes.

### Phase 3
- Run `bundle exec buildr react4j:processor:test`.

## Required Full-Gate Command
- `bundle exec buildr react4j:processor:test`
