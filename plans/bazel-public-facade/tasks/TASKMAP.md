# Task Map

- Spec: [SPEC.md](../SPEC.md)
- Status: implementation-review
- Current frontier: none
- Planning reviewer: /root/bazel_facade_planning (2/3 rounds; final confirmation unused); Findings: none.
- Plan checkpoint: automatic; accepted scope and implementation authority from user “accept”, no human pause requested, planning review passed.
- Implementation reviewer: pending (0/5 rounds; final confirmation unused)

## Full-scope validation

- Gate: tools/check.sh, simplify pass, independent implementation review, public inventory/negative access checks.
- Evidence: full tools/check.sh passed, including 50 default / 14 optimized browser / 22 JVM coverage suites, all coverage floors unchanged, fresh native external consumer plus compiler/visibility failures, 27,032 site links with zero broken, unsigned archive validation and 15 independently rebuilt artifacts identical. Simplify and domain-owner sweeps complete.

## Tasks

| ID | Task | Status | Blocked by |
| --- | --- | --- | --- |
| T01 | [Public facade and symbolic composition](T01-public-boundary.md) | complete | None |
| T02 | [Delivery verification](T02-delivery.md) | complete | T01 |

## Promoted knowledge

- Public API setup belongs in [Project Setup](../../../docs/project_setup.md) and [CSS README](../../../css/README.md); contributor boundary guidance belongs in [CONTRIBUTING](../../../CONTRIBUTING.md). Mutable public inventory and dependency patches remain in code/configuration. No new domain spec/ADR/glossary/deferred artifact required.

## Review history

- Round 1: Findings: none. Reviewed all requirements/tasks, alias-only ownership, macro feasibility and visibility, caller-private inputs/link children/provider identities, patch export and full delivery verification. Real nested composition remains subject to implementation tests.

- Planning round 2: verified pinned Bazel checks select condition visibility at the symbolic definition package, without ordinary label argument delegation. Round 2 passed with Findings: none: fixture/docs use narrow condition grants; no new options, public targets or implementation relocation. External completion and precise grants remain implementation gates.

## Workflow gates

- Implementation review: ready for round 1 against the committed implementation, limit 5 plus one final confirmation; requires Findings: none.
- Closeout: after review, record evidence, remove this exact temporary tree, update/push PR112 and leave Merge to the user.
