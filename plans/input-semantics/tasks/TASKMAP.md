# Task Map

- Spec: [SPEC.md](../SPEC.md)
- Status: planned
- Current frontier: T01
- Planning reviewer: plan_review (1/3 rounds; final confirmation unused; Findings: none)
- Plan checkpoint: automatic (accepted interview scope, explicit implementation/PR authority, passing planning review)
- Implementation reviewer: pending (0/5 rounds; final confirmation unused)

## Full-scope validation

- Gate: `tools/check.sh`
- Evidence: pending

## Tasks

| ID | Task | Status | Blocked by |
| --- | --- | --- | --- |
| T01 | [Content-based immutable array identity](T01-array-identity.md) | pending | None |
| T02 | [Typed custom immutable keys](T02-custom-keys.md) | pending | T01 |
| T03 | [Mutable input equality policies](T03-input-equality.md) | pending | T02 |
| T04 | [Contract, migration and complete verification](T04-delivery.md) | pending | T03 |

## Sequencing notes

Primary agent implements the lowest-numbered unblocked task sequentially. Reviewers are independent and read-only. Commit each task and its evidence together. User authority covers implementation and a review PR; no Rose adoption, release, merge or secrets changes.

## Promoted knowledge

Pending: canonical input-semantics contract and reader guidance.

## Planning review

Round 1: Findings: none. Complete plan and repository boundaries checked. Residual risk is generated adaptation and lifecycle behavior, covered by planned checks.
