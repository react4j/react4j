# Task Map

- Spec: [SPEC.md](../SPEC.md)
- Status: implementing
- Current frontier: T02
- Planning reviewer: /root/interfaces_planning_review — Findings: none (1/3 rounds; final confirmation unused)
- Plan checkpoint: automatic — user-authorized implementation, final grill-me confirmation and passing planning review; no human plan checkpoint requested
- Implementation reviewer: pending (0/5 rounds; final confirmation unused)

## Full-scope validation

- Gate: tools/check.sh
- Evidence: pending

## Tasks

| ID | Task | Status | Blocked by |
| --- | --- | --- | --- |
| T01 | [Inherited Java accessors](T01-inherited-accessors.md) | complete | None |
| T02 | [Inherited symbol contracts](T02-symbol-contracts.md) | pending | T01 |
| T03 | [Separate compilation and full validation](T03-integration-validation.md) | pending | T02 |

## Sequencing notes

Only one implementation task is active. T01 proves plain inherited accessors and generic substitution without claiming symbol-annotation inheritance until T02. Reviewers are read-only. The exact active tree is plans/css-interfaces. The prior css-symbol-accessors plan is closed and is not resumed.

## Promoted knowledge

Not required; permanent contracts belong in the existing CSS guide and changelog.

## Planning review

Round 1 inspected the complete spec/task tree and relevant processor/generator, annotation retention, fixtures and integration evidence. Findings: none. Residual implementation risks are effective method resolution and header-jar contract preservation, covered by T01–T03.
