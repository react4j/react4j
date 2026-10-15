# T14 — Shared CI gate and full verification

- Status: `in_progress`
- Blocked by: `T13`
- Spec coverage: `R1`, `R3`, `R7`, `R8`, `R9`, `R10`, `R11`, `R12`, `R13`, `AC1`, `AC2`, `AC3`, `AC4`, `AC5`, `AC6`, `AC7`, `AC8`

## Delivers

Provide shared local/CI check entrypoint and GitHub Actions workflow, finalize reviewed Ahab reproducibility/sandbox declarations and coverage report checks, run the strongest full gate and inspect the complete change before implementation review.

## Acceptance criteria

- [ ] Formatting/buildifier/Ahab, all JVM/processor tests and coverage, archive/consumer checks, both browser modes, site and unsigned release checks pass. CI runs the same gate; obsolete infrastructure is absent; full evidence is recorded.

## Validation

- tools/check.sh; final clean-checkout/readiness checks appropriate to the gate; diff inspection and task/artifact linkage audit — proves the delivered behavior. Record actual labels, commands and outcomes below; do not mark complete with skipped required checks.

## Evidence

- pending
