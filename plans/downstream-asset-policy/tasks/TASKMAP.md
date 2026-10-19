# Task Map

- Spec: [SPEC.md](../SPEC.md)
- Status: `implementation-review`
- Current frontier: implementation review
- Planning reviewer: `/root/downstream_policy_planning` (`1/3` rounds; final confirmation unused); Findings: none. Whole plan and affected packaging/build/preview/contracts checked.
- Plan checkpoint: automatic (explicit implementation instruction and passing independent planning review)
- Implementation reviewer: `/root/downstream_policy_implementation` (`1/5` rounds; final confirmation unused); round 1 in progress.

## Full-scope validation

- Gate: `tools/check.sh`, action/dependency audit and independent implementation review.
- Evidence: `tools/check.sh` passed completely (2026-10-20 local); `/tmp/react4j-asset-full-check.log` contains the run, with coverage in `reports/coverage/summary.txt`. Focused asset tests, six browser variants, action/module audits, and direct manifest/hash/file-set checks passed.

## Tasks

| ID | Task | Status | Blocked by |
| --- | --- | --- | --- |
| T01 | [Policy-free plain packaging](T01-packaging.md) | complete | None |
| T02 | [Downstream contract and delivery](T02-delivery.md) | complete | T01 |

## Sequencing notes

T01 changes the packaging contract atomically across tools, Bazel and consumers so no compatibility path is needed. T02 owns durable documentation, external-consumer proof, full validation, simplification, review and publication. After T02, the workflow owns independent review, PR112 publication and deletion of this exact plan tree. Review budget is three planning rounds and five implementation rounds, with one unused final confirmation allowance per phase.

## Promoted knowledge

[Web assets spec](../../../docs/specs/web-assets.md), [linking ADR](../../../docs/adr/0003-finalize-asset-urls-after-linking.md), [glossary](../../../docs/glossary/README.md#asset-inventory), [CSS README](../../../css/README.md) and CHANGELOG updated. Classification: plain outputs, complete inventory and downstream policy ownership are accepted replacement-independent requirements owned by the existing spec; inventory meaning belongs to the glossary; metadata timing stays in the existing ADR; usage belongs to README; removed planner/dependency details stay in code/history. Specification-only diff audited and domain links validated. No new ADR or deferred issue qualifies.

## Review evidence

Round 1 independently confirmed the full scope, action graphs and real directory/manifest correspondence. A stale site-copy test name was updated to refer to assets rather than representations; its behavior is unchanged. The focused site suite and Java formatting passed after the rename. Final reviewer disposition pending.
