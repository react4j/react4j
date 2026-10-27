# Task map

- Spec: [SPEC.md](../SPEC.md)
- Status: reviewed
- Current frontier: none (closeout)
- Planning reviewer: terminal_children_plan_review (1/3 rounds; final confirmation unused); Findings: none. Complete plan and consequential repository claims checked.
- Plan checkpoint: automatic; accepted scope and latest implementation/PR authority recovered, independent planning review passed.
- Implementation reviewer: terminal_children_impl_review (1/5 rounds; final confirmation unused); Findings: none. Full base-to-d900970a6 scope, API, default/explicit/context selection, terminal/binding/key paths, shared builder, source/precompiled fixtures, real browser tests and documentation checked independently.

## Full-scope validation

- Gate: tools/check.sh, including documentation/site and release validation; domain links separately.
- Evidence: tools/check.sh passed on the complete implementation; 58 main, 17 optimized browser and 26 coverage targets passed, coverage thresholds and site/release checks passed. Domain links and specification-only audit passed.

## Tasks

| ID | Task | Status | Blocked by |
| --- | --- | --- | --- |
| T01 | [Terminal child setters](T01-terminal-setters.md) | complete | None |
| T02 | [Default children enhancer](T02-default-enhancer.md) | complete | T01 |

## Sequencing notes

The primary agent implements sequentially. Reviewers are independent and read-only. Do not mutate Rose. After delivery create a ready-for-review PR, run an immediate babysit tick and maintain one same-chat 30-minute heartbeat. Pause for skill-defined blockers; after verified merge pause the heartbeat and archive only when no work or attention remains.

## Promoted knowledge

- [Input-step-enhancers specification](../../../docs/specs/input-step-enhancers.md): default selection and override precedence, terminal child semantics, stream order/consumption and scalar binding are accepted durable, replacement-independent requirements. Existing owner/history inspected; glossary has no affected terms.
- Author guide, checked examples and changelog own usage/migration. Code owns selection and source lists. This tree owns sequencing, downstream inspection and validation evidence.
- No new ADR: ordinary enhancer selection/binding ownership is direct and does not qualify as a surprising hard-to-reverse architectural choice; no deferred question or new glossary term.

## Closeout evidence

- Planning review passed in 1 round; implementation review passed in 1 round. No findings or unresolved dispositions.
- Full gate and domain links passed. Final core binary/source/Javadoc archives each contain ChildrenEnhancer.
- Final knowledge sweep: accepted contracts remain owned by docs/specs/input-step-enhancers.md and author/migration guidance by docs/input-step-enhancers.md; no later requirement, new term, qualifying ADR or deferred issue was introduced.
- No Rose files were modified; downstream evidence remains a source assessment. Plan and task evidence are committed before deleting this exact tree.
