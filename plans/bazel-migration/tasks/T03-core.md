# T03 — Core JVM contracts and tests

- Status: `in_progress`
- Blocked by: `T02`
- Spec coverage: `R1`, `R3`, `R4`, `R6`, `R7`, `AC1`, `AC2`

## Delivers

Build/test core under Bazel against migrated Arez artifacts. Convert core nullness to JSpecify, enforce complete checker policy, remove redundant typed checks, preserve dynamic/invariant behavior and runtime flags. Isolate maintained-source formatting from semantic edits within the task commits if feasible.

## Acceptance criteria

- [ ] Core JVM tests and package dependency tests pass using Bazel locations/properties. Full checker policy passes and focused behavior checks demonstrate dynamic-null validation remains.

## Validation

- bazel test core JVM targets; full-policy core compilation; meaningful dynamic-boundary regression tests — proves the delivered behavior. Record actual labels, commands and outcomes below; do not mark complete with skipped required checks.

## Evidence

- Mechanical formatting checkpoint: formatter applied to 49 core main/test files. Lexical comparison with prior commit passes after normalizing equivalent static/abstract modifier ordering and split constant string literals. Semantic migration and Bazel validation remain in progress.
