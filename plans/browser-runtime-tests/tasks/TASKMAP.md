# Task Map

- Spec: [SPEC.md](../SPEC.md)
- Status: `implementation-review`
- Current frontier: `none`
- Planning reviewer: `/root/browser_runtime_planning` (`1/3` rounds; final confirmation `unused`); Findings: none.
- Plan checkpoint: `automatic`; latest user “Do that” supplies implementation authority, no human checkpoint requested, planning review passed.
- Implementation reviewer: `pending` (`0/5` rounds; final confirmation `unused`)

## Full-scope validation

- Gate: `tools/check.sh`, simplify pass and independent implementation review.
- Evidence: full tools/check.sh passed on the delivered implementation; 50 default / 14 optimized browser / 22 JVM coverage suites, every coverage floor, fresh native downstream consumer and negative compiler/visibility probes, 27,032 site links with zero broken, unsigned archive validation and all 15 independently rebuilt artifacts identical. Core: 97.62% lines / 91.38% branches; DOM: 97.81% / 100%.
- Simplify pass complete; no legacy compatibility path or temporary checker probes remain.

## Tasks

| ID | Task | Status | Blocked by |
| --- | --- | --- | --- |
| T01 | [Browser runtime verification](T01-browser-runtime.md) | complete | None |
| T02 | [Browser-only coverage evidence](T02-browser-coverage.md) | complete | T01 |
| T03 | [Delivery verification and closeout](T03-delivery.md) | complete | T02 |

## Sequencing notes

- Keep bytecode dependency tests and publication classes; removing their build outputs is outside scope.
- Use the repository compiler policy for J2CL test libraries; native downstream checker selection stays unchanged.
- T01 can use the old coverage inventory temporarily; T02 removes that execution dependency before full validation.

## Promoted knowledge

- Runtime configuration/setup belongs in [Project Setup](../../../docs/project_setup.md); contributor compiler and coverage operation belongs in [CONTRIBUTING](../../../CONTRIBUTING.md); publication compatibility stays in the [release guide](../../../tools/release/README.md).
- Shared compiler settings and unchanged coverage floors remain in code/configuration. Existing CSS specifications, including JVM accessor behavior, are unchanged. No new glossary term, qualifying specification/ADR or deferred issue: this replaces verification machinery for the already adopted browser runtime and uses existing documentation owners.

## Planning review history

- Round 1: Findings: none. Reviewed complete scope, prior assertions, configuration, checker/formatter boundaries, archive inventory and preservation of publication/JVM tool checks.

## Workflow gates

- Implementation review: ready for round 1 after this full-gate evidence commit; requires Findings: none.
- Closeout: pending after review passes. Preserve needed run evidence in history/PR, update PR112, remove this exact temporary tree and push. Do not merge.
