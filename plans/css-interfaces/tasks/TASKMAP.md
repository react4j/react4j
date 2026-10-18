# Task Map

- Spec: [SPEC.md](../SPEC.md)
- Status: review-passed
- Current frontier: closeout
- Planning reviewer: /root/interfaces_planning_review — Findings: none (1/3 rounds; final confirmation unused)
- Plan checkpoint: automatic — user-authorized implementation, final grill-me confirmation and passing planning review; no human plan checkpoint requested
- Implementation reviewer: /root/interfaces_implementation_review — Findings: none (2/5 rounds; final confirmation unused)

## Full-scope validation

- Gate: tools/check.sh
- Evidence: tools/check.sh passed after the final implementation change; all 37 development targets, six optimized browser targets, unchanged coverage policy, isolated consumer, site/release validation and 13 reproducible artifacts. CSS processor root: 1282/1292 lines and 902/936 branches. See T03 for focused evidence and simplify results.

## Tasks

| ID | Task | Status | Blocked by |
| --- | --- | --- | --- |
| T01 | [Inherited Java accessors](T01-inherited-accessors.md) | complete | None |
| T02 | [Inherited symbol contracts](T02-symbol-contracts.md) | complete | T01 |
| T03 | [Separate compilation and full validation](T03-integration-validation.md) | complete | T02 |

## Sequencing notes

Implementation tasks run sequentially. T01 proves plain inherited accessors and generic substitution without claiming symbol-annotation inheritance until T02. Reviewers are read-only. The exact active tree is plans/css-interfaces. The prior css-symbol-accessors plan is closed and is not resumed.

## Promoted knowledge

Not required; permanent contracts belong in the existing CSS guide and changelog.

## Planning review

Round 1 inspected the complete spec/task tree and relevant processor/generator, annotation retention, fixtures and integration evidence. Findings: none. Residual implementation risks are effective method resolution and header-jar contract preservation, covered by T01–T03.

## Implementation review

Round 1 verified the complete scope and reran the assembled processor, implementation and integration targets. Two P2 findings were accepted: inherited member names can hide singleton/self references, and unrelated signatures can select method-generic/throws-bearing peers by interface name. Both fixes and their regression coverage are complete within T03, and tools/check.sh passed again.

Round 2 returned Findings: none at 9fc56ca17. The same independent reviewer verified both fixes, regression tests, goldens, header-jar integration and documentation; reran the assembled processor, implementation and integration targets; and independently passed 30 JDK 21 collision combinations and 24 source/binary signature combinations. The reviewer inspected the full-gate log and coverage reports and identified no residual risk. Review passed in 2/5 rounds; final confirmation was unused. See T03 for dispositions and evidence.
