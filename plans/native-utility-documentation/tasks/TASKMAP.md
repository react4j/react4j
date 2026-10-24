# Task Map

- Spec: [SPEC.md](../SPEC.md)
- Status: `implementation-review`
- Current frontier: independent implementation review
- Planning reviewer: `/root/docs_planning_review` (`1/3` rounds; final confirmation unused); Findings: none
- Plan checkpoint: automatic; accepted interview scope, explicit execution authority, and passing planning review
- Implementation reviewer: `/root/docs_implementation_review` (`1/5` rounds consumed; final confirmation unused), review in progress
- Authority: final interview confirmation and explicit implementation instruction in this chat.

## Full-scope validation

- Gate: `tools/check.sh`; domain link validator; targeted documented CLI/recipe checks; narrow/wide app visual inspection.
- Evidence: `tools/check.sh` exit 0; all checks passed. Domain validator, documented compiler probes, browser keyboard/reduced-motion checks and wide/narrow screenshots passed. See task evidence and [full gate log](../full-gate.log).

## Tasks

| ID | Task | Status | Blocked by |
| --- | --- | --- | --- |
| T01 | [Runnable native teaching app](T01-native-example.md) | complete | None |
| T02 | [Public learning paths and knowledge audit](T02-guides.md) | complete | T01 |

## Sequencing notes

- T01 establishes the exact teaching vocabulary and browser behavior before T02 documents it.
- The existing uncommitted CssInputIntegrationTest cleanup was authorized and verified in the preceding turn; committed separately as `94f513b5a`; all three methods passed an uncached targeted run.
- Read-only reviewer/fact-finder agents may assist; the primary agent implements tasks sequentially.

## Promoted knowledge

[Knowledge routing audit](../knowledge-audit.md) complete. Existing [native contract](../../../docs/specs/native-utility-language.md) now explicitly records separate case-sensitive namespaces and rejection of empty/duplicate candidate requests. [Bindings](../../../docs/specs/css-bindings.md) clarify distinct lists. [Glossary](../../../docs/glossary/README.md) and ADRs [0005](../../../docs/adr/0005-compile-declarative-utility-rules-on-jvm.md)/[0006](../../../docs/adr/0006-publish-checked-utility-expansions.md) retain current owners. No new ADR, glossary term, essential use case or deferred commitment qualifies. Specification-only diff audit and domain link validator pass.

## Planning review

Round 1: Findings: none. Reviewed all plan artifacts, requirement/task coverage, website and example build evidence, verification and cleanup. Residual focus/reduced-motion risks are owned by T01 browser checks.

## Implementation review

Round 1 in progress with a fresh read-only reviewer; all tasks and full-scope validation complete. Review and closeout are post-task workflow gates; task completion does not waive either gate.

## Concurrent integration

Remote user commit `4f95bf4fe` removes an extra changelog blank line. Merged without conflicts; preserved its change and applied the same spacing to the adjacent newly added note. No functional source or scope changed. Reviewer notified; no review-budget reset.
