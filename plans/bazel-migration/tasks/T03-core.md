# T03 — Core JVM contracts and tests

- Status: `complete`
- Blocked by: `T02`
- Spec coverage: `R1`, `R3`, `R4`, `R6`, `R7`, `AC1`, `AC2`

## Delivers

Build/test core under Bazel against migrated Arez artifacts. Convert core nullness to JSpecify, enforce complete checker policy, remove redundant typed checks, preserve dynamic/invariant behavior and runtime flags. Isolate maintained-source formatting from semantic edits within the task commits if feasible.

## Acceptance criteria

- [x] Core JVM tests and package dependency tests pass using Bazel locations/properties. Full checker policy passes and focused behavior checks demonstrate dynamic-null validation remains.

## Validation

- bazel test core JVM targets; full-policy core compilation; meaningful dynamic-boundary regression tests — proves the delivered behavior. Record actual labels, commands and outcomes below; do not mark complete with skipped required checks.

## Evidence

- Mechanical formatting checkpoint: formatter applied to 49 core main/test files. Lexical comparison with prior commit passes after normalizing equivalent static/abstract modifier ordering and split constant string literals. Semantic migration and Bazel validation remain in progress.

- `bazel test //core:tests --test_output=errors`: passed, 12 TestNG cases including nullable default forwarding and missing/duplicate/primitive context invariants plus JDepend package constraints. The class archive is supplied through Bazel runfiles, replacing Buildr directory properties.
- `bazel build //:java_format_check` and `bazel run //tools/buildifier:buildifier_check`: passed. Full core main/test compilation uses unchanged Arez checker flags; package scopes are NullMarked.
- Archive class-version inspection: all owned classes major 61 (Java 17). Source check: no javax Nonnull/Nullable imports remain.
- Nullable React property-map values, keys/refs/owner and native props/state are explicit. Only native/factory field initialization has narrowly scoped NullAway.Init suppression. Context consumers accept null defaults and may render null. Scheduler lock dereference asserts the existing scheduled-callback invariant.
- Removed redundant typed parameter guards while retaining Contexts dynamic/registration invariants. J2CL/React-native behavior will also be exercised in T09/T10; processor-generated dynamic prop/context guards remain owned by T06/T07.
- `git diff --check`: passed; source and BUILD diffs inspected. Current annotation Javadocs still need their planned T13 migration text updates.
