# Task Map

- Spec: [SPEC.md](../SPEC.md)
- Status: `implementing`
- Current frontier: `T02`
- Planning reviewer: `/root/css_liveness_plan_review` (`1/3` rounds; final confirmation `unused`); Findings: none.
- Plan checkpoint: `automatic`; accepted scope, prior implementation/push authority, completed interview confirmation and passing independent planning review. No additional human plan pause was requested.
- Implementation reviewer: `pending` (`0/5` rounds; final confirmation `unused`)

## Full-scope validation

- Gate: `tools/check.sh`, domain link validation, simplify, independent implementation review.
- Evidence: pending.

## Tasks

| ID | Task | Status | Blocked by |
| --- | --- | --- | --- |
| T01 | [Link live class values to pruned browser CSS](T01-linked-values.md) | complete | None |
| T02 | [Preserve selectors and resource dependencies](T02-resource-reachability.md) | pending | T01 |
| T03 | [Verify distribution boundaries and close delivery](T03-delivery.md) | pending | T02 |

## Sequencing notes

- Primary implements tasks sequentially; reviewers read only. Fact-finding agents produced evidence only.
- Preserve baseline c0098a68d and concurrent changes; stage exact owned paths.

## Review history

- Planning round 1: Findings: none. Whole plan, dependencies, scope, domain artifacts and verification reviewed. Worker integration and map projection implementation proof remains owned by T01/T02. No blocking findings or dispositions.

## Promoted knowledge

- [Glossary](../../../docs/glossary/README.md), [specification](../../../docs/specs/css-liveness.md), [ADR](../../../docs/adr/0001-css-symbol-liveness.md), [deferred compression question](../../../docs/deferred/css-name-compression.md).
- Classification and current behavior gaps: SPEC Knowledge classification. Final validation pending.
