# Task Map

- Spec: [SPEC.md](../SPEC.md)
- Status: reviewed
- Current frontier: closeout and draft PR push
- Planning reviewer: imports_coverage_planning (3/3 rounds plus final confirmation consumed; passed)
- Plan checkpoint: automatic; revised review passed and latest user implementation authority persists
- Implementation reviewer: imports_coverage_review (1/5 rounds consumed; passed; final confirmation unused)
- Authority: latest user instruction authorizes all accepted import/peer/coverage work; no human checkpoint.

| ID | Task | Status | Blocked by |
| --- | --- | --- | --- |
| T01 | [Unified resource imports](T01-unified-imports.md) | complete | None |
| T02 | [Checked peer selectors and graph semantics](T02-peer-selectors.md) | complete | T01 |
| T03 | [Measured processor coverage](T03-processor-coverage.md) | complete | T02 |
| T04 | [Honest runtime coverage pipeline](T04-runtime-measurement.md) | complete | T03 |
| T05 | [Core and DOM coverage to 95/85](T05-runtime-tests.md) | complete | T04 |
| T06 | [Examples, permanent docs and complete validation](T06-examples-validation.md) | complete | T05 |

## Full-scope validation
Gate: tools/check.sh passed on 2026-10-18, including formatting, both hermeticity modes, 36 development targets, six optimized browser targets, 18 JVM coverage targets, isolated consumers, site/release checks and 13 reproducibility comparisons. All root/class floors pass unchanged. Root line/branch percentages: core 97.84/92.65, dom 97.81/100, processor 96.22/86.12, css/processor 99.24/96.98, css/internal 97.75/93.18, css/bundler 98.18/93.53. Reports remain in reports/coverage; supporting implementation and task evidence are committed before review.

## Sequencing
The primary agent implements sequentially. T04 validates the coverage seam before T05 expands tests; invalid source attribution or denominator assumptions reopen only that measurement decision. Read-only planning and implementation reviewers are independent. No implementation delegation is authorized by this workflow.

## Promoted knowledge
Checked before implementation review: docs/adr, docs/glossary, docs/specs and docs/deferred do not exist. No domain-documentation promotion is required. Durable user/contributor guidance lives in css/README.md and CONTRIBUTING.md.

## Planning review history
Round 1: blocked because per-root browser routing omitted JVM-only bodies in ReactConfig, despite separate JVM tests. Resolved by assigning disjoint source obligations using parsed @GwtIncompatible ranges, supplementing browser evidence with JVM-only counters in the effective runtime report, and requiring missing/unexecuted mixed-platform controls to fail the same root gate. Updated D4/D5, AC4 and T04/T05. Round 2: Findings: none. Reviewer confirmed mixed-platform obligations remain inside the enforced root total, with missing/unexecuted evidence controls. Residual instrumentation/source-attribution feasibility is explicitly gated by T04 before T05.

T04 probe-driven replan: the pinned Java coverage-only mode preserves functions but does not instrument ternary/short-circuit expression outcomes (verified locally and against the pinned BranchCoverageInstrumentationCallback). Per-generated-line counters can also collapse distinct Java origins. Revised D4/D5 and T04 to use explicit token-attributed Closure AST probes, with semantic and denominator controls; all existing thresholds, platform separation and JVM-only obligations remain. T01–T03 are committed and complete. Round 3 pending; budget remains shared across the replan.

Round 3: blocked because J2CL maps omit executable origins within single multiline expressions (ReactElement lines 120/136), so statement/token probes could still reduce the Java denominator. Fix: independent complete JVM-compilation line inventory supplies obligations only; browser-missing origins stay uncovered, safe completion attribution requires an unconditional same-path Java-AST proof, and controls cover skipped/throwing operands plus deferred bodies. Updated D4/D5, AC4, T04 and T05. Final confirmation pending; no review-budget reset.

Final confirmation: Findings: none. Reviewer verified D4/D5, AC4 and T04/T05 preserve the independent Java denominator and safe proof requirement, with omitted/skipped/throwing/deferred-expression controls. Residual risk remains the explicit T04 feasibility gate: enough safe attribution must exist to meet 95/85. All planning attempts, including final confirmation, are consumed.

## Implementation review history
Round 1 on 01c9cbbc1: Findings: none. Fresh read-only reviewer inspected the complete 420b502b6..01c9cbbc1 delivery against T01–T06, imports/peers/graph semantics, full/header compilation, actual native behavior and honest coverage inventories/attribution. Reviewer independently reran 13 focused targets and compiled disposable compound-assignment/array-expression probes through pinned J2CL; no premature completion issue was established. Full-gate log and all unchanged threshold totals were verified. Residual delivery gates are exact plan cleanup and pushing draft PR #112; no merge is authorized.
