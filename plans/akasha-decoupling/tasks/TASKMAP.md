# Task Map

- Spec: [`SPEC.md`](../SPEC.md)
- Status: `implementing`
- Current frontier: `T06`
- Planning reviewer: `/root/planning_reviewer` — `Findings: none` (`1/3` rounds)
- Human approval: `pre-approved by explicit user instruction after completed grill-me session`
- Implementation reviewer: `pending` (`0/5` rounds)

## Full-scope validation

- Gate: full React4j test/package/API-diff/downstream-statistics workflow, generated-POM and active-reference inspection, all ten remote branch-tip verification, and clean diff checks.
- Evidence: `pending`

## Tasks

| ID | Task | Status | Blocked by |
| --- | --- | --- | --- |
| `T01` | [`Decouple core and processor contracts`](T01-core-processor-contracts.md) | `done` | None |
| `T02` | [`Replace DOM public browser types`](T02-dom-public-types.md) | `done` | `T01` |
| `T03` | [`Remove the repository dependency`](T03-repository-dependency-removal.md) | `done` | `T02` |
| `T04` | [`Record the breaking API transition`](T04-api-transition.md) | `done` | `T03` |
| `T05` | [`Migrate TodoMVC upgrade branches`](T05-todomvc-branches.md) | `done` | `T04` |
| `T06` | [`Migrate broad-browser upgrade branches`](T06-browser-app-branches.md) | `in_progress` | `T04` |
| `T07` | [`Regenerate release evidence and run the full gate`](T07-release-evidence.md) | `pending` | `T05`, `T06` |

## Sequencing notes

- React4j retains the Akasha dependency until T03 so T01 and T02 can migrate API slices while remaining compilable.
- T05 and T06 are independently unblocked after the local `0.229` artifacts and API-diff contract exist, but the primary agent executes them sequentially as required by the workflow.
- Downstream branches are built before push, then T07 reruns the release workflow against their remote tips.
- Implementation commits include the active task and task-map evidence. React4j is not pushed.

## Promoted knowledge

- `not-required`: this repository has no `docs/adr`, `docs/glossary`, `docs/specs`, or `docs/deferred` owner.
