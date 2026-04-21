# Implementation Plan

## Delivery Sequence
1. Add planning artifacts for the constructor parameter order warning.
2. Introduce the new warning key and constructor-order validation helper in `React4jProcessor`.
3. Add minimal constructor-focused fixtures for warned, suppressed, and valid cases.
4. Verify with the processor test target.

## Delivery Approach
- Keep validation local to `React4jProcessor.parse(...)`, matching the existing warning architecture.
- Classify constructor parameters using existing `isInputParameter(...)` and `isFromTreeContextInput(...)` helpers.
- Use a monotonic left-to-right group scan and emit at most one warning per constructor.
- Reuse existing warning test providers rather than adding a new harness.

## High-Risk Areas
- Matching existing warning phrasing and suppression behavior.
- Ensuring suppression applies to constructors and enclosing types but not parameters.
- Avoiding false positives on valid explicit constructors.

## Mitigations
- Anchor the warning on the constructor element and reuse `ElementsUtil.isWarningNotSuppressed(...)`.
- Add one explicit valid fixture and multiple suppression fixtures.
- Run the focused processor test task after the code and fixture updates.

## Decision Log
- `Q-01`: warning-only validation, no generation changes.
  - Plan impact: processor-only structural validation in `parse(...)`.
- `Q-02`: canonical order is `inject`, `tree`, `input`.
  - Plan impact: group classifier and fixed expected-order text use those labels.
- `Q-03`: one constructor-level warning with standard suppression.
  - Plan impact: helper emits a single `Diagnostic.Kind.WARNING` on the constructor element.
- `Q-04`: include compact actual group sequence in the message.
  - Plan impact: helper builds a stable comma-separated label sequence from source order.
- `Q-05`: use dedicated fixtures in the existing warning providers.
  - Plan impact: update `successfulCompiles`, `compileWithWarnings`, and `compileWithoutWarnings`.

## Phase Details

### Phase 1
- Add `React4j:ConstructorParameterOrder` to `processor/src/main/java/react4j/processor/Constants.java`.
- Implement a constructor-order warning helper in `React4jProcessor`.

### Phase 2
- Call the helper during `parse(...)` after constructor validation and before later descriptor/generator work.
- Keep helper logic source-oriented and avoid dependency on `InputDescriptor`.

### Phase 3
- Add a valid explicit-constructor fixture under `processor/src/test/fixtures/input/com/example/constructor/`.
- Add a misordered warning fixture plus constructor-level and type-level suppression variants.
- Register the fixtures in the existing test providers.

### Phase 4
- Run `bundle exec buildr react4j:processor:test`.
- If failures occur, adjust messaging/fixtures and rerun the same gate.

## Required Full-Gate Command
- `bundle exec buildr test`
