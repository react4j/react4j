# T04 — DOM JVM contracts and tests

- Status: `in_progress`
- Blocked by: `T03`
- Spec coverage: `R2`, `R3`, `R4`, `R6`, `R7`, `AC1`, `AC2`

## Delivers

Build/test authored and generated DOM Java under the complete policy, preserving event/factory APIs and packaging inputs.

## Acceptance criteria

- [ ] DOM factory generation and JVM/package-boundary tests pass. Owned DOM nullability is JSpecify; authored and generated code satisfy full checking.

## Validation

- bazel test DOM JVM targets; compile generated DOM output under full policy — proves the delivered behavior. Record actual labels, commands and outcomes below; do not mark complete with skipped required checks.

## Evidence

- Mechanical formatting checkpoint: 66 DOM main/test files formatted; equivalent Java token comparison against the previous commit passed. Semantic migration and compilation remain in progress.
