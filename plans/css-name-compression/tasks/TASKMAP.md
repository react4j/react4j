# Task Map

- Spec: [SPEC.md](../SPEC.md)
- Status: `implementation-review`
- Current frontier: `implementation review`
- Planning reviewer: `/root/css_compression_plan_review` (`2/3` rounds; final confirmation `unused`; passed)
- Plan checkpoint: `automatic` — explicit implementation request, confirmed interview and passing planning review; no human checkpoint requested.
- Implementation reviewer: `/root/css_compression_implementation_review` (`2/5` rounds; final confirmation `unused`; round 3 ready)
- Authority: implementation request and confirmed interview recorded in SPEC; existing PR112 push authority; no merge.

## Full-scope validation

- Gate: `tools/check.sh`, domain link validator, simplify, independent implementation review and exact diff inspection.
- Evidence: complete `tools/check.sh` passed on 2026-10-19; 40 ordinary targets, 8 optimized browser suites, 20 JVM coverage targets, unchanged 95/85 policy, external consumer, site, unsigned release and all 13 independently rebuilt publication artifacts passed. Simplify and domain replacement/link audits complete. See T07 for exact CSS coverage counts. Independent implementation review remains the next gate.

## Post-implementation gates

- Fresh read-only implementation review under the recorded budget; fix findings with focused validation before closeout.
- After review passes, remove this exact plan tree and task-created scratch output, commit and push PR112, refresh/attach its description, and report actual remote Verify state. Never merge.

## Tasks

| ID | Task | Status | Blocked by |
| --- | --- | --- | --- |
| T01 | [Effective accessor declarations](T01-effective-declarations.md) | complete | None |
| T02 | [Stable names in every mode](T02-stable-names.md) | complete | T01 |
| T03 | [Checked shared IDs and liveness](T03-shared-ids.md) | complete | T02 |
| T04 | [Component compression through a real link](T04-component-compression.md) | complete | T03 |
| T05 | [Compress every symbol and protect variants](T05-complete-compression.md) | complete | T04 |
| T06 | [Source content and map fidelity](T06-source-maps.md) | complete | T05 |
| T07 | [Delivery verification and permanent docs](T07-delivery.md) | complete | T06 |

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

- Implementation round 1: fresh read-only reviewer activated against plan d1375174d and delivered HEAD b2a5dfa7f, with complete scope/authority/corrections, promoted artifacts and full-gate evidence. Found two P2 collision gaps: raw attribute references and escaped grammar names. Both verified and addressed with catalogue reservations, finite candidate checks, decoded contexts and related scoped-function handling. Ten focused targets and one full post-fix gate passed; the last no-gain comparison refinement passes four focused targets. Full gate rerunning for the final tree before closeout. No significant decision or requirement changed.

- Implementation round 2: same read-only reviewer confirmed escaped-context fixes and found one remaining P2: attribute checks missed actual root-plus-modifier getter compositions. Accepted and fixed by reconstructing finite getter values from canonical table metadata. Regressions cover late variants, known catalogue CSS, reversed inventory order, partial/all stability, case flags, escapes and unchanged matches. Internal, bundler and linker targets pass; formatter, domain links and diff checks pass. Complete gate passed against 05a5f0802 before this final fix; rerun against the committed fix before closeout. No significant decision or requirement changed.
