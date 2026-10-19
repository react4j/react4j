# Task Map

- Spec: [SPEC.md](../SPEC.md)
- Status: `implementing`
- Current frontier: `T03`
- Planning reviewer: `/root/java_browser_planning_review` (`1/3` rounds; final confirmation `unused`; Findings: none)
- Plan checkpoint: `automatic` (accepted implementation authority; planning review passed)
- Implementation reviewer: `pending` (`0/5` rounds; final confirmation `unused`)
- Authority: user explicitly requested the Java/JS split, then extended it to every JavaScript test added by PR112; no human checkpoint requested. Existing branch publication remains authorized; do not merge.
- Base: 424077f39 (visibility audit complete, full gate passed).

## Full-scope validation

- Gate: tools/check.sh, all changed browser scenarios in both modes, native/assertion boundary audit and document-link validation.
- Evidence: pending

## Tasks

| ID | Task | Status | Blocked by |
| --- | --- | --- | --- |
| T01 | [Core runtime and browser support](T01-core-and-support.md) | complete | None |
| T02 | [DOM probes and coverage drivers](T02-dom-and-coverage.md) | complete | T01 |
| T03 | [CSS browser assertions](T03-css-browser.md) | pending | T01 |
| T04 | [Example scenarios and full delivery](T04-examples-and-closeout.md) | pending | T02,T03 |

## Sequencing notes

- Primary implements sequentially, lowest eligible task first; reviewers remain read-only. No implementation delegation.
- Each step preserves the real browser runner and remains green before the next migration. T04 owns the final full-scope gate after every suite has migrated.
- JavaScript with no assertions may remain as a deliberate native/measurement/link fixture; audit its actual purpose instead of moving it mechanically.

## Review history

- Planning round 1: Findings: none. Reviewer checked every added/modified JS driver, native overlay discovery and measurement/attribution fixtures. Shared support is limited to concrete migrated cases; real modes, cleanup and coverage remain required. Final confirmation unused.

## Promoted knowledge

- pending classification; contributor test-language policy belongs in CONTRIBUTING.md. Existing coverage obligations are preserved, not redefined.
