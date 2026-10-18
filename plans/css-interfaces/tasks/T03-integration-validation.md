# T03 — Separate compilation and full validation

- Status: pending
- Blocked by: T02
- Spec coverage: R7/AC7 and cross-boundary verification of AC1–AC6

## Delivers

Separate compiled interfaces, real JVM/J2CL/browser consumers, permanent documentation and full-scope evidence for the complete interface feature.

## Acceptance criteria

- [ ] Separate contract libraries exercise inherited annotations and generic signatures through real header jars, for components and resource bindings.
- [ ] Shared-interface browser consumers use two independent style implementations and checked symbols in development and optimized builds.
- [ ] Focused tests cover deprecation, nullness, nested type shadowing, malformed contracts and deterministic declaration ordering.
- [ ] README/changelog explain the accepted interface contracts; simplify pass completes.
- [ ] tools/check.sh passes without weakening coverage thresholds; all diffs/goldens are inspected and evidence recorded before commit.

## Validation

Run focused integration/browser suites first, then tools/check.sh. Follow independent implementation review and closeout; remove exactly the active temporary plan after preserving review evidence in git history.

## Evidence

Pending.
