# Task map

- Spec: [SPEC.md](../SPEC.md)
- Status: reviewed
- Current frontier: closeout and publication
- Plan checkpoint: automatic; user explicitly authorized implementation, no human checkpoint, planning review passed.
- Planning reviewer: browser_controls_planning; 1/3 rounds consumed, final confirmation unused; Findings: none. Complete scope, controls/defaults, optional HTML/config, assembly/cache boundaries, Rose delivery contracts and blocked full-Rose gate checked.
- Implementation reviewer: browser_controls_review; 2/5 rounds consumed, final confirmation unused. Round 1 found one P2 reserved-token validation regression; fixed in 47f1c7c68. Round 2: Findings: none. Original repro, new regressions and 10,000 mixed escaped-token candidates verified; complete prior scope remained covered. No waived findings. Repeat full tools/check.sh passed after the fix, including the isolated consumer, publication and 15 independent-build comparisons.

| Task | Status | Blocked by |
| --- | --- | --- |
| [T01 Independent browser application settings](T01-controls.md) | complete | None |
| [T02 Development overhead and cache boundaries](T02-performance.md) | complete | T01 |
| [T03 Rose public application delivery](T03-rose.md) | complete | T02 |
| [T04 Validation and documentation](T04-validation.md) | complete | T03 |

## Full-scope gate

Passed full tools/check.sh again after the review fix, Rose real public adapter fixtures (compression enabled/disabled), scoped tool suites, both documentation validators, local/production action graphs and bounded timing/cache probes. Whole Rose link/WAR/browser/iteration parity remains a pending migration gate: real Acal recheck still produces 393 shared dependency diagnostics. This does not become a passing or waived gate. Final remote pin resolution/module-lock validation waits for publication after review.

## Sequencing

Primary implements. Independent review, PR publication and exact tree deletion are workflow gates after task completion. Avoid formatter chat-owned work; inspect git status before edits/commits. React4j commit must be published before Rose’s final immutable pin.

## Knowledge promotion

Candidate classification (all accepted requirements pass qualification and replacement tests): runtime/optimization independence, defaults, typed define parity and downstream HTML → docs/specs/browser-applications.md; independent map publication and preserved internal maps → docs/specs/css-source-maps.md; Rose compression choice and local graph boundary → Rose docs/specs/client-application-delivery.md. Public attributes, artifact names and delivery commands → user READMEs/project_setup and code. Module-scoped parsing, CLI protocol and file layout are reversible implementation choices, below the ADR threshold. Timings and delivery sequence → this plan/PR evidence. Existing shared dependency duplication is a migration bug recorded in Rose draft PR6342, not a new deferred design question. No new glossary terms or essential use case.

Canonical changes are committed with implementation: Root docs/specs/browser-applications.md and docs/specs/css-source-maps.md, plus Rose docs/specs/client-application-delivery.md and ADR0043. Both repositories passed validate_doc_links.py.

Final domain sweep: the review fix preserves the existing token-validation requirement; it adds no durable contract or architectural choice. Existing promoted classifications remain valid, both validators pass, and no additional artifact is needed.
