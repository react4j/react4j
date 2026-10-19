# Task Map

- Spec: [SPEC.md](../SPEC.md)
- Status: `implementing`
- Current frontier: `T02`
- Planning reviewer: `/root/browser_runtime_planning` (`1/3` rounds; final confirmation `unused`); Findings: none.
- Plan checkpoint: `automatic`; latest user “Do that” supplies implementation authority, no human checkpoint requested, planning review passed.
- Implementation reviewer: `pending` (`0/5` rounds; final confirmation `unused`)

## Full-scope validation

- Gate: `tools/check.sh`, simplify pass and independent implementation review.
- Evidence: pending.

## Tasks

| ID | Task | Status | Blocked by |
| --- | --- | --- | --- |
| T01 | [Browser runtime verification](T01-browser-runtime.md) | complete | None |
| T02 | [Browser-only coverage evidence](T02-browser-coverage.md) | pending | T01 |
| T03 | [Delivery verification and closeout](T03-delivery.md) | pending | T02 |

## Sequencing notes

- Keep bytecode dependency tests and publication classes; removing their build outputs is outside scope.
- Use the repository compiler policy for J2CL test libraries; native downstream checker selection stays unchanged.
- T01 can use the old coverage inventory temporarily; T02 removes that execution dependency before full validation.

## Promoted knowledge

- Pending contributor documentation update; no qualifying new domain specification or ADR.

## Planning review history

- Round 1: Findings: none. Reviewed complete scope, prior assertions, configuration, checker/formatter boundaries, archive inventory and preservation of publication/JVM tool checks.
