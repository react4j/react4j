# Implementation Plan

## Delivery Sequence
1. Create planning artifacts for the `@PostConstruct` naming warning and capture resolved decisions.
2. Add a dedicated warning key and a small early-parse helper in `React4jProcessor`.
3. Add warned, valid, suppressed, and multi-method boundary fixtures.
4. Verify with the focused processor test target.

## Delivery Approach
- Keep validation local to `React4jProcessor.parse(...)`, mirroring `verifyConstructorParameterOrder(...)`.
- Reuse the existing effective method set already collected for processor analysis.
- Count effective `@PostConstruct` methods, and only if there is exactly one, compare its simple name to `postConstruct`.
- Emit at most one warning on the method element and reuse standard React4j warning suppression handling.
- Reuse the existing warning test providers in `React4jProcessorTest`.

## High-Risk Areas
- Matching established warning phrasing and suppression behavior.
- Avoiding accidental broadening to multi-method `@PostConstruct` cases.
- Keeping the check independent from later descriptor logic.

## Mitigations
- Anchor the warning on the `@PostConstruct` method element and use `ElementsUtil.isWarningNotSuppressed(...)`.
- Add a dedicated multi-method no-warning fixture to pin the boundary.
- Run the focused processor test target after code and fixture updates.

## Decision Log
- `Q-01`: warning-only validation, no runtime or codegen changes.
  - Plan impact: processor-only warning helper; no generator/runtime work.
- `Q-02`: warn only when exactly one effective `@PostConstruct` exists and it is misnamed.
  - Plan impact: helper counts effective methods first and bails out for zero or multiple matches.
- `Q-03`: dedicated warning key `React4j:PostConstructName`.
  - Plan impact: add a new constant and dedicated suppression text.
- `Q-04`: fixed warning message with standard suppression wording.
  - Plan impact: use a stable literal diagnostic string, parallel to the constructor-order warning.
- `Q-05`: standard method/enclosing-type suppression.
  - Plan impact: warning is emitted once on the method element with existing suppression utilities.
- `Q-06`: no broader `@PostConstruct` lifecycle validation in this change.
  - Plan impact: keep implementation narrowly scoped to naming only.
- `Q-07`: mirror the constructor-warning fixture matrix.
  - Plan impact: add warned, valid, and suppressed fixtures and register them in existing providers.
- `Q-08`: lock in the multi-method no-warning boundary with a fixture.
  - Plan impact: add one explicit two-method fixture that compiles without this warning.
- `Q-09`: use a dedicated `com.example.post_construct` fixture package with parallel names.
  - Plan impact: isolate new fixtures under a focused package.
- `Q-10`: use the processor’s existing effective method set.
  - Plan impact: helper consumes the same `methods` list already built in `parse(...)`.
- `Q-11`: exact case-sensitive name match to `postConstruct`.
  - Plan impact: helper compares the simple name directly to `postConstruct`.
- `Q-12`: processor-only change, no docs/changelog work.
  - Plan impact: no public-doc updates included in task scope.
- `Q-13`: run the check early during `parse(...)`.
  - Plan impact: invoke the helper shortly after method collection and constructor validation.
- `Q-14`: multi-method fixture should not warn even if neither method is named `postConstruct`.
  - Plan impact: boundary fixture uses two misnamed methods and expects no warning.
- `Q-15`: keep dedicated plan artifacts under `plans/post-construct-name-warning/`.
  - Plan impact: retain the planning tree and record explicit user review before marking the plan accepted.

## Phase Details

### Phase 1
- Create `plans/post-construct-name-warning/` artifacts.
- Record resolved design decisions and request explicit user review before marking the plan accepted.

### Phase 2
- Add `React4j:PostConstructName` to `processor/src/main/java/react4j/processor/Constants.java`.
- Implement a `@PostConstruct` naming warning helper in `processor/src/main/java/react4j/processor/React4jProcessor.java`.

### Phase 3
- Add fixtures under `processor/src/test/fixtures/input/com/example/post_construct/`:
  - warned misnamed single-method case
  - valid correctly named single-method case
  - method-level suppression case
  - type-level suppression case
  - two-method no-warning boundary case
- Register them in `processor/src/test/java/react4j/processor/React4jProcessorTest.java`.

### Phase 4
- Run `bundle exec buildr react4j:processor:test`.
- If failures occur, adjust the warning message or fixtures and rerun the same gate.

## Required Full-Gate Command
- `bundle exec buildr test`
