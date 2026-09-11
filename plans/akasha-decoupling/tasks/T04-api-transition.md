# T04 — Record the breaking API transition

- Status: `pending`
- Blocked by: `T03`
- Spec coverage: `R6`, `R11`, `AC7`, `AC11`, `D1`

## Delivers

The breaking browser-binding-neutral API transition is documented and represented by the standard release API-diff fixture without rewriting historical records.

## Acceptance criteria

- [ ] The Unreleased changelog has one concise `Breaking:` entry describing Akasha removal, replacement types, and unchecked-cast migration.
- [ ] The now-contradictory Unreleased Akasha-upgrade entry is reconciled.
- [ ] The generated `0.228-0.229.json` fixture exactly reflects the intentional API changes.
- [ ] Historical changelog entries and historical API fixtures are unchanged.
- [ ] API-diff validation passes.

## Validation

- `bundle exec buildr update_api_diff` followed by `bundle exec buildr test_api_diff` — generates and validates the release fixture.
- Targeted historical-file diff inspection — proves immutable records were not rewritten.

## Evidence

- `pending`
