# T01 — Decouple core and processor contracts

- Status: `done`
- Blocked by: `None`
- Spec coverage: `R1`, `R3`, `AC1`, `AC6`, `D3`, `D6`

## Delivers

Core and processor-generated APIs stop exposing Akasha arrays and errors while retaining the existing ReactNode varargs behavior and a typed `@OnError` contract.

## Acceptance criteria

- [x] Public `react4j.JsError` exists as an opaque global native type with cast-boundary Javadocs.
- [x] `@OnError`, `OnComponentDidCatch`, processor validation/generation, and authored fixtures use `react4j.JsError`.
- [x] `ReactNode.of(JsArray<ReactNode>)` is removed.
- [x] Generated child builders use Java arrays/varargs and contain no Akasha array import.
- [x] Core dependency rules and regenerated processor fixtures reflect the new contracts.
- [x] Core and all processor tests pass.

## Validation

- `bundle exec buildr react4j:processor:test` — proves core compilation/GWT behavior and the full generated-source contract.
- Active-source `rg` checks — prove no Akasha reference remains in core or processor contracts except work intentionally deferred to later tasks.

## Evidence

- `bundle exec buildr clean react4j:processor:test` — passed: core `12/12`, processor `468/468`, and both core GWT permutations.
- Fixture regeneration was performed through the processor test harness with `react4j.output_fixture_data=true`; the normal write-disabled run then passed against the checked-in outputs.
- `rg -n "akasha" core/src/main core/src/test processor/src/main processor/src/test --glob '!core/src/main/java/react4j/React.gwt.xml'` — no matches; the deferred module inherit is owned by T03.
- `git diff --check` — passed.
