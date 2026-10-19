# Task Map

- Spec: [SPEC.md](../SPEC.md)
- Status: `ready-for-closeout`
- Current frontier: `none`
- Planning reviewer: `/root/assets_planning_review` (`2/3` rounds; final confirmation `unused`)
- Plan checkpoint: automatic; accepted scope and explicit implementation authority, planning review passed
- Implementation reviewer: `/root/assets_delivery_review` (`2/5` rounds; final confirmation `unused`; Findings: none)
- Authority: user accepted the preceding design and explicitly instructed implementation; no human plan checkpoint.
- Base: d9ff8955726c3dead8a2700e34ea3ea268fc46aa

## Full-scope validation

- Gate: `tools/check.sh` plus incremental asset mutation and packaged HTTP/subpath checks.
- Evidence: final tools/check.sh passed after the Unicode font collision correction, and again after all round-1 font validation and documentation fixes. 45 development/JVM, 8 optimized browser and 23 JVM coverage targets passed; configured coverage, both Ahab modes, external consumer, complete site/release validation and 15 reproducible archives passed. T02/T04 record actual incremental cache evidence; T05 records packaged HTTP/subpath/offline checks.

## Tasks

| ID | Task | Status | Blocked by |
| --- | --- | --- | --- |
| T01 | [Checked asset declarations](T01-declarations.md) | complete | None |
| T02 | [Java asset liveness and late URLs](T02-java-link.md) | complete | T01 |
| T03 | [CSS assets and owned fonts](T03-css-fonts.md) | complete | T02 |
| T04 | [Manifest packaging and compression](T04-packaging.md) | complete | T03 |
| T05 | [Examples, downstream contracts and full gates](T05-examples-delivery.md) | complete | T04 |

## Sequencing notes

- Implement sequentially; reviewers are read-only and independent. Each task is committed before advancing.
- Preserve the existing shared-link CSS variant contract. Asset byte-dependent data must never become a Java/J2CL/Closure input.
- Existing branch publication is authorized by the ongoing PR request; do not merge.

## Review history

- Round 1: two blockers, both accepted. T05 now owns all TodoMVC local SVG migrations; T03 specifies selected shared-font imports and T05 migrates all four Drumloop consumers. Round 2: Findings: none; both corrections verified. Planning gate passed.

## Implementation review history

- Round 1: three P2 findings, all accepted and reproduced. Distinct scoped custom-property definitions are retained, variable fragments are validated in their full font context, and the global font guide permits supported outer-level font faces. Parsed-model regressions cover scoped orders, conditional definitions, transitive/cyclic references, fallback/unknown values and keyword/multiword distinctions; processor regressions cover component/global declarations in both distributions. Focused unit suite plus implementation and standalone processor suites passed (590 standalone cases). Full post-fix tools/check.sh passed, including downstream development/optimized applications, site/release validation and all 15 independently rebuilt archives. Final confirmation remains unused.

- Round 2: Findings: none. Same reviewer independently verified all three fixes, scoped/transitive/cyclic regressions, full-declaration validation without emitted-AST mutation, permanent README/spec alignment and the completed post-fix full gate. Residual: Linux CI awaits branch publication. No confirmation allowance consumed.

## Promoted knowledge

- Classification and replacement-test audit: SPEC.md → Durable-knowledge classification.
- Canonical owners: [asset contract](../../../docs/specs/web-assets.md), [cache decision](../../../docs/adr/0003-finalize-asset-urls-after-linking.md), [language](../../../docs/glossary/README.md), and existing CSS binding/name/liveness/source-map specs.
- `python3 /Users/peter/.codex/skills/domain-modeling/scripts/validate_doc_links.py .`: passed. No new deferred issue.

## Final knowledge and cleanup gate

- All five tasks and the final full gate passed. Planning used 2/3 rounds and implementation 2/5 rounds, both ending Findings: none. Both final confirmation allowances remain unused.
- Final knowledge sweep reused the accepted classification; only the reviewed CSS-binding R12 clarification changed since promotion, and its replacement-independent contract/link validation passed. No missing canonical owner or new deferred issue was found.
- Simplify covered the complete delivered feature in T05 and the private font-validation corrections in round 1. No compatibility path or new public configuration was added for review fixes.
- Exact active tree remains plans/checked-web-assets. Implementation commits a357cd546 through 158821d5e and this review record preserve the audit trail; inspect and remove only this completed tree at closeout.
