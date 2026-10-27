# Task Map

- Spec: [SPEC.md](../SPEC.md)
- Status: implementation-review
- Current frontier: implementation review
- Planning reviewer: plan_review (1/3 rounds; final confirmation unused; Findings: none)
- Plan checkpoint: automatic (accepted interview scope, explicit implementation/PR authority, passing planning review)
- Implementation reviewer: implementation_review (2/5 rounds; final confirmation unused; confirmation pending)

## Full-scope validation

- Gate: `tools/check.sh`
- Evidence: tools/check.sh passed before review; repeat after review correction in progress. Documentation preview and domain links passed.

## Tasks

| ID | Task | Status | Blocked by |
| --- | --- | --- | --- |
| T01 | [Content-based immutable array identity](T01-array-identity.md) | complete | None |
| T02 | [Typed custom immutable keys](T02-custom-keys.md) | complete | T01 |
| T03 | [Mutable input equality policies](T03-input-equality.md) | complete | T02 |
| T04 | [Contract, migration and complete verification](T04-delivery.md) | complete | T03 |

## Sequencing notes

Primary agent implements the lowest-numbered unblocked task sequentially. Reviewers are independent and read-only. Commit each task and its evidence together. User authority covers implementation and a review PR; no Rose adoption, release, merge or secrets changes.

## Promoted knowledge

[Input contract](../../../docs/specs/input-semantics.md), [reader guide](../../../docs/input-semantics.md), compiled doc examples and Unreleased changelog. Specification qualification/ownership and link audit complete; no ADR/glossary/deferred artifact needed.

## Planning review

Round 1: Findings: none. Complete plan and repository boundaries checked. Residual risk is generated adaptation and lifecycle behavior, covered by planned checks.

## Implementation review

Round 1 found a P2 compilation defect: framed array/custom keys combined with dynamically keyed inputs passed an Object-valued conditional to InputKeys.frame(String). Confirmed with failing mixed Object, generic and unsupported-array compiler cases. The correction converts the resolved dynamic contribution to text before framing and adds compiler and browser regressions. Focused strict compiler tests and development/optimized browser tests pass. Scope and accepted contracts are unchanged; canonical documentation and domain-link validation remain valid.

Round 2 confirms the correction and affected surfaces; pending. Three ordinary rounds and the final-confirmation allowance remain after this attempt.
