# T04 — Record the breaking API transition

- Status: `done`
- Blocked by: `T03`
- Spec coverage: `R6`, `R11`, `AC7`, `AC11`, `D1`

## Delivers

The breaking browser-binding-neutral API transition is documented and represented by the standard release API-diff fixture without rewriting historical records.

## Acceptance criteria

- [x] The Unreleased changelog has one concise `Breaking:` entry describing Akasha removal, replacement types, and unchecked-cast migration.
- [x] The now-contradictory Unreleased Akasha-upgrade entry is reconciled.
- [x] The generated `0.228-0.229.json` fixture exactly reflects the intentional API changes.
- [x] Historical changelog entries and historical API fixtures are unchanged.
- [x] API-diff validation passes.

## Validation

- `bundle exec buildr update_api_diff` followed by `bundle exec buildr test_api_diff` — generates and validates the release fixture.
- Targeted historical-file diff inspection — proves immutable records were not rewritten.

## Evidence

- `bundle exec buildr update_api_diff` derived `PREVIOUS_PRODUCT_VERSION=0.228` and `PRODUCT_VERSION=0.229`, found the expected 38 API differences, and generated `api-test/src/test/resources/fixtures/0.228-0.229.json`.
- Fixture inspection found only the approved surface: seven owned facade additions, ten concrete event generic changes, one generic-bound change, one portal field change, six container/error parameter changes, the removed `JsArray` overload, and twelve event return-type changes including the corrected timestamp.
- `bundle exec buildr test_api_diff` passed (`1/1`) against the generated fixture and again reported 38 differences.
- `git diff --name-only HEAD -- CHANGELOG.md api-test/src/test/resources/fixtures` listed only `CHANGELOG.md` and the new `0.228-0.229.json`; no historical changelog section or fixture was modified.
- `git diff --check` passed.
