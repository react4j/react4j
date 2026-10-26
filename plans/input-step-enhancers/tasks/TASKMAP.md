# Task Map

- Spec: [SPEC.md](../SPEC.md)
- Status: `implementing`
- Current frontier: `T03`
- Planning reviewer: `/root/planning_review` (`1/3` rounds; final confirmation unused); Findings: none. Checked complete scope, tasks, compiler and browser evidence. Residual risk: implementation tests for generic/nullness/J2CL interactions.
- Plan checkpoint: automatic; accepted interview scope, explicit implementation/PR authority, and passing planning review establish the basis to proceed.
- Implementation reviewer: pending (`0/5` rounds; final confirmation unused)
- Authority: final interview acceptance, followed by the explicit request to implement and raise a PR. Correction 3A excludes children migration.

## Full-scope validation

- Gate: `tools/check.sh`
- Evidence: pending

## Tasks

| ID | Task | Status | Blocked by |
| --- | --- | --- | --- |
| T01 | [Enhance generated steps](T01-enhance-steps.md) | complete | None |
| T02 | [Forward first-step helpers](T02-static-shortcuts.md) | complete | T01 |
| T03 | [Verify browser behavior and publish the contract](T03-runtime-and-contract.md) | pending | T02 |

## Sequencing notes

- Primary agent implements sequentially. Reviewers are independent and read-only.
- Planning has three rounds plus one final confirmation; implementation has five rounds plus one final confirmation.
- Planning review covers the whole tree. The final task owns the complete gate, domain promotion, and documentation.
- PR creation follows passing implementation review and closeout; the PR is for human review, with no merge.

## Promoted knowledge

- Pending: canonical public contract and author guidance; classify ADR qualification before promotion.
