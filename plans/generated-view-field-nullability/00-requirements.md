# Generated View Field Nullability Requirements

## Status

Accepted on 2026-07-13 by user instruction.

## Problem

`ViewGenerator.buildNativeView(...)` always annotates the generated native `view` field with `@Nonnull`, but assigns
`null` when an immutable disposable input is already disposed. Error Prone consequently reports
`FieldMissingNullable` against downstream `React4j_*.java` sources.

## Scope

In scope:

- Make the generated field annotation describe the field's actual internal lifecycle state.
- Preserve `@Nonnull` for generated fields whose constructor cannot assign `null`.
- Update existing expected generated-source fixtures.
- Add the required Unreleased changelog entry.
- Validate processor generation and fixture comparisons.

Out of scope:

- Changing source `@Input` nullability or public view contracts.
- Suppressing Error Prone diagnostics.
- Changing the disposed-input lifecycle behavior.
- Fixing Arez-generated collection cache fields.
- Adding compatibility paths or configuration flags.

## Requirements

1. A native `view` field must be generated `@Nullable` exactly when the view has one or more immutable disposable
   inputs, because that is the condition that can produce the `? null` constructor assignment.
2. All other native `view` fields must remain `@Nonnull`, including views with only mutable disposable inputs.
3. The same computed immutable-disposable input collection must drive the annotation and constructor predicate so the
   two contracts cannot diverge.
4. Existing generated behavior and lifecycle guards must remain unchanged.
5. Existing fixture inputs must remain unchanged because they already cover nullable/non-null disposable inputs,
   injection, and lite/full native views.

## Acceptance Criteria

- The generator no longer unconditionally adds `@Nonnull` to the native `view` field.
- Every expected field paired with a generated `view = ... ? null : ...` assignment is `@Nullable`.
- Unaffected expected native `view` fields remain `@Nonnull`.
- The 13 affected fields in each of the `expected` and `expectedFormatted` suites are updated (26 fields across 22
  generated-source files) and no unrelated fixture output changes.
- `CHANGELOG.md` contains a concise entry under `Unreleased`.
- `bundle exec buildr react4j:processor:test` passes.
- The final diff passes `git diff --check` and contains no scratch or generated build artifacts.

## Decisions

- Use conditional field metadata rather than a suppression: accepted.
- Key the condition to immutable disposable inputs rather than `ViewDescriptor.hasDisposableInput()`: accepted.
- Reuse existing fixtures instead of adding a new fixture input: accepted.
- Treat the plan as approved without an additional review pause: accepted by explicit user instruction.

## Open Questions

None.
