# Task Map

- Spec: [SPEC.md](../SPEC.md)
- Status: `implementation-review`
- Current frontier: none (all tasks complete)
- Planning reviewer: `/root/planning_review` (`1/3` rounds; final confirmation unused); Findings: none. Checked complete scope, tasks, compiler and browser evidence. Residual risk: implementation tests for generic/nullness/J2CL interactions.
- Plan checkpoint: automatic; accepted interview scope, explicit implementation/PR authority, and passing planning review establish the basis to proceed.
- Implementation reviewer: pending (`0/5` rounds; final confirmation unused)
- Authority: final interview acceptance, followed by the explicit request to implement and raise a PR. Correction 3A excludes children migration.

## Full-scope validation

- Gate: `tools/check.sh`
- Evidence: pinned Bazel 9.3.1 `tools/check.sh` passed on the frozen implementation. Processor coverage: 3331/3464 lines and 1678/1938 branches; all configured thresholds passed. Site and unsigned bundle validation passed (12 artifacts, 60 entries).

## Tasks

| ID | Task | Status | Blocked by |
| --- | --- | --- | --- |
| T01 | [Enhance generated steps](T01-enhance-steps.md) | complete | None |
| T02 | [Forward first-step helpers](T02-static-shortcuts.md) | complete | T01 |
| T03 | [Verify browser behavior and publish the contract](T03-runtime-and-contract.md) | complete | T02 |

## Sequencing notes

- Primary agent implements sequentially. Reviewers are independent and read-only.
- Planning has three rounds plus one final confirmation; implementation has five rounds plus one final confirmation.
- Planning review covers the whole tree. The final task owns the complete gate, domain promotion, and documentation.
- PR creation follows passing implementation review and closeout; the PR is for human review, with no merge.

## Promoted knowledge

- R1-R5 and the enhancer compatibility invariant in R6 pass specification qualification and the replacement test: accepted, observable public contracts with no existing durable owner. Promoted to [input step enhancer specification](../../../docs/specs/input-step-enhancers.md).
- Author usage and examples belong to [the guide](../../../docs/input-step-enhancers.md), backed by compiled `doc-examples` sources.
- D1/D2 are public contract choices captured by those requirements. D3's one-object realization stays in code; its diagnostic behavior is specified. It does not meet the hard-to-reverse ADR threshold, so no ADR is added.
- D4 is a delivery boundary from the user's correction, retained in this plan and the PR; no children migration or new deferred-issue artifact is authorized.
- Task ordering, compiler experiments and delivery evidence remain temporary. No new glossary term is required by the existing CSS glossary.
- Domain link validation passed; the specification-only audit retains only independently verifiable obligations.
