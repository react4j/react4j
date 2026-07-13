# Generated View Field Nullability Implementation Plan

## Status

Accepted on 2026-07-13.

## Phase Sequence

1. Correct the generator's internal field contract.
2. Regenerate or mechanically update only the affected expected outputs and changelog.
3. Validate fixture scope, processor tests, and the final diff.

## Implementation

### T01: Make native view-field nullability conditional

- In `ViewGenerator.buildNativeView(...)`, compute the existing immutable disposable input list before creating the
  native `view` field.
- Add `GeneratorUtil.NULLABLE_CLASSNAME` when that list is non-empty; otherwise retain
  `GeneratorUtil.NONNULL_CLASSNAME`.
- Remove the later duplicate declaration and reuse the same list when constructing the disposed-input predicate.
- Do not add a descriptor helper: the condition is local to this generated storage contract and the existing list is
  already needed by the constructor.

Target:

- `processor/src/main/java/react4j/processor/ViewGenerator.java`

Targeted checks:

- Inspect the generated source diff for one non-null disposable input, one nullable disposable input, one injected
  view, and one lite/full pair.
- Confirm unaffected native `view` fields remain `@Nonnull`.

### T02: Update expected outputs and release note

- Update `@Nonnull` to `@Nullable` on exactly the 13 nullable native `view` fields in each of the `expected` and
  `expectedFormatted` suites (26 fields across 22 files). The affected relative paths in both suites are:
  - `com/example/arez/React4j_MaybeTrackingHasComponentDependencyFieldView.java` (2)
  - `com/example/arez/React4j_NonObservableComponentDependencyView.java` (2)
  - `com/example/inject/React4j_ConstructorInputAndInjectArezComponentTypeComponent.java`
  - `com/example/prop/React4j_ImmutablePropTypeArezComponent.java`
  - `com/example/prop/React4j_ImmutablePropTypeArezComponentAndKeyed.java`
  - `com/example/prop/React4j_ImmutablePropTypeArezComponentLikeAndKeyed.java`
  - `com/example/prop/React4j_ImmutablePropTypeArezComponentLikeClass.java`
  - `com/example/prop/React4j_ImmutablePropTypeArezComponentLikeInterface.java`
  - `com/example/prop/React4j_ImmutablePropTypeArezComponentWithExplicitRequireId.java`
  - `com/example/prop/React4j_ImmutablePropTypeMultiple.java`
  - `com/example/prop/React4j_ImmutablePropTypes.java`
- Add an Unreleased changelog entry stating that nullable generated native-view storage is now annotated accurately.
- Do not edit fixture inputs or generated/build directories.

### T03: Validate and review

- Run `bundle exec buildr react4j:processor:test`.
- Search expected outputs to verify every `view = ... ? null : ...` field is `@Nullable` and unrelated fields remain
  `@Nonnull`.
- Run `git diff --check`, inspect `git status --short`, and review the complete diff.
- If a downstream Rose reproduction is available with the locally changed processor, confirm React4j-owned
  `FieldMissingNullable` diagnostics are absent. Record this as supplemental evidence, not a React4j completion gate,
  because the captured log was overwritten and Arez-owned failures are independently expected.

## Risks and Mitigations

- Risk: Annotating every disposable view field `@Nullable` would weaken accurate metadata for mutable-only cases.
  - Mitigation: Base the annotation on the exact immutable-disposable list that creates the ternary assignment.
- Risk: The field annotation and constructor predicate could drift.
  - Mitigation: Compute and reuse one local list for both.
- Risk: Fixture updates could become a broad generated-output rewrite.
  - Mitigation: Expect exactly 13 annotation changes in each fixture suite across the enumerated relative paths and
    reject unrelated output churn.
- Risk: A stricter downstream nullness checker could report guarded dereferences after the annotation is corrected.
  - Mitigation: Existing affected outputs guard lifecycle dereferences with `Disposable.isNotDisposed(view)`; capture
    any new checker finding as evidence and address it only if reproduced.

## Required Full Gate

```sh
bundle exec buildr react4j:processor:test
```

## Completion Criteria

- T01-T03 are complete with evidence recorded in the task board.
- All acceptance criteria in `00-requirements.md` are met.
- The final diff is limited to generator logic, 22 expected fixture files, `CHANGELOG.md`, and active plan tracking.
- No commit is created unless explicitly requested.
