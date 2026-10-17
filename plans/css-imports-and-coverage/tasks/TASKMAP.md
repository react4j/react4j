# Task Map

- Spec: [SPEC.md](../SPEC.md)
- Status: implementing
- Current frontier: T03
- Planning reviewer: imports_coverage_planning (2/3 rounds; passed; final confirmation unused)
- Plan checkpoint: automatic; latest user implementation authority and passing planning review
- Implementation reviewer: pending (0/5 rounds; final confirmation unused)
- Authority: latest user instruction authorizes all accepted import/peer/coverage work; no human checkpoint.

| ID | Task | Status | Blocked by |
| --- | --- | --- | --- |
| T01 | [Unified resource imports](T01-unified-imports.md) | complete | None |
| T02 | [Checked peer selectors and graph semantics](T02-peer-selectors.md) | complete | T01 |
| T03 | [Measured processor coverage](T03-processor-coverage.md) | pending | T02 |
| T04 | [Honest runtime coverage pipeline](T04-runtime-measurement.md) | pending | T03 |
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
