# Task Map

- Spec: [SPEC.md](../SPEC.md)
- Status: `planned`
- Current frontier: `T01`
- Planning reviewer: `/root/docs_planning_review` (`1/3` rounds; final confirmation unused); Findings: none
- Plan checkpoint: automatic; accepted interview scope, explicit execution authority, and passing planning review
- Implementation reviewer: pending (`0/5` rounds; final confirmation unused)
- Authority: final interview confirmation and explicit implementation instruction in this chat.

## Full-scope validation

- Gate: `tools/check.sh`; domain link validator; targeted documented CLI/recipe checks; narrow/wide app visual inspection.
- Evidence: pending.

## Tasks

| ID | Task | Status | Blocked by |
| --- | --- | --- | --- |
| T01 | [Runnable native teaching app](T01-native-example.md) | pending | None |
| T02 | [Public learning paths and knowledge audit](T02-guides.md) | pending | T01 |

## Sequencing notes

- T01 establishes the exact teaching vocabulary and browser behavior before T02 documents it.
- The existing uncommitted CssInputIntegrationTest cleanup was authorized and verified in the preceding turn; committed separately as `94f513b5a`; all three methods passed an uncached targeted run.
- Read-only reviewer/fact-finder agents may assist; the primary agent implements tasks sequentially.

## Promoted knowledge

Pending audit against existing glossary, native utility specification, shared CSS contracts and ADRs 0005/0006. Documentation and example delivery choices are implementation guidance, not automatically new domain requirements.

## Planning review

Round 1: Findings: none. Reviewed all plan artifacts, requirement/task coverage, website and example build evidence, verification and cleanup. Residual focus/reduced-motion risks are owned by T01 browser checks.
