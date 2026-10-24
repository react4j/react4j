# Task Map

- Spec: [SPEC.md](../SPEC.md)
- Status: `review-passed`
- Current frontier: `none` (design task and independent review complete; closeout follows)
- Planning reviewer: `planning_review` (`2/3` rounds; passed; final confirmation `unused`)
- Plan checkpoint: `automatic` — confirmed design-only scope, explicit PR authority and passing planning review; no human plan pause requested.
- Implementation reviewer: `design_review` (`1/5` rounds; passed; final confirmation `unused`)
- Scope and authority: confirmed design-only interview plus explicit PR delivery request; source recorded in [SPEC.md](../SPEC.md#source).

## Full-scope validation

- Gate: documentation links/fences/JSON, checked numeric examples, full design consistency review and final documentation-only diff audit.
- Evidence: passed domain link validation and explicit design/plan checks (16 local links, 17 fenced examples/JSON, 15 types, 16 literal utilities, 27 families, five scale calculations and two range cases), all 17 W3C fragments, and staged documentation-only diff audit. Compiler/browser gates are intentionally unrun for this design-only delivery; no runtime behavior is claimed.

## Tasks

| ID | Task | Status | Blocked by |
| --- | --- | --- | --- |
| `T01` | [Deliver the expanded language and package design](T01-design.md) | `complete` | None |

## Sequencing notes

- One coherent design slice owns the grammar, examples, package boundary and verification. Workflow review and publication follow it; no compiler implementation is authorized.
- The active tree is exactly `plans/native-utility-language-design/`. Resolve this path before each plan edit, commit and closeout deletion.
- Workflow closeout owns the spec's delivery obligations after T01: independent review, removal of this tree and PR publication. These do not block task completion and are checked before claiming delivery.

## Review history

- Planning round 1: `planning_review` reported `Findings: none`. Checked complete scope/authority, task coverage, domain routing, package dependencies, finite contracts, Rose facts and proportionate documentation validation. Residual risk: no parser or browser behavior has been implemented or verified. No finding dispositions required; two ordinary rounds remain, final confirmation unused.
- Planning round 2: author preflight separated post-task review, cleanup and publication from T01 completion to remove a circular gate. The same reviewer confirmed `Findings: none` across all three plan files. Scope, authority and significant decisions are unchanged. One ordinary round remains; final confirmation unused.
- Implementation/document review round 1: `design_review` reported `Findings: none`. Independently covered R1–R5/AC1–AC5, complete grammar/bounds/dispatch, wrapper composition, anchor identity/scope, package responsibilities/dependencies, shared metadata/liveness and Rose recipe. Checked current contracts/build boundaries, Rose source, W3C semantics, design links/fragments, 17 fences/JSON and whitespace. Four ordinary rounds remain; final confirmation unused. No finding dispositions required. Residual risk remains explicitly unimplemented grammar/compiler and untested browser behavior.

## Promoted knowledge

- Proposed behavior belongs in [the language design](../../../docs/design/utility-language.md). Current specifications/ADRs remain authoritative for implemented behavior; this delivery does not replace them.
- Domain classification: R1–R5 are design requirements pending PR review and future implementation, owned by that document. Existing JVM/finite-CSS obligations retain their current specification/ADR owners. The proposed package has no implemented hard-to-reverse structural change requiring a new adopted ADR; no new glossary meaning is introduced. Bounds, query/anchor behavior and deferred implementation caveats have a continuing use in the design, not in a retained delivery plan.
- Final knowledge sweep: no conclusions changed after classification or independent review. The design is the lasting owner; this completed plan and its task evidence can be deleted after this review record is committed. Publication remains ready-for-review only, with human merge pending.
