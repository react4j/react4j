# Task Map

- Spec: [SPEC.md](../SPEC.md)
- Status: `implementing`
- Current frontier: `T02`
- Planning reviewer: `/root/planning_review` (`1/3` rounds; final confirmation `unused`), Findings: none.
- Plan checkpoint: automatic. Basis: confirmed interview, explicit implementation/PR instruction in this chat on 2026-10-27, and passing independent planning review.
- Implementation reviewer: `pending` (`0/5` rounds; final confirmation `unused`)

## Full-scope validation

- Gate: `tools/check.sh` with Bazel 9.3.1 selected through PATH; domain artifact validator; independent implementation review.
- Evidence: pending.

## Tasks

| ID | Task | Status | Blocked by |
| --- | --- | --- | --- |
| T01 | [Core and DOM composition](T01-runtime-composition.md) | complete | None |
| T02 | [Generated composition and enhancer migration](T02-generated-composition.md) | pending | T01 |
| T03 | [Consumer guidance and full validation](T03-delivery-validation.md) | pending | T02 |

## Sequencing notes

- The primary agent implements sequentially. Reviewers remain read-only.
- Keep standard ChildrenEnhancer's signature migration with T02 so T01 remains compatible with the current processor.
- After T03, promote qualified durable requirements/decisions, validate and commit them before activating a fresh implementation reviewer. After a pass, inspect and remove this exact plan tree and commit closeout before publishing the PR.

## Promoted knowledge

- Pending classification/promotion during T03 and before implementation review.

## Review history

- Planning round 1: Findings: none. Reviewed complete plan, authority, dependencies, runtime/DOM boundaries, generator/enhancer migration, nullness, J2CL restrictions, validation and closeout. Residual risk: prove native-marker conversion and generated signatures with checked J2CL and optimized browser execution.
- Author preflight: all plan links resolve; T01 -> T02 -> T03 graph is acyclic and covers every requirement/acceptance criterion.
- Baseline: pinned Bazel 9.3.1 `bazel test //core:tests //dom:tests --test_output=errors` passed (2 tests).
