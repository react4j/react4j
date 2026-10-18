# Task Map

- Spec: [SPEC.md](../SPEC.md)
- Status: `complete`
- Current frontier: `None; closeout pending`
- Planning reviewer: `/root/css_liveness_plan_review` (`1/3` rounds; final confirmation `unused`); Findings: none.
- Plan checkpoint: `automatic`; accepted scope, prior implementation/push authority, completed interview confirmation and passing independent planning review. No additional human plan pause was requested.
- Implementation reviewer: `/root/css_liveness_implementation_review` (`2/5` rounds completed; final confirmation `unused`); Findings: none.

## Full-scope validation

- Gate: `tools/check.sh`, domain link validation, simplify, independent implementation review.
- Evidence: final `tools/check.sh` passed after all changes; domain link validation, simplify and independent implementation review passed. Detailed checks and coverage are in T03.

## Tasks

| ID | Task | Status | Blocked by |
| --- | --- | --- | --- |
| T01 | [Link live class values to pruned browser CSS](T01-linked-values.md) | complete | None |
| T02 | [Preserve selectors and resource dependencies](T02-resource-reachability.md) | complete | T01 |
| T03 | [Verify distribution boundaries and close delivery](T03-delivery.md) | complete | T02 |

## Sequencing notes

- Primary implements tasks sequentially; reviewers read only. Fact-finding agents produced evidence only.
- T03 completion means implementation readiness. Independent review, cleanup and push are subsequent workflow gates; their status remains here until closeout.
- Preserve baseline c0098a68d and concurrent changes; stage exact owned paths.

## Review history

- Planning round 1: Findings: none. Whole plan, dependencies, scope, domain artifacts and verification reviewed. Worker integration and map projection implementation proof remains owned by T01/T02. No blocking findings or dispositions.
- Implementation round 1: complete against c0098a68d..a233214f1. One P2 finding: native nesting parents were pruned without preserving inherited specificity or `:not(&)` matching. Full delivered scope inspected; no other findings. Fix preserves native parent contexts while still pruning impossible leaves. Unit and real authored J2CL/Closure regressions reproduced the failure, then passed, including conditional nesting. `tools/check.sh` passed after the fix. Round 2 verifies the committed fix and affected surfaces.
- Implementation round 2: Findings: none against a233214f1..efc2b25d8. Reviewer verified optimized parent lists and negated nesting with both checked IDs dead in the report, conditional/deeper nesting, impossible-leaf pruning, focused suites, diff checks and coverage. No residual risk within the reviewed fix. Final confirmation unused.

## Promoted knowledge

- [Glossary](../../../docs/glossary/README.md), [specification](../../../docs/specs/css-liveness.md), [ADR](../../../docs/adr/0001-css-symbol-liveness.md), [deferred compression question](../../../docs/deferred/css-name-compression.md).
- Classification: SPEC Knowledge classification. All specified behavior gaps implemented; glossary/spec/ADR/deferred ownership and specification replacement audit checked. Domain link validation passed.
- Final knowledge sweep: the nesting correction enforces the existing matching/specificity contract. No new durable requirement, language, architectural choice or deferred question needs promotion. Canonical links validate; no permanent inbound links target this temporary plan. All tasks and validation are complete. Plan snapshot 9b8ec6f9c and implementation commits through efc2b25d8 exist in history; delete only this active tree at closeout, then push PR #112 without merging.
