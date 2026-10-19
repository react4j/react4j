# Task Map

- Spec: [SPEC.md](../SPEC.md)
- Status: `implementing`
- Current frontier: `T05`
- Planning reviewer: `/root/assets_planning_review` (`2/3` rounds; final confirmation `unused`)
- Plan checkpoint: automatic; accepted scope and explicit implementation authority, planning review passed
- Implementation reviewer: `pending` (`0/5` rounds; final confirmation `unused`)
- Authority: user accepted the preceding design and explicitly instructed implementation; no human plan checkpoint.
- Base: d9ff8955726c3dead8a2700e34ea3ea268fc46aa

## Full-scope validation

- Gate: `tools/check.sh` plus incremental asset mutation and packaged HTTP/subpath checks.
- Evidence: pending

## Tasks

| ID | Task | Status | Blocked by |
| --- | --- | --- | --- |
| T01 | [Checked asset declarations](T01-declarations.md) | complete | None |
| T02 | [Java asset liveness and late URLs](T02-java-link.md) | complete | T01 |
| T03 | [CSS assets and owned fonts](T03-css-fonts.md) | complete | T02 |
| T04 | [Manifest packaging and compression](T04-packaging.md) | complete | T03 |
| T05 | [Examples, downstream contracts and full gates](T05-examples-delivery.md) | in_progress | T04 |

## Sequencing notes

- Implement sequentially; reviewers are read-only and independent. Each task is committed before advancing.
- Preserve the existing shared-link CSS variant contract. Asset byte-dependent data must never become a Java/J2CL/Closure input.
- Existing branch publication is authorized by the ongoing PR request; do not merge.

## Review history

- Round 1: two blockers, both accepted. T05 now owns all TodoMVC local SVG migrations; T03 specifies selected shared-font imports and T05 migrates all four Drumloop consumers. Round 2: Findings: none; both corrections verified. Planning gate passed.

## Promoted knowledge

- Pending final durable-contract classification under domain-modeling.
