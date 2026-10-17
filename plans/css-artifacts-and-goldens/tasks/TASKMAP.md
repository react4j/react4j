# Task map

- Spec: [SPEC.md](../SPEC.md)
- Status: planned
- Current frontier: T01
- Planning reviewer: /root/planning_review (1/3 rounds), Findings: none
- Plan checkpoint: automatic — accepted immediate scope and latest user execution instruction; passing planning review
- Implementation reviewer: pending (0/5 rounds)

## Full-scope validation

- Gate: tools/check.sh plus css/check_reproducibility.sh and assembled processor jar repeatability
- Evidence: pending

## Tasks

| ID | Task | Status | Blocked by |
| --- | --- | --- | --- |
| T01 | [Subproject layout](T01-layout.md) | pending | None |
| T02 | [Standalone shaded artifact](T02-artifact.md) | pending | T01 |
| T03 | [Golden harness and delivery](T03-goldens.md) | pending | T02 |

## Sequencing and ownership

Each task stays green and commits its state. T01 owns target/CI/format/path migration and overview docs; T02 owns artifact construction/isolation; T03 owns fixtures, strict checks, updater, final docs, full gate, draft PR and cleanup. No production language expansion.

## Promoted knowledge

Pending check of existing canonical documentation directories.
