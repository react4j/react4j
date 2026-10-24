# Task Map

- Spec: [SPEC.md](../SPEC.md)
- Status: `implementing`
- Current frontier: `T01`
- Planning reviewer: `planning_review` (`2/3` rounds; passed; final confirmation `unused`)
- Plan checkpoint: `automatic` — confirmed design-only scope, explicit PR authority and passing planning review; no human plan pause requested.
- Implementation reviewer: `pending` (`0/5` rounds; final confirmation `unused`)
- Scope and authority: confirmed design-only interview plus explicit PR delivery request; source recorded in [SPEC.md](../SPEC.md#source).

## Full-scope validation

- Gate: documentation links/fences/JSON, checked numeric examples, full design consistency review and final documentation-only diff audit.
- Evidence: `pending`

## Tasks

| ID | Task | Status | Blocked by |
| --- | --- | --- | --- |
| `T01` | [Deliver the expanded language and package design](T01-design.md) | `in_progress` | None |

## Sequencing notes

- One coherent design slice owns the grammar, examples, package boundary and verification. Workflow review and publication follow it; no compiler implementation is authorized.
- The active tree is exactly `plans/native-utility-language-design/`. Resolve this path before each plan edit, commit and closeout deletion.
- Workflow closeout owns the spec's delivery obligations after T01: independent review, removal of this tree and PR publication. These do not block task completion and are checked before claiming delivery.

## Review history

- Planning round 1: `planning_review` reported `Findings: none`. Checked complete scope/authority, task coverage, domain routing, package dependencies, finite contracts, Rose facts and proportionate documentation validation. Residual risk: no parser or browser behavior has been implemented or verified. No finding dispositions required; two ordinary rounds remain, final confirmation unused.
- Planning round 2: author preflight separated post-task review, cleanup and publication from T01 completion to remove a circular gate. The same reviewer confirmed `Findings: none` across all three plan files. Scope, authority and significant decisions are unchanged. One ordinary round remains; final confirmation unused.
- Implementation: not started; five rounds available plus one final confirmation for existing findings.

## Promoted knowledge

- Proposed behavior belongs in [the language design](../../../docs/design/utility-language.md). Current specifications/ADRs remain authoritative for implemented behavior; this delivery does not replace them.
