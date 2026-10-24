# Task Map

- Spec: [SPEC.md](../SPEC.md)
- Status: `planned`
- Current frontier: `T01`
- Planning reviewer: `implementation_plan_review` (`1/3` rounds; passed; final confirmation `unused`)
- Plan checkpoint: `automatic` — explicit implementation authority and passing independent planning review; no human pause requested.
- Implementation reviewer: `pending` (`0/5` rounds; final confirmation `unused`)
- Authority: explicit user implementation authorization lifting design-only; JVM-only and separate package remain mandatory.

## Full-scope validation

- Gate: task tests; development/optimized browser scenarios; `tools/check.sh`; domain-link validation; complete diff and package/dependency audit.
- Evidence: `pending`

## Tasks

| ID | Task | Status | Blocked by |
| --- | --- | --- | --- |
| `T01` | [Expand checked rules through the isolated JVM facade](T01-language-facade.md) | `pending` | None |
| `T02` | [Consume native rules through checked Java declarations](T02-java-consumption.md) | `pending` | T01 |
| `T03` | [Link independently compiled candidate fragments](T03-fragment-delivery.md) | `pending` | T02 |
| `T04` | [Accept broader typed values and numeric limits](T04-value-domains.md) | `pending` | T01 |
| `T05` | [Apply container and starting-style variants](T05-conditional-variants.md) | `pending` | T03, T04 |
| `T06` | [Deliver checked named anchors and scoped popovers](T06-named-anchors.md) | `pending` | T05 |
| `T07` | [Expose agent authoring feedback and packaged commands](T07-authoring-tools.md) | `pending` | T06 |
| `T08` | [Verify and document the complete delivered contract](T08-contract-and-gate.md) | `pending` | T07 |

## Sequencing notes

- Select the lowest-numbered unblocked pending task. T04 may be unblocked while T02/T03 run; it does not require their metadata changes. Keep only one task in progress.
- Exact active tree: `plans/native-utility-language/`. Resolve it before every plan edit, commit and deletion. The prior design-delivery tree was closed and is not resumed.
- Workflow owns post-task independent review, PR publication and plan cleanup. The PR is stacked on `codex/react4j-explore-tailwind-language-and`; human merge remains pending.
- Promotion owners: accepted behavior belongs in utility/binding/naming/liveness/source-map specifications, resolved terms in the glossary, adopted JVM rule compilation rationale in the replacing ADR, and usable author reference/examples beside the tooling. Design-only claims must be reconciled with implementation.

## Review history

- Planning round 1: `implementation_plan_review` reported `Findings: none`. Checked all eight tasks, accepted scope, task graph/coverage, processor/sidecar/header/catalogue/linker/bundler/name boundaries, distribution/browser/full-gate verification. Residual uncertainties (typed-hole flattening, shared binding extraction, browser semantics) have explicit task-owned checks. No finding dispositions required; two ordinary rounds and one final confirmation remain.
- Implementation: not started; five rounds plus one final confirmation available.

## Promoted knowledge

- Pending T08 classification and promotion. Preserve current specifications until delivered behavior justifies their updates; [accepted design](../../../docs/design/utility-language.md) is the current implementation input.
