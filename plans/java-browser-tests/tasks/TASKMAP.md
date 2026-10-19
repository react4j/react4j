# Task Map

- Spec: [SPEC.md](../SPEC.md)
- Status: `complete`
- Current frontier: `closeout`
- Planning reviewer: `/root/java_browser_planning_review` (`1/3` rounds; final confirmation `unused`; Findings: none)
- Plan checkpoint: `automatic` (accepted implementation authority; planning review passed)
- Implementation reviewer: `/root/java_browser_implementation_review` (`1/5` rounds; final confirmation `unused`; Findings: none)
- Authority: user explicitly requested the Java/JS split, then extended it to every JavaScript test added by PR112; no human checkpoint requested. Existing branch publication remains authorized; do not merge.
- Base: 424077f39 (visibility audit complete, full gate passed).

## Full-scope validation

- Gate: tools/check.sh, all changed browser scenarios in both modes, native/assertion boundary audit and document-link validation.
- Evidence: `tools/check.sh` passed (45 default tests, 8 optimized browsers, 23 JVM coverage targets, runtime collector in both environments, unchanged floors, hermeticity, external consumer, site/release and 15 independent archive checks). Final CSS timeout configuration subsequently passed all three variants in both modes. Formatter/buildifier, case-name/native-boundary audit and canonical link validation passed. See T04 for prior-CI timeout evidence and the isolated local WebDriver startup retry.

## Tasks

| ID | Task | Status | Blocked by |
| --- | --- | --- | --- |
| T01 | [Core runtime and browser support](T01-core-and-support.md) | complete | None |
| T02 | [DOM probes and coverage drivers](T02-dom-and-coverage.md) | complete | T01 |
| T03 | [CSS browser assertions](T03-css-browser.md) | complete | T01 |
| T04 | [Example scenarios and full delivery](T04-examples-and-closeout.md) | complete | T02,T03 |

## Sequencing notes

- Primary implements sequentially, lowest eligible task first; reviewers remain read-only. No implementation delegation.
- Each step preserves the real browser runner and remains green before the next migration. T04 owns the final full-scope gate after every suite has migrated.
- JavaScript with no assertions may remain as a deliberate native/measurement/link fixture; audit its actual purpose instead of moving it mechanically.

## Review history

- Planning round 1: Findings: none. Reviewer checked every added/modified JS driver, native overlay discovery and measurement/attribution fixtures. Shared support is limited to concrete migrated cases; real modes, cleanup and coverage remain required. Final confirmation unused.

- Implementation round 1: Findings: none. Reviewer independently compared the complete migration to original scenarios, checked native boundaries, resource/lifecycle behavior, overlay discovery, unchanged coverage, explicit builds/visibility/formatting, gate logs and final CSS retry evidence. Diff and canonical links passed. Residual: Linux CI on the final publication remains pending.

## Promoted knowledge

- Contributor test-language policy is recorded in [CONTRIBUTING.md](../../../CONTRIBUTING.md#browser-and-distribution-boundaries). It owns the development workflow; no duplicate domain specification is needed.
- Coverage obligations and floors retain their existing [contributor contract](../../../CONTRIBUTING.md#coverage). CSS behavior, assets and maps retain their existing [liveness](../../../docs/specs/css-liveness.md), [asset](../../../docs/specs/web-assets.md) and [source-map](../../../docs/specs/css-source-maps.md) owners; this migration changes no product contract.
- Native/Java fixture structure and runner choice are reversible test implementation decisions below the ADR threshold. No new domain language or deliberately deferred product requirement was introduced. Deferred compression remains outside this migration.
- Domain classification completed using domain-modeling qualification/replacement gates. Canonical link validation passed.

## Final knowledge sweep

- No new durable conclusions since promotion. CONTRIBUTING.md owns the Java/native testing policy; the existing product and coverage contracts remain unchanged. All tasks and checks are complete; independent review passed on its first round. The active plan tree may be removed after this evidence is committed.
