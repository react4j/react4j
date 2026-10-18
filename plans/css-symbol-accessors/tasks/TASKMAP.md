# Task Map

- Spec: [SPEC.md](../SPEC.md)
- Status: complete
- Current frontier: closeout
- Planning reviewer: passed (2/3 rounds; final confirmation unused)
- Plan checkpoint: automatic — accepted implementation scope, final interview confirmation and passing independent planning review
- Implementation reviewer: passed (1/5 rounds; final confirmation unused)

## Full-scope validation

- Gate: tools/check.sh
- Evidence: tools/check.sh passed on the T03 implementation, including all local/CI stages; CSS processor coverage 99.1% lines / 96.3% branches. See T03 for counts and task-local verification.

## Tasks

| ID | Task | Status | Blocked by |
| --- | --- | --- | --- |
| T01 | [Property symbols and imports](T01-properties.md) | complete | None |
| T02 | [ID and animation exports](T02-ids-animations.md) | complete | T01 |
| T03 | [Coverage, documentation and full validation](T03-validation.md) | complete | T02 |

## Sequencing notes

T01 includes property migrations so all-local scoping lands without an intermediate broken application. Only one implementation task is active. Reviewers are read-only. Current active tree is plans/css-symbol-accessors; no other plan is resumed or edited.

## Promoted knowledge

Not required: no domain artifact directories exist. Permanent user contracts belong in css/README.md and CHANGELOG.md.

## Planning review history

Round 1: two valid blockers. Direct resource bindings now conservatively root reachable resources (T02); mixed-kind property dependency closure and global emission/order are explicitly assigned to T01 with bundler tests. Both corrections preserve accepted direct access and import behavior without Closure liveness. Round 2: Findings: none; both dispositions verified.

## Implementation review history

Round 1: Findings: none. The independent read-only reviewer checked APIs, processor constraints/generation, parsed symbol rewriting, transitive header-jar imports, retention and ordering, migrations, goldens, source maps, documentation and delivery alignment. Eight focused test targets passed independently; the reviewer verified full-gate success and unchanged coverage policy. No residual risk identified within scope.

## Closeout gate

All three implementation tasks and their focused checks are complete. Full tools/check.sh passed. Planning and implementation reviews passed within their budgets, and the plan and implementation are committed at 7736b7a1d, 06e48d61b, 399f2782c and c874fecc5. Permanent contracts are in css/README.md, annotation APIs and CHANGELOG.md; no domain promotion is required. The exact active tree is plans/css-symbol-accessors and is ready for deletion in the final closeout commit. PR #112 is the authorized delivery target; human merge remains pending.
