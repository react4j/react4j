# Task Map

- Spec: [SPEC.md](../SPEC.md)
- Status: `review-passed`
- Current frontier: closeout
- Planning reviewer: `/root/docs_planning_review` (`1/3` rounds; final confirmation unused); Findings: none
- Plan checkpoint: automatic; accepted interview scope, explicit execution authority, and passing planning review
- Implementation reviewer: `/root/docs_implementation_review` (`1/5` rounds consumed; final confirmation unused); Findings: none
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

Round 1: Findings: none through `94f421a67`. The fresh reviewer checked every requirement and acceptance criterion, knowledge routing, independently reran guide probes and uncached development/optimized browser tests, keyboard opening/Tab/Escape/focus return, actual reduced-motion emulation, wide/narrow layouts, site and domain links. The full repository gate log was reviewed, not independently rerun. Final whitespace merge and preview URL changes were checked. Residual browser boundary remains pinned Chromium. All tasks and full-scope validation complete. Review and closeout are post-task workflow gates; task completion does not waive either gate.

## Concurrent integration

Remote user commit `4f95bf4fe` removes an extra changelog blank line. Merged without conflicts; preserved its change and applied the same spacing to the adjacent newly added note. No functional source or scope changed. Reviewer notified; no review-budget reset.

## Final knowledge and cleanup audit

No qualifying domain conclusion changed after promotion. Preview endpoint instructions belong to the user guide/README; concurrent changelog spacing remains with its source. Canonical domain links validate. Plan commit `d3f9ef827`, task commits `420e1e1b6`/`d26b098d7`, integration `6b799c7a7` and URL fix `94f421a67` exist in history. All tasks complete, full gate passed, independent review passed in one round. Closeout removes this exact tree, including both temporary verification scripts and the gate log; public guides, app, canonical contracts and ignored review screenshots have continuing users. PR120 remains the delivery target; no merge or site deployment is authorized.
