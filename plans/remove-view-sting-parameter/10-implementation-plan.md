# Implementation Plan

## Delivery Sequence
1. Create and approve planning artifacts for the `@View.sting()` removal.
2. Remove the annotation member and simplify processor inference to constructor-driven behavior.
3. Refresh processor tests/fixtures, docs/examples, changelog, and API-diff artifacts.
4. Run targeted verification gates and record any acceptable downstream fallout.

## Delivery Approach
- Treat this as a hard-cut API removal with no compatibility path.
- Preserve runtime and generation behavior for constructor injection; only remove the explicit override mechanism.
- Keep Sting integration activation best-effort based on constructor injection plus Sting availability on the processor classpath.
- Leave unrelated Arez-generation behavior untouched.

## High-Risk Areas
- Removing `@View.sting()` without leaving stale annotation parsing or validation behind.
- Preserving factory generation for constructor injection while dropping explicit override-only tests.
- Keeping type-level and parameter-level Sting annotation propagation intact.
- Updating API-diff fixtures to reflect the intentional public API break cleanly.

## Mitigations
- Change the annotation and processor logic first, then refresh test fixtures in one pass.
- Use targeted searches for `sting = Feature`, `.sting()`, and `have specified sting=` before and after edits.
- Keep positive constructor-injection fixtures that still prove named/type/raw handling.
- Validate with processor and API-diff tests before closing the work.

## Decision Log
- `Q-01`: constructor injection keeps generating factories exactly as before.
  - Plan impact: retain `needsInjection()`-driven factory generation and constructor-based Sting inference.
- `Q-02`: no new targeted React4j error for missing Sting integration.
  - Plan impact: remove old flag-based validation logic rather than replacing it with new special-case diagnostics.
- `Q-03`: document the break explicitly.
  - Plan impact: update `CHANGELOG.md` and API-diff fixtures.
- `Q-04`: sweep docs/examples now.
  - Plan impact: refresh `@View` Javadoc and any user-facing guidance referencing annotation-controlled Sting toggles.
- `Q-05`: do not preserve dedicated forced-disable coverage.
  - Plan impact: remove fixtures/tests that existed only to force the non-Sting path.
- `Q-06`: rely on javac to reject old `@View(sting = ...)` source.
  - Plan impact: no dedicated compatibility-failure test is added.
- `Q-07`: keep `@ActAsStingComponent`.
  - Plan impact: `@View` remains Sting-compatible for type-level annotations.
- `Q-08`: keep generated Arez `sting = Feature.DISABLE` unchanged.
  - Plan impact: limit edits to the React4j annotation contract and processor inference.
- `Q-09`: remove obsolete override-specific negative tests, keep positive named/type/raw coverage.
  - Plan impact: delete bad-input cases for explicit `ENABLE`/`DISABLE`, retain valuable constructor-injection fixtures.
- `Q-10`: record the break in API-diff fixtures.
  - Plan impact: refresh Revapi fixture data instead of suppressing the change.

## Phase Details

### Phase 1
- Remove `sting()` from `core/src/main/java/react4j/annotations/View.java`.
- Rewrite `@View` Javadoc so it no longer describes annotation-controlled Sting integration.

### Phase 2
- Remove `React4jProcessor.deriveSting(...)` logic that reads the annotation member.
- Infer Sting activation solely from injectable constructor parameters plus Sting classpath availability.
- Drop obsolete validation messages that referenced `sting=ENABLED` and `sting=DISABLED`.

### Phase 3
- Delete bad-input fixtures and exact test expectations tied only to the removed annotation member.
- Remove or migrate input fixtures that explicitly used `sting = Feature.DISABLE`.
- Keep positive fixtures for named/type-level Sting propagation and raw injected types.
- Update docs/examples and `CHANGELOG.md`.
- Refresh API-diff fixtures for the removed `View.sting()` member.

### Phase 4
- Run:
  - `bundle exec buildr react4j:processor:test`
  - `bundle exec buildr test_api_diff`
- Optionally run `bundle exec buildr test` to detect acceptable external downstream fallout, but do not require it for acceptance.

## Required Full-Gate Command
- `bundle exec buildr test`
