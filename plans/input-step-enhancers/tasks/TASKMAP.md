# Task Map

- Spec: [SPEC.md](../SPEC.md)
- Status: `closeout`
- Current frontier: none (all tasks complete)
- Planning reviewer: `/root/planning_review` (`1/3` rounds; final confirmation unused); Findings: none. Checked complete scope, tasks, compiler and browser evidence. Residual risk: implementation tests for generic/nullness/J2CL interactions.
- Plan checkpoint: automatic; accepted interview scope, explicit implementation/PR authority, and passing planning review establish the basis to proceed.
- Implementation reviewer: `/root/implementation_review` (`3/5` rounds consumed; final confirmation unused). Round 3: Findings: none; all prior findings resolved. No residual risk identified.
- Authority: final interview acceptance, followed by the explicit request to implement and raise a PR. Correction 3A excludes children migration.

## Full-scope validation

- Gate: `tools/check.sh`
- Evidence: pinned Bazel 9.3.1 `tools/check.sh` passed again after round 2 fixes. Processor coverage: 3441/3573 lines and 1751/2012 branches; all configured thresholds passed. Site and unsigned bundle validation passed (12 artifacts, 60 entries).

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

## Implementation review history

- Round 1: generic helper variables could shadow generated/referenced types (R2/R4); declaration erasures could escape clash validation (R5); concrete collection helper parameters failed the generated-code checker (R2/R4). All reproduced with real compiler tests before fixes.
- Disposition: reserve generated and signature type names before forwarding; validate declaration erasures as well as resolved signatures, including inherited setters; extend the existing concrete collection signature suppression to enhancers. Retain legal overload tests and source/precompiled goldens for all continuation types. The signature traversal also checks inaccessible intersection bounds.
- These fixes preserve the accepted contracts and significant decisions. No canonical requirement or guide change is needed.
- Validation: `tools/update_goldens.sh jspecify`, all 8 `//processor:all_tests` targets, `tools/check.sh`, domain link validation and `git diff --check` passed. Existing children goldens remain unchanged.
- Round 2: reserve view type-parameter bounds even when absent from inputs; include overridden ancestor setter erasures. Both personally reproduced before fixing. A focused plain-javac probe confirmed the ancestor-erasure failure also affects default helpers, so the fix covers both peers.
- Cause and prevention: effective-member resolution discarded declarations whose erasures still participate in javac name-clash checks. Collect every overridden declaration's erasure for setters and helpers; source/precompiled negative tests exercise both. Earlier inheritance cases did not combine explicit payload specialization with a cross-enhancer overload. For name hygiene, java.lang bounds hid the omitted view-bound traversal; an unused external AtomicInteger bound now exercises it.
- Round 2 validation: focused enhancer tests and regenerated source/precompiled goldens passed. The final full gate passed on frozen code, including all processor suites. One prior coverage launch collided with the Bazel server's IPv6 port; process/socket inspection identified the owner, focused runtime coverage passed on retry, then the complete gate passed without code changes. Domain contracts remain unchanged.
- Round 3: Findings: none. Reviewer verified view-bound reservation, setter/helper ancestor erasures, source/precompiled tests and goldens, legal overloads, unchanged children fixtures, clean diff and the full gate. Focused enhancer and golden suites passed independently.
- Final knowledge sweep: reuse prior classifications. Review fixes implement the existing public signature/diagnostic requirements; Java erasure traversal, name reservation and the one-off coverage port collision add no accepted domain requirement, architectural decision, glossary term or deliberately deferred question. The guide and canonical specification remain current.
