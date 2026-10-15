# T06 — Effective processor nullness and input rules

- Status: `pending`
- Blocked by: `T05`
- Spec coverage: `R4`, `R5`, `R6`, `R7`, `AC2`, `AC3`

## Delivers

Implement declaration-owned effective JSpecify contracts throughout input/render/observation/constructor/builder/factory generation, including resolved generic/nested types and correct default/omission diagnostics. Remove redundant typed checks while preserving dynamic props/context invariants.

## Acceptance criteria

- [ ] Focused source/compiled inheritance and nested/generic tests pass; nullable AUTODETECT and required nullable semantics remain; invalid optional non-null/default contracts are rejected; parametric contracts are preserved or diagnosed without strengthening.

## Validation

- Run affected golden/diagnostic families and new focused source/compiled/default/omission/dynamic-boundary regressions; regenerate expected outputs using the generator — proves the delivered behavior. Record actual labels, commands and outcomes below; do not mark complete with skipped required checks.

## Evidence

- pending
