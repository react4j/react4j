# Input Semantics Specification

## Purpose

Define immutable input identity and mutable input equality across view construction, rendering and reactive updates.

## Requirements

1. Constructor `@Input` parameters are immutable: equal synthesized keys reuse the existing view and its original constructor values; a different key creates a new view.
2. Automatic identity for inputs declared as `String[]` or any primitive array uses ordered scalar contents, distinguishing null arrays, empty arrays, null elements and element boundaries. Floating contents follow immutable scalar key semantics: NaNs share identity and signed zeros share identity. Runtime values supplied through other declared types do not select this automatic content rule.
3. Array inputs remain caller-owned references. Callers must keep immutable array contents unchanged while a view can use them.
4. Immutable constructor inputs may select `InputKeyGenerator<T>` through `@Input.keyGenerator`. The provider accepts the input's effective Java type and nullness and returns a non-null string contributing that input's identity. An explicit provider overrides automatic derivation.
5. Views using automatic array content keys or explicit key generators combine input contributions without losing their boundaries or the view's identity. Effective values from explicit calls, omission, defaults and tree context follow the same identity rules.
6. Mutable method inputs select equality in this order: explicit `@Input.equalityComparator`, the effective declared type's own Arez `@DefaultEqualityComparator`, then JavaScript strict equality.
7. Type-default lookup behaves consistently for source and compiled types after generic substitution. It does not search type-variable bounds, supertypes, implemented interfaces, arrays, array component types or type-use annotations.
8. The selected equality policy governs input-driven rendering, Arez invalidation and PRE/POST input-change predicates. Other changed inputs, independent reactive dependencies and explicit scheduling can still require rendering. Multi-input change callbacks run only when all listed inputs compare changed.
9. Equal mutable replacements may become the current input reference while rendering and invalidation are skipped. Equality must treat the values as interchangeable for rendering and derived state; it does not canonicalize or retain the old reference.
10. Providers receive declared Java values, with ordinary boxing for primitives and nullable values where permitted. They must be deterministic and have no observable side effects; instances may be reused and invocation counts are unspecified.
11. Provider classes must be concrete, accessible from generated code, static when nested and constructible through an accessible no-argument constructor without checked exceptions. Key-provider generic compatibility, nullness and invalid input targets produce processor diagnostics.
12. The exact Arez `ObjectsEqualsComparator` and `ObjectsDeepEqualsComparator` implementations preserve their Java equality semantics without comparator allocation or dispatch. Custom implementations retain their own comparison behavior, including null handling. The strict-equality fallback remains distinct from selected Java equality.
