# T05 — Processor Proton and golden baseline

- Status: `in_progress`
- Blocked by: `T04`
- Spec coverage: `R1`, `R3`, `R4`, `R7`, `AC2`, `AC3`

## Delivers

Upgrade processor infrastructure to the pinned Proton API and JSpecify, adapting the existing test families into Bazel golden/diagnostic targets without losing no-Sting or formatter subprocess coverage. Preserve test inventory and deliberate regeneration.

## Acceptance criteria

- [ ] Processor compiles under reference checking. Every existing relevant fixture/test is inventoried and has a migrated owner or explicit obsolete-GWT disposition; formatted/unformatted expected output is regenerated through bounded commands.

## Validation

- Compile processor; run representative positive/negative golden families plus no-Sting/formatter subprocess tests; record fixture inventory and ownership — proves the delivered behavior. Record actual labels, commands and outcomes below; do not mark complete with skipped required checks.

## Evidence

- Mechanical formatting checkpoint: equivalent Java token comparison passed for 21 maintained processor main/test files; original fixtures/test inventory captured before migration.
