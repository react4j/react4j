# Input Semantics Specification

## Purpose

Define immutable input identity and mutable input equality across view construction, rendering and reactive updates.

## Requirements

1. Constructor `@Input` parameters are immutable: equal synthesized keys reuse the existing view and its original constructor values; a different key creates a new view.
2. Automatic identity for every declared array uses ordered element keys, distinguishing null arrays, empty arrays, null elements, element boundaries and nested array boundaries.
3. Array traversal follows the declared dimensions. Each scalar leaf uses the immutable key strategy selected for its declared component type: string, primitive or boxed primitive, enum, `Keyed`, identifiable Arez component, or dynamic derivation. Dynamic derivation checks `Keyed`, then Arez identity, then string conversion.
4. Runtime arrays supplied through scalar declarations, including `Object` and type variables, retain scalar key behavior. A generic `T[]` traverses one declared dimension and applies the scalar strategy for `T`; `T[][]` traverses two.
5. Floating array contents follow immutable scalar key semantics: NaNs share identity and signed zeros share identity. Long values retain all 64 bits.
6. Array inputs remain caller-owned references. Callers must keep array structure, contents and referenced elements' key-relevant state unchanged while a view can use them.
7. Immutable constructor inputs may select `InputKeyGenerator<T>` through `@Input.keyGenerator`. The provider accepts the input's effective Java type and nullness and returns a non-null string contributing that input's identity. An explicit provider overrides automatic derivation for the entire input, including arrays.
8. Views using automatic array keys or explicit key generators combine input contributions without losing their boundaries or the view's identity. Effective values from explicit calls, omission, defaults and tree context follow the same identity rules.
9. Mutable method inputs select equality in this order: explicit `@Input.equalityComparator`, the effective declared type's own Arez `@DefaultEqualityComparator`, then JavaScript strict equality.
10. Type-default lookup behaves consistently for source and compiled types after generic substitution. It does not search type-variable bounds, supertypes, implemented interfaces, arrays, array component types or type-use annotations.
11. The selected equality policy governs input-driven rendering, Arez invalidation and PRE/POST input-change predicates. Other changed inputs, independent reactive dependencies and explicit scheduling can still require rendering. Multi-input change callbacks run only when all listed inputs compare changed.
12. Equal mutable replacements may become the current input reference while rendering and invalidation are skipped. Equality must treat the values as interchangeable for rendering and derived state; it does not canonicalize or retain the old reference.
13. Providers receive declared Java values, with ordinary boxing for primitives and nullable values where permitted. They must be deterministic and have no observable side effects; instances may be reused and invocation counts are unspecified.
14. Provider classes must be concrete, accessible from generated code, static when nested and constructible through an accessible no-argument constructor without checked exceptions. Key-provider generic compatibility, nullness and invalid input targets produce processor diagnostics.
15. The exact Arez `ObjectsEqualsComparator` and `ObjectsDeepEqualsComparator` implementations preserve their Java equality semantics without comparator allocation or dispatch. Custom implementations retain their own comparison behavior, including null handling. The strict-equality fallback remains distinct from selected Java equality.
