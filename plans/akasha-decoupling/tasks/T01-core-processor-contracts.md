# T01 — Decouple core and processor contracts

- Status: `pending`
- Blocked by: `None`
- Spec coverage: `R1`, `R3`, `AC1`, `AC6`, `D3`, `D6`

## Delivers

Core and processor-generated APIs stop exposing Akasha arrays and errors while retaining the existing ReactNode varargs behavior and a typed `@OnError` contract.

## Acceptance criteria

- [ ] Public `react4j.JsError` exists as an opaque global native type with cast-boundary Javadocs.
- [ ] `@OnError`, `OnComponentDidCatch`, processor validation/generation, and authored fixtures use `react4j.JsError`.
- [ ] `ReactNode.of(JsArray<ReactNode>)` is removed.
- [ ] Generated child builders use Java arrays/varargs and contain no Akasha array import.
- [ ] Core dependency rules and regenerated processor fixtures reflect the new contracts.
- [ ] Core and all processor tests pass.

## Validation

- `bundle exec buildr react4j:processor:test` — proves core compilation/GWT behavior and the full generated-source contract.
- Active-source `rg` checks — prove no Akasha reference remains in core or processor contracts except work intentionally deferred to later tasks.

## Evidence

- `pending`
