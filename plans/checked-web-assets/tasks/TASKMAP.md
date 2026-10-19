# Task Map

- Spec: [SPEC.md](../SPEC.md)
- Status: `implementation-review`
- Current frontier: `none`
- Planning reviewer: `/root/assets_planning_review` (`2/3` rounds; final confirmation `unused`)
- Plan checkpoint: automatic; accepted scope and explicit implementation authority, planning review passed
- Implementation reviewer: `pending` (`0/5` rounds; final confirmation `unused`)
- Authority: user accepted the preceding design and explicitly instructed implementation; no human plan checkpoint.
- Base: d9ff8955726c3dead8a2700e34ea3ea268fc46aa

## Full-scope validation

- Gate: `tools/check.sh` plus incremental asset mutation and packaged HTTP/subpath checks.
- Evidence: final tools/check.sh passed after the Unicode font collision correction. 45 development/JVM, 8 optimized browser and 23 JVM coverage targets passed; configured coverage, both Ahab modes, external consumer, complete site/release validation and 15 reproducible archives passed. T02/T04 record actual incremental cache evidence; T05 records packaged HTTP/subpath/offline checks.

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

## Promoted knowledge

- Classification and replacement-test audit: SPEC.md → Durable-knowledge classification.
- Canonical owners: [asset contract](../../../docs/specs/web-assets.md), [cache decision](../../../docs/adr/0003-finalize-asset-urls-after-linking.md), [language](../../../docs/glossary/README.md), and existing CSS binding/name/liveness/source-map specs.
- `python3 /Users/peter/.codex/skills/domain-modeling/scripts/validate_doc_links.py .`: passed. No new deferred issue.
