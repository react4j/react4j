# T07 — Complete strict golden verification

- Status: `pending`
- Blocked by: `T06`
- Spec coverage: `R3`, `R4`, `R5`, `R6`, `R7`, `AC2`, `AC3`

## Delivers

Complete all golden family migrations and enforce exact emitted file sets and full checker compilation of generated bodies in both formatting modes.

## Acceptance criteria

- [ ] Entire processor suite passes under Bazel, golden updater is bounded and reruns its family, diagnostic coverage is retained, strict checker negative control fails for the intended nullness reason.

## Validation

- bazel test all processor targets; run strict-checker negative control and golden update no-change checks; compare fixture inventory against T05 — proves the delivered behavior. Record actual labels, commands and outcomes below; do not mark complete with skipped required checks.

## Evidence

- pending
