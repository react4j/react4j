# Task Map

- Spec: [SPEC.md](../SPEC.md)
- Status: implementing
- Current frontier: T05
- Planning reviewer: imports_coverage_planning (3/3 rounds plus final confirmation consumed; passed)
- Plan checkpoint: automatic; revised review passed and latest user implementation authority persists
- Implementation reviewer: pending (0/5 rounds; final confirmation unused)
- Authority: latest user instruction authorizes all accepted import/peer/coverage work; no human checkpoint.

| ID | Task | Status | Blocked by |
| --- | --- | --- | --- |
| T01 | [Unified resource imports](T01-unified-imports.md) | complete | None |
| T02 | [Checked peer selectors and graph semantics](T02-peer-selectors.md) | complete | T01 |
| T03 | [Measured processor coverage](T03-processor-coverage.md) | complete | T02 |
| T04 | [Honest runtime coverage pipeline](T04-runtime-measurement.md) | complete | T03 |
| T05 | [Core and DOM coverage to 95/85](T05-runtime-tests.md) | pending | T04 |
| T06 | [Examples, permanent docs and complete validation](T06-examples-validation.md) | pending | T05 |

## Full-scope validation
Gate: tools/check.sh. Evidence: pending. Current baseline fails the newly required root floors; this delivery must close those gaps without weakening them.

## Sequencing
The primary agent implements sequentially. T04 validates the coverage seam before T05 expands tests; invalid source attribution or denominator assumptions reopen only that measurement decision. Read-only planning and implementation reviewers are independent. No implementation delegation is authorized by this workflow.

## Promoted knowledge
Not required unless canonical domain directories exist at promotion time.

## Planning review history
Round 1: blocked because per-root browser routing omitted JVM-only bodies in ReactConfig, despite separate JVM tests. Resolved by assigning disjoint source obligations using parsed @GwtIncompatible ranges, supplementing browser evidence with JVM-only counters in the effective runtime report, and requiring missing/unexecuted mixed-platform controls to fail the same root gate. Updated D4/D5, AC4 and T04/T05. Round 2: Findings: none. Reviewer confirmed mixed-platform obligations remain inside the enforced root total, with missing/unexecuted evidence controls. Residual instrumentation/source-attribution feasibility is explicitly gated by T04 before T05.

T04 probe-driven replan: the pinned Java coverage-only mode preserves functions but does not instrument ternary/short-circuit expression outcomes (verified locally and against the pinned BranchCoverageInstrumentationCallback). Per-generated-line counters can also collapse distinct Java origins. Revised D4/D5 and T04 to use explicit token-attributed Closure AST probes, with semantic and denominator controls; all existing thresholds, platform separation and JVM-only obligations remain. T01–T03 are committed and complete. Round 3 pending; budget remains shared across the replan.

Round 3: blocked because J2CL maps omit executable origins within single multiline expressions (ReactElement lines 120/136), so statement/token probes could still reduce the Java denominator. Fix: independent complete JVM-compilation line inventory supplies obligations only; browser-missing origins stay uncovered, safe completion attribution requires an unconditional same-path Java-AST proof, and controls cover skipped/throwing operands plus deferred bodies. Updated D4/D5, AC4, T04 and T05. Final confirmation pending; no review-budget reset.

Final confirmation: Findings: none. Reviewer verified D4/D5, AC4 and T04/T05 preserve the independent Java denominator and safe proof requirement, with omitted/skipped/throwing/deferred-expression controls. Residual risk remains the explicit T04 feasibility gate: enough safe attribution must exist to meet 95/85. All planning attempts, including final confirmation, are consumed.
