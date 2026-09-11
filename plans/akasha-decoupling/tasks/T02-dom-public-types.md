# T02 — Replace DOM public browser types

- Status: `done`
- Blocked by: `T01`
- Spec coverage: `R2`, `AC2`, `AC3`, `AC4`, `D2`, `D3`, `D4`, `D5`

## Delivers

DOM public APIs become browser-library-neutral through opaque React4j event types and `Object` container boundaries, including a correct numeric SyntheticEvent timestamp.

## Acceptance criteria

- [x] Public opaque `Event`, `EventTarget`, `Element`, `Document`, `DataTransfer`, and `TouchList` types exist in `react4j.dom.events` with the approved inheritance and cast guidance.
- [x] ReactDOM container parameters and ReactPortal container information use `Object`.
- [x] SyntheticEvent remains generic over the neutral Event type and all concrete wrappers use that neutral type.
- [x] All event return types use the approved React4j facades.
- [x] Timestamp is `double` in Java and `number` in the Closure extern.
- [x] DOM dependency rules and tests reflect the neutral API.

## Validation

- `bundle exec buildr react4j:dom:test` — proves Java and GWT compilation plus DOM dependency rules.
- Signature/reference inspection — proves no active DOM API contains Akasha.

## Evidence

- `bundle exec buildr clean react4j:dom:test` — passed: core `12/12` and DOM `1/1`.
- `bundle exec buildr react4j:dom:package` — passed: `DomTest` and `DomDevTest` GWT permutations, Javadocs, and package artifacts.
- `rg -n "akasha" dom/src/main dom/src/test` — no matches.
- Signature inspection confirmed all concrete wrappers extend `SyntheticEvent<Event>`, container boundaries use `Object`, and the timestamp declarations are Java `double`/Closure `number`.
- `git diff --check` — passed.
