# Task Map

- Spec: [SPEC.md](../SPEC.md)
- Status: `planned`
- Current frontier: `T01`
- Planning reviewer: `/root/downstream_policy_planning` (`1/3` rounds; final confirmation unused); Findings: none. Whole plan and affected packaging/build/preview/contracts checked.
- Plan checkpoint: automatic (explicit implementation instruction and passing independent planning review)
- Implementation reviewer: pending (`0/5` rounds; final confirmation unused)

## Full-scope validation

- Gate: `tools/check.sh`, action/dependency audit and independent implementation review.
- Evidence: pending.

## Tasks

| ID | Task | Status | Blocked by |
| --- | --- | --- | --- |
| T01 | [Policy-free plain packaging](T01-packaging.md) | pending | None |
| T02 | [Downstream contract and delivery](T02-delivery.md) | pending | T01 |

## Sequencing notes

T01 changes the packaging contract atomically across tools, Bazel and consumers so no compatibility path is needed. T02 owns durable documentation, external-consumer proof, full validation, simplification, review and publication. Review budget is three planning rounds and five implementation rounds, with one unused final confirmation allowance per phase.

## Promoted knowledge

Pending: update the existing web-assets spec, asset-linking ADR and CSS README.
