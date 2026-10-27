# Input semantics

## Source

- Accepted scope: this chat's grill-me interview, final confirmation, typed-provider and exact-type-default selections, and Arez specialization correction.
- Execution authority: user invoked structured-delivery-workflow and requested a PR for review. Plan checkpoint is automatic; no implementation or publication beyond the PR is requested.
- Evidence: `core/.../annotations/Input.java`, `Keyed.java`, processor input descriptors and generators, processor strict golden harness, core browser harness, and pinned Arez comparator resolution/generation.

## Problem and outcome

Fresh equal arrays cause unnecessary view replacement or input-driven work. Applications cannot select per-input immutable identity or mutable equality, despite existing Arez equality policies. Deliver the accepted defaults and explicit hooks with checked generated code and real-browser evidence.

## Requirements

- R1: Declared String[] and the eight primitive arrays contribute ordered content to immutable identity. Null, empty, element boundaries and combined input boundaries are distinct. Floating values follow immutable scalar key semantics: NaNs share identity and signed zeros share identity. Inputs remain caller-owned and unchanged; no copying. Object/generic/nested-array defaults retain existing behavior.
- R2: Immutable constructor inputs accept an explicit generic InputKeyGenerator<T> provider; validate effective input type, generic compatibility, nullness, construction and accessibility. Its non-null string overrides ordinary key derivation for that input. Providers receive declared Java values, including nullable values when allowed.
- R3: Mutable method input equality resolves explicit Input.equalityComparator, then Arez DefaultEqualityComparator on the exact effective declared type, then JavaScript strict equality. Do not inspect bounds, supertypes, array component types or type-use annotations. Source and classpath metadata behave consistently.
- R4: One resolved equality governs rendering, Arez invalidation and PRE/POST OnInputChange predicates. Preserve other rendering causes, latest mutable props, and existing multi-input callback conjunction semantics. Providers are deterministic/effect-free; invocation counts are unspecified.
- R5: Recognize exact Arez ObjectsEqualsComparator and ObjectsDeepEqualsComparator classes and emit equivalent direct calls without provider allocations. Custom comparators retain dispatch. Preserve null, floating and Java boxing semantics; strict fallback remains distinct.
- R6: Identity covers effective inputs supplied explicitly, omitted/defaulted, through context, and in combined keys. Explicit custom keys take precedence; React4j owns collision-safe composition and component identity. No view instance is required by key providers.
- R7: Prove behavior with strict formatted/unformatted compilation, diagnostics, browser lifecycle/invalidation/callback counts in development and optimized modes, and the complete local gate. Make no timing claim.
- R8: Promote durable contracts, document migration and provide a Rose-agent adoption prompt. No Rose changes, releases or secrets access.

## Acceptance criteria

- AC1 (R1,R6): Equal fresh supported arrays reuse; different ordered values remount. Adversarial strings and combined parts cannot collide. Defaults/context and primitive edge values are covered.
- AC2 (R2,R6): Typed explicit keys work through all paths; incompatible, nullable-contract and unusable providers diagnose. Equal custom keys retain constructor identity.
- AC3 (R3,R4,R5): Explicit policies override exact-type defaults, default exclusions and classpath metadata are tested. Equivalent replacements skip only input-driven effects while changed values propagate. Goldens verify builtin direct calls/custom dispatch and proper Java value adaptation.
- AC4 (R7,R8): Full gate passes; durable documentation and changelog describe defaults and hooks, especially automatic type-default migration. Rose prompt is deliverable prose only.

## Significant decisions

| Decision | Rationale and impact | Review focus |
| --- | --- | --- |
| Content identity only for declared supported arrays | Bounded predictable defaults | Order, nulls, long precision and floating equivalence |
| Separate typed keys and Arez comparators | Identity and change are different contracts | Provider compatibility and precedence |
| Automatic exact-type Arez defaults | Explicit user selection; changes existing mutable inputs | Migration and source/classpath/exclusion tests |
| Specialize known comparators | User correction; direct optimizable equivalent code | Exact matching and semantics at every change site |

## Technical and testing decisions

Use existing generator/nullness patterns and browser harness, explicit BUILD sources, and Yoda equality. Generated helpers may be public in react4j.internal to serve downstream generated code. Keep providers accessible, concrete, static if nested, with accessible no-argument constructors. Retain only tests with behavioral value. Processor tests must compile emitted bodies, and browser assertions must survive optimization. Final gate is tools/check.sh.

## Open questions

None.
