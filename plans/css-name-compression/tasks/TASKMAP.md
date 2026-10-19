# Task Map

- Spec: [SPEC.md](../SPEC.md)
- Status: `implementing`
- Current frontier: `T03`
- Planning reviewer: `/root/css_compression_plan_review` (`2/3` rounds; final confirmation `unused`; passed)
- Plan checkpoint: `automatic` — explicit implementation request, confirmed interview and passing planning review; no human checkpoint requested.
- Implementation reviewer: `pending` (`0/5` rounds; final confirmation `unused`)
- Authority: implementation request and confirmed interview recorded in SPEC; existing PR112 push authority; no merge.

## Full-scope validation

- Gate: `tools/check.sh`, domain link validator, simplify, independent implementation review and exact diff inspection.
- Evidence: pending.

## Tasks

| ID | Task | Status | Blocked by |
| --- | --- | --- | --- |
| T01 | [Effective accessor declarations](T01-effective-declarations.md) | complete | None |
| T02 | [Stable names in every mode](T02-stable-names.md) | complete | T01 |
| T03 | [Checked shared IDs and liveness](T03-shared-ids.md) | pending | T02 |
| T04 | [Component compression through a real link](T04-component-compression.md) | pending | T03 |
| T05 | [Compress every symbol and protect variants](T05-complete-compression.md) | pending | T04 |
| T06 | [Source content and map fidelity](T06-source-maps.md) | pending | T05 |
| T07 | [Delivery verification and permanent docs](T07-delivery.md) | pending | T06 |

## Sequencing notes

- Primary implements sequentially. Every task stays green and commits implementation with evidence/state. Intermediate class-only compression is delivery staging, not the final public contract.
- Owned symbol inventory is established in T02 to record stable policy. T04 adds canonical getter composition and the first real compression table, then T05 completes all kinds; no unused catalogue-only prefactoring task.
- T01 owns an evidence-based disposition of the baseline assembled-suite timeout, because source/binary/golden compiler variations exercise that target throughout later tasks.
- Review budgets include every attempt across replacements. Planning: 3 rounds plus one final confirmation. Implementation: 5 rounds plus one final confirmation.
- Entire active tree is temporary and removed only after passing implementation review and promoting conclusions. Retain exact gate/review evidence in git/PR.

## Promoted knowledge

- [Classification](../knowledge.md)
- [CSS names](../../../docs/specs/css-names.md)
- [CSS bindings](../../../docs/specs/css-bindings.md)
- [CSS liveness](../../../docs/specs/css-liveness.md)
- [Symbolic boundary ADR](../../../docs/adr/0001-css-symbol-liveness.md)
- [Glossary](../../../docs/glossary/README.md)
- [Identifier-token mapping deferral](../../../docs/deferred/css-identifier-token-mapping.md)
- [JavaScript-to-authored-CSS mapping deferral](../../../docs/deferred/javascript-authored-css-mapping.md)

## Review history

- Author preflight: all requirements/criteria have task ownership; sequential seven-task DAG and local links validated; domain candidates classified before specifications and specification-only diff audited. Implementation mismatches explicitly owned by tasks.
- Planning round 1: complete plan/code/domain review found equal-valued getters lose identity because the linker coalesces literals. Accepted; SPEC and T02–T04 now require exact binding identity/composition/provider semantics, early stable-name metadata/matcher changes and real-link equal-text regressions across kinds/policies/providers. Global-local keyframe wording was also narrowed to preserve existing placement constraints. Verified in round 2.
- Planning round 2: Findings: none. Checked binding-identity correction and affected metadata, symbolization, provider, allocation, test and sequencing surfaces. Residual experiments (J2CL method matching and CI runtime) remain owned by T02 and T01. Final confirmation unused.
