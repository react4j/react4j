# Task Map

- Spec: [SPEC.md](../SPEC.md)
- Status: planned
- Current frontier: T01
- Planning reviewer: /root/bazel_facade_planning (1/3 rounds; final confirmation unused); Findings: none.
- Plan checkpoint: automatic; accepted scope and implementation authority from user “accept”, no human pause requested, planning review passed.
- Implementation reviewer: pending (0/5 rounds; final confirmation unused)

## Full-scope validation

- Gate: tools/check.sh, simplify pass, independent implementation review, public inventory/negative access checks.
- Evidence: pending

## Tasks

| ID | Task | Status | Blocked by |
| --- | --- | --- | --- |
| T01 | [Public facade and symbolic composition](T01-public-boundary.md) | pending | None |
| T02 | [Delivery verification](T02-delivery.md) | pending | T01 |

## Promoted knowledge

- Public API setup belongs in [Project Setup](../../../docs/project_setup.md) and [CSS README](../../../css/README.md); contributor boundary guidance belongs in [CONTRIBUTING](../../../CONTRIBUTING.md). Mutable public inventory and dependency patches remain in code/configuration. No new domain spec/ADR/glossary/deferred artifact required.

## Review history

- Round 1: Findings: none. Reviewed all requirements/tasks, alias-only ownership, macro feasibility and visibility, caller-private inputs/link children/provider identities, patch export and full delivery verification. Real nested composition remains subject to implementation tests.
