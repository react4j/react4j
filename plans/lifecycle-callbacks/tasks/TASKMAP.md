# Task map

- Spec: [SPEC.md](../SPEC.md)
- Status: `implementation-review`
- Current frontier: none
- Planning reviewer: `/root/planning_review` (`1/3`; final confirmation unused; passed)
- Plan checkpoint: automatic; confirmed scope, explicit delivery/PR authority, and passing planning review.
- Implementation reviewer: `/root/implementation_review` (`1/5`; final confirmation unused; recheck pending)
- Authority: user confirmed the design and explicitly requested implementation and a PR on 2026-10-26.

## Tasks

| ID | Task | Status | Blocked by |
| --- | --- | --- | --- |
| T01 | [Ordered multiple callbacks](T01-ordered-callbacks.md) | complete | None |
| T02 | [Remove combined annotation and prove runtime behavior](T02-remove-combined.md) | complete | T01 |

## Full-scope validation

- Gate: `tools/check.sh`
- Evidence: passed completely on 2026-10-26 with pinned Bazel 9.3.1; see T02 for suite, coverage, site and unsigned-distribution results.

## Review history

- Planning round 1: Findings: none. Reviewed full spec/task coverage, repository claims, validation, migration and cleanup. Implementation results remain to be verified.
- Implementation round 1: one P2 finding. The durable disposed-view requirement exceeded the accepted disposed-dependency guard scope. Qualified requirement 14 to views with disposable inputs, matching the generator and browser scenario; runtime behavior is unchanged. Reviewer checked the complete diff and reran processor, development/optimized browser and documentation-link checks; no other findings.

## Promoted knowledge

- Durable participation, ordering and lifecycle behavior: [lifecycle callback specification](../../../docs/specs/lifecycle-callbacks.md).
- Adopted inheritance tradeoff and rationale: [registration-order ADR](../../../docs/adr/0007-preserve-lifecycle-registration-order.md).
- Annotation removal/migration: Unreleased changelog and public annotation Javadocs. Implementation details remain in code; verification and delivery evidence remain in this temporary tree.
