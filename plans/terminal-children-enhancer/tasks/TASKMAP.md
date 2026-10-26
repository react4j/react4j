# Task map

- Spec: [SPEC.md](../SPEC.md)
- Status: implementing
- Current frontier: T02
- Planning reviewer: terminal_children_plan_review (1/3 rounds; final confirmation unused); Findings: none. Complete plan and consequential repository claims checked.
- Plan checkpoint: automatic; accepted scope and latest implementation/PR authority recovered, independent planning review passed.
- Implementation reviewer: pending (0/5 rounds; final confirmation unused)

## Full-scope validation

- Gate: tools/check.sh, including documentation/site and release validation; domain links separately.
- Evidence: pending

## Tasks

| ID | Task | Status | Blocked by |
| --- | --- | --- | --- |
| T01 | [Terminal child setters](T01-terminal-setters.md) | complete | None |
| T02 | [Default children enhancer](T02-default-enhancer.md) | pending | T01 |

## Sequencing notes

The primary agent implements sequentially. Reviewers are independent and read-only. Do not mutate Rose. After delivery create a ready-for-review PR, run an immediate babysit tick and maintain one same-chat 30-minute heartbeat. Pause for skill-defined blockers; after verified merge pause the heartbeat and archive only when no work or attention remains.

## Promoted knowledge

- Existing owner: [input-step-enhancers specification](../../../docs/specs/input-step-enhancers.md); update in T02. No new ADR or glossary term qualifies.
