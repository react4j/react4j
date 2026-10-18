# T01 — Link live class values to pruned browser CSS

- Status: `pending`
- Blocked by: None
- Spec coverage: R1, R2, R4, R5; AC1, AC4, AC5 (basic class path)

## Delivers

A real SaveButton optimized link deletes an unused owner/modifier and keeps the root when only a modifier accessor is called, including after helpers/interfaces inline. Its development and library bundles retain their intended full CSS and maps remain usable.

## Acceptance criteria

- [ ] Processor emits validated binding/symbol identities and generic/facade/nested goldens remain correct; ordinary JVM literal accessors unchanged.
- [ ] Generic rules_closure factory/config/report/output seam and local JVM worker compile once with stock options/checks; no runtime marker.
- [ ] Deterministic catalogue and link-bound report drive independent css_bundle; BUNDLE retain-all and unlinked library path are explicit; binary without report fails.
- [ ] Existing three theme variants share report; malformed/mismatched evidence fails unit checks.
- [ ] Basic impossible class rules are removed conservatively and source maps for survivors resolve after deleted preceding spans. Other dependencies may remain conservatively until T02.

## Validation

- Processor/internal/bundler/linker JVM tests; affected golden update and strict compilation.
- SaveButton browser tests in development and `-c opt`; generated CSS/JS assertions prove deletion/presence and no marker.
- Integration library-root tests and source-map consumer queries.

## Evidence

- Pending.
