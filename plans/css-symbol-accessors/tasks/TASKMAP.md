# Task Map

- Spec: [SPEC.md](../SPEC.md)
- Status: implementing
- Current frontier: T02
- Planning reviewer: passed (2/3 rounds; final confirmation unused)
- Plan checkpoint: automatic — accepted implementation scope, final interview confirmation and passing independent planning review
- Implementation reviewer: pending (0/5 rounds; final confirmation unused)

## Full-scope validation

- Gate: tools/check.sh
- Evidence: pending

## Tasks

| ID | Task | Status | Blocked by |
| --- | --- | --- | --- |
| T01 | [Property symbols and imports](T01-properties.md) | complete | None |
| T02 | [ID and animation exports](T02-ids-animations.md) | pending | T01 |
| T03 | [Coverage, documentation and full validation](T03-validation.md) | pending | T02 |

## Sequencing notes

T01 includes property migrations so all-local scoping lands without an intermediate broken application. Only one implementation task is active. Reviewers are read-only. Current active tree is plans/css-symbol-accessors; no other plan is resumed or edited.

## Promoted knowledge

Not required: no domain artifact directories exist. Permanent user contracts belong in css/README.md and CHANGELOG.md.

## Planning review history

Round 1: two valid blockers. Direct resource bindings now conservatively root reachable resources (T02); mixed-kind property dependency closure and global emission/order are explicitly assigned to T01 with bundler tests. Both corrections preserve accepted direct access and import behavior without Closure liveness. Round 2: Findings: none; both dispositions verified.
