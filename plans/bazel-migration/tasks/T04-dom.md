# T04 — DOM JVM contracts and tests

- Status: `complete`
- Blocked by: `T03`
- Spec coverage: `R2`, `R3`, `R4`, `R6`, `R7`, `AC1`, `AC2`

## Delivers

Build/test authored and generated DOM Java under the complete policy, preserving event/factory APIs and packaging inputs.

## Acceptance criteria

- [x] DOM factory generation and JVM/package-boundary tests pass. Owned DOM nullability is JSpecify; authored and generated code satisfy full checking.

## Validation

- bazel test DOM JVM targets; compile generated DOM output under full policy — proves the delivered behavior. Record actual labels, commands and outcomes below; do not mark complete with skipped required checks.

## Evidence

- Mechanical formatting checkpoint: 66 DOM main/test files formatted; equivalent Java token comparison against the previous commit passed. Semantic migration and compilation remain in progress.

- `bazel test //dom:tests --test_output=errors`: passed, four TestNG cases: original package-boundary test and meaningful class-name regressions (nullable elements, existing value on absent input, development validation of blanks).
- Authored DOM plus generated source archive compile under the full reference checker policy. All 529 DOM factory methods are present; all class versions are Java 17 (61).
- `bazel test //core:tests //dom:tests`: both targets passed before the additional DOM behavior cases; the updated complete DOM target then passed independently. `bazel build //:java_format_check` and buildifier check passed after all changes.
- Optional native HTML/CSS reference properties and nullable class-name elements are explicit. Existing non-null/enum constraints remain; event related targets permit null. Native externally initialized fields suppress only initialization checking; the F-bounded self cast suppresses only SelfAlwaysReturnsThis.
- Generator templates now reinterpret nullable property maps, use legal JSpecify local-variable locations and satisfy the complete compile policy. Expected API signatures remain unchanged apart from nullness contracts.
- No legacy nullability imports remain in owned DOM Java. `git diff --check` passed; source/build/generator diffs inspected. Browser-native behavior is also owned by T09/T10.
