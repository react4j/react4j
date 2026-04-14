# Implementation Plan

## Delivery Sequence
1. Refactor the processor input model so immutable inputs are no longer assumed to be method-backed.
2. Implement Phase 1 additive behavior and fixtures.
3. Layer in the Phase 2 warning path and warning suppression coverage.
4. Remove legacy method-based immutable support and the `immutable` annotation member for Phase 3.
5. Refresh docs/changelog and run validation gates.

## High-Risk Areas
- Constructor parameter splitting between injected services and immutable inputs.
- Generated constructor/factory signatures when a view mixes injection and immutable inputs.
- Reusing method-oriented validation/default/nullability logic for constructor-parameter inputs.
- Fixture churn across generated `React4j_*`, `*Builder`, and `*Factory` outputs.

## Mitigations
- Centralize origin-aware input metadata in `InputDescriptor`.
- Keep one source of truth for resolved input name/type/nullability and route all validation through it.
- Preserve builder behavior by continuing to store immutable inputs in the React input map and only changing how generated view constructors receive them.
- Add mixed-mode fixtures before final cleanup to guard migration behavior.

## Decision Log
- `Q-01`: Mixed-mode immutable inputs are allowed in Phase 1/2.
  - Plan impact: parsing and duplicate detection must operate across both origins.
- `Q-02`: Constructor `@Input` is immutable-only until Phase 3.
  - Plan impact: Phase 1/2 parse logic must reject constructor `@Input` unless immutable.

## Phase Details

### Phase 1
- Expand `@Input` target to include parameters.
- Make `InputDescriptor` origin-aware and allow constructor-parameter declarations.
- Parse constructor parameters into immutable inputs vs injection parameters.
- Update `ViewDescriptor`, `ViewGenerator`, and `FactoryGenerator` so generated constructors and factories only inject non-input parameters while forwarding immutable inputs from `NativeView.inputs()`.
- Preserve method-based immutable field caching and accessor generation.
- Add processor fixtures for constructor immutable inputs, mixed-mode views, and invalid constructor-input cases.
- Update docs/changelog to describe both styles and prefer constructor-based immutable inputs for new code.

### Phase 2
- Add warning key `React4j:MethodBasedImmutableInput`.
- Emit the warning only for method-based immutable inputs.
- Support existing suppression mechanisms for the warning.
- Add warning and suppression fixtures.
- Update docs/changelog to mark method-based immutable inputs as migration-only.

### Phase 3
- Remove `immutable` from `@Input` and update its Javadoc.
- Remove method-based immutable input parsing/generation.
- Switch constructor immutable fixtures/examples/docs to plain `@Input`.
- Update API diff fixtures and any downstream-facing examples.

## Required Full-Gate Command
- `bundle exec buildr test`
