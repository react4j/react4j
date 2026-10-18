# Task Map

- Spec: [SPEC.md](../SPEC.md)
- Status: implementation-review
- Current frontier: implementation review
- Planning reviewer: /root/interfaces_planning_review — Findings: none (1/3 rounds; final confirmation unused)
- Plan checkpoint: automatic — user-authorized implementation, final grill-me confirmation and passing planning review; no human plan checkpoint requested
- Implementation reviewer: pending (0/5 rounds; final confirmation unused)

## Full-scope validation

- Gate: tools/check.sh
- Evidence: tools/check.sh passed after the final implementation change; all 37 development targets, six optimized browser targets, unchanged coverage policy, isolated consumer, site/release validation and 13 reproducible artifacts. CSS processor root: 1244/1254 lines and 890/926 branches. See T03 for focused evidence and simplify results.

## Tasks

| ID | Task | Status | Blocked by |
| --- | --- | --- | --- |
| T01 | [Inherited Java accessors](T01-inherited-accessors.md) | complete | None |
| T02 | [Inherited symbol contracts](T02-symbol-contracts.md) | complete | T01 |
| T03 | [Separate compilation and full validation](T03-integration-validation.md) | complete | T02 |

## Sequencing notes

Only one implementation task is active. T01 proves plain inherited accessors and generic substitution without claiming symbol-annotation inheritance until T02. Reviewers are read-only. The exact active tree is plans/css-interfaces. The prior css-symbol-accessors plan is closed and is not resumed.

## Promoted knowledge

Not required; permanent contracts belong in the existing CSS guide and changelog.

## Planning review

Round 1 inspected the complete spec/task tree and relevant processor/generator, annotation retention, fixtures and integration evidence. Findings: none. Residual implementation risks are effective method resolution and header-jar contract preservation, covered by T01–T03.
