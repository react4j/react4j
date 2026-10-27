# Task Map

- Spec: [SPEC.md](../SPEC.md)
- Status: `reviewed`
- Current frontier: none (all tasks and implementation review complete; closeout pending)
- Planning reviewer: `/root/planning_review` (`1/3` rounds; final confirmation `unused`), Findings: none.
- Plan checkpoint: automatic. Basis: confirmed interview, explicit implementation/PR instruction in this chat on 2026-10-27, and passing independent planning review.
- Implementation reviewer: `/root/implementation_review` (`2/5` rounds; final confirmation `unused`), Findings: none. Round 1 P2 resolved.

## Full-scope validation

- Gate: `tools/check.sh` with Bazel 9.3.1 selected through PATH; domain artifact validator; independent implementation review.
- Evidence: full `tools/check.sh` passed with pinned Bazel 9.3.1, including all 58 tests, 17 optimized browser targets, JVM/browser coverage, site, publication and unsigned bundle verification. Repeated the entire gate after the round 1 correction: all passed, including 27,307 site links (zero broken) and 12 artifacts/60 unsigned bundle entries. Domain links and author diff review passed. Independent implementation round 2: Findings: none, residual risk none identified.

## Tasks

| ID | Task | Status | Blocked by |
| --- | --- | --- | --- |
| T01 | [Core and DOM composition](T01-runtime-composition.md) | complete | None |
| T02 | [Generated composition and enhancer migration](T02-generated-composition.md) | complete | T01 |
| T03 | [Consumer guidance and full validation](T03-delivery-validation.md) | complete | T02 |

## Sequencing notes

- The primary agent implements sequentially. Reviewers remain read-only.
- Keep standard ChildrenEnhancer's signature migration with T02 so T01 remains compatible with the current processor.
- After T03, promote qualified durable requirements/decisions, validate and commit them before activating a fresh implementation reviewer. After a pass, inspect and remove this exact plan tree and commit closeout before publishing the PR.

## Promoted knowledge

- Candidate classification before drafting: accepted public types, eager conversion, ready-step eligibility, typed input/render boundaries, nullness, identity, order, exceptions and array ownership qualify as durable independently verifiable requirements; new owner `docs/specs/renderable-composition.md`. Enhancer setter matching updates its existing owner `docs/specs/input-step-enhancers.md` and links the new node contract.
- The native marker/ordinary Buildable split qualifies for an ADR: public hierarchy migration is costly, its native restriction is surprising without J2CL context, and it preserves primitive node representation over alternative wrappers. Owner: next available ADR `0008`.
- Migration examples and usage belong in the existing enhancer guide and checked snippet sources; the changelog records the API impact. Concrete generator/helper layout stays in code; gate evidence and delivery sequence stay here. No new glossary term or deferred issue qualifies.

- Promoted owners: [composition contract](../../../docs/specs/renderable-composition.md), [enhancer contract](../../../docs/specs/input-step-enhancers.md), [native boundary ADR](../../../docs/adr/0008-preserve-native-renderables.md).
- Specification-only diff audit: every requirement survives replacement, describes accepted observable behavior or public interoperable types, and excludes delivery/configuration detail. Existing enhancer history reviewed; its setter-matching requirement now names the projected builder type. All domain links pass validation.
- Simplify review: shared scalar/array input materialization now serves setters, defaults and context; generated output remains unchanged. Public native bridge fields (e.g. ReactPortal.children) remain materialized output, while typed DOM props are composition inputs. No compatibility overloads or new readiness state were added.
- Final knowledge sweep: reused the earlier classifications. The review correction fixes a bug under the existing composition requirements (2, 5 and 10); it adds no requirement, architectural choice, term or deferred question. Canonical owners and migration guidance remain complete; domain links validated again before closeout. Continuing coverage scenarios stay in existing suites; task logs and this exact temporary tree have no continuing use.

## Review history

- Implementation round 2: Findings: none. Reviewed unconditional conversion, the hybrid regression's failure before the fix and passing results afterward, all 458 fixture changes, repeated full-gate evidence and clean worktree. Prior P2 resolved; no residual risk identified. Reviewed fix commit `1241befb7`.
- Implementation round 1: one P2 finding, accepted and fixed. Exact-ReactNode render declarations bypassed normalization even when their runtime value also implements Buildable, violating R5/R6. The new real-browser hybrid regression failed with "Post-render observes the completed hybrid build" before the fix and passed after always normalizing authored render results. All three golden families regenerated and passed; the complete gate passed again, including checked and optimized hybrid execution. No scope or significant decision changed; existing canonical requirements already cover this case.
- Planning round 1: Findings: none. Reviewed complete plan, authority, dependencies, runtime/DOM boundaries, generator/enhancer migration, nullness, J2CL restrictions, validation and closeout. Residual risk: prove native-marker conversion and generated signatures with checked J2CL and optimized browser execution.
- Author preflight: all plan links resolve; T01 -> T02 -> T03 graph is acyclic and covers every requirement/acceptance criterion.
- Baseline: pinned Bazel 9.3.1 `bazel test //core:tests //dom:tests --test_output=errors` passed (2 tests).
