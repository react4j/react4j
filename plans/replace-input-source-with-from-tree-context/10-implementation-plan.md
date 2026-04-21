# Implementation Plan

## Delivery Sequence
1. Create and approve planning artifacts for the `fromTreeContext` migration.
2. Replace the public annotation member and remove `Input.Source`.
3. Rename processor parsing/model terminology and preserve tree-context behavior.
4. Refresh fixtures, diagnostics, changelog, and api-diff artifacts.
5. Run targeted verification gates and record any acceptable downstream fallout.

## Delivery Approach
- Treat this as a hard-cut source break with no compatibility path.
- Keep behavior constant and limit changes to API shape, terminology, diagnostics, and supporting artifacts.
- Update durable internal names to match the public flag exactly where they describe the old annotation model.
- Avoid unrelated generated-code design changes; keep runtime/builder `Context*` identifiers as-is.

## High-Risk Areas
- Removing `Input.Source` without leaving stale processor parsing code or Javadoc references.
- Preserving requiredness/default/builder/runtime behavior while renaming the selector syntax.
- Refreshing all fixture inputs and exact diagnostic expectations consistently.
- Updating api-diff artifacts to reflect one removed member, one removed enum, and one new boolean member.

## Mitigations
- Change the annotation and processor parsing first, then refresh fixture inputs and diagnostics in one pass.
- Use targeted ripgrep scans for `source=CONTEXT`, `Input.Source`, `source()`, and `TreeInput` before and after edits.
- Keep builder/runtime generation logic behaviorally intact by renaming internal predicates rather than redesigning the flow.
- Validate with processor tests and api-diff tests before considering the task complete.

## Decision Log
- `Q-01`: `qualifier` remains valid only with tree-context sourcing.
  - Plan impact: preserve validation rule and update diagnostics to `fromTreeContext=true`.
- `Q-02`: requiredness semantics remain unchanged.
  - Plan impact: keep tree-context inputs optional-by-default and continue rejecting `require = ENABLE`.
- `Q-03`: both method and constructor-parameter inputs continue to support tree-context sourcing.
  - Plan impact: update both method and parameter parsing paths.
- `Q-04`: public docs and diagnostics drop `source=CONTEXT` and `TreeInput`.
  - Plan impact: refresh Javadoc, errors, tests, and changelog wording.
- `Q-05`: the public member name is exactly `fromTreeContext`.
  - Plan impact: add `boolean fromTreeContext() default false;`.
- `Q-06`: the migration is an atomic hard cut.
  - Plan impact: rewrite fixtures and remove the enum in the same change set.
- `Q-07`: diagnostics should be cleaned up while renamed.
  - Plan impact: revise message strings and update exact test expectations.
- `Q-08`: internal naming mirrors `fromTreeContext`.
  - Plan impact: rename durable processor fields/methods/predicates.
- `Q-09`: the break is explicitly documented.
  - Plan impact: add a changelog entry and update api-diff fixtures.
- `Q-10`: external downstream failures are acceptable.
  - Plan impact: record downstream-only failure as acceptable if it occurs.
- `Q-11`: targeted in-repo verification is the acceptance bar.
  - Plan impact: required gates are processor tests and api-diff tests.
- `Q-12`: all fixtures move to the new syntax and no old-syntax rejection tests are added.
  - Plan impact: refresh all positive/bad-input fixtures that reference the removed syntax.
- `Q-13`: perform a full terminology purge except generated/runtime `Context*` identifiers.
  - Plan impact: rename durable concept-bearing names and comments but do not redesign generated context helper names.
- `Q-14`: scope remains limited to the live `@Input` API.
  - Plan impact: preserve historical `@Prop` changelog/api-fixture references unless regenerated live artifacts require change.

## Phase Details

### Phase 1
- Add `fromTreeContext()` to `react4j.annotations.Input` with default `false`.
- Remove `source()` and the nested `Source` enum.
- Rewrite `Input` Javadoc to describe builder-supplied inputs vs tree-context inputs using the new flag name.

### Phase 2
- Update processor parsing for method and constructor-parameter inputs to read `fromTreeContext`.
- Rename durable internals such as `_contextSource`, `isContextSource()`, and `isContextInput(...)`.
- Preserve validation and generation behavior for qualifier handling, requiredness, defaults, builder omission, and runtime validation.
- Clean up affected diagnostics to use the new terminology and better wording.

### Phase 3
- Rewrite all fixture sources using `source = Input.Source.CONTEXT` to the new flag.
- Update expected diagnostics in `React4jProcessorTest`.
- Refresh expected generated outputs only as needed by regeneration.
- Update `CHANGELOG.md` with an explicit breaking-change entry.
- Update api-diff fixtures for the public annotation surface change.

### Phase 4
- Run:
  - `bundle exec buildr react4j:processor:test`
  - `bundle exec buildr test_api_diff`
- Optionally run `bundle exec buildr test` to identify acceptable external downstream fallout, but do not require it for acceptance.

## Required Full-Gate Command
- `bundle exec buildr test`
