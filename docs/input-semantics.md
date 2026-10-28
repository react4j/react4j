---
title: Input Identity and Equality
---

Choose identity when a changed value should create a new view. Choose equality when a changed value should update the existing view.

Constructor `@Input` parameters contribute to the view's React key. Matching keys keep the existing instance and its original constructor arguments. Method `@Input` declarations receive replacements on an existing instance; their equality policy decides whether that input triggers rendering, Arez invalidation and change callbacks.

## Immutable arrays

Every declared array uses ordered element keys for its default immutable identity. Fresh arrays with equal element keys reuse the view. Changes to length, order or element identity create a new view. Null arrays, empty arrays, null elements and literal `"null"` elements remain distinct.

{@file_content: file=react4j/examples/input_semantics/ImmutableIcons.java}

The declared component type selects the same key rule as a scalar input:

| Component type | Element identity |
| --- | --- |
| String, primitive or boxed primitive | Scalar text |
| Enum | Enum text |
| `Keyed` | Its key |
| Identifiable Arez component | Its Arez ID |
| Other object or type variable | Dynamic: `Keyed`, then Arez ID, then string conversion |

Nested arrays recurse through the declared dimensions, preserving row boundaries and null or empty rows. For example, `String[][]` uses string keys within each row. `T[]` traverses one dimension and treats each `T` as a scalar; when `T` happens to be an array, that runtime value keeps its scalar identity. An input declared as `Object` also keeps scalar behavior when supplied an array.

React4j retains the supplied arrays and their elements. Keep the structure, contents and elements' key-relevant state unchanged while the view can use them. Objects sharing an element key retain the original constructor value even when their other fields differ. Review existing object-array and nested-array inputs when adopting this content-based identity.

## Custom immutable keys

Select a reusable `InputKeyGenerator<T>` implementation when an input needs a different identity rule. This provider normalizes a label's case:

{@file_content: file=react4j/examples/input_semantics/LowerCaseKeys.java}

The view stores the normalized value too, so equal keys correspond to interchangeable constructor values:

{@file_content: file=react4j/examples/input_semantics/NamedPanel.java}

An explicit generator takes precedence over automatic array, scalar, `Keyed` and Arez identity derivation. React4j combines its result with the other immutable inputs and the component identity. A stable ID can deliberately exclude other fields; those fields will not refresh the retained constructor argument.

Providers run before view construction. Use a concrete accessible class with an accessible no-argument constructor; nested providers must be static. The processor checks the effective input type and JSpecify nullness against `T`. Nullable inputs require a provider accepting null. Return a non-null string, keep results deterministic and avoid observable side effects. Instances may be shared and calls repeated.

## Mutable input equality

For mutable arrays, select Arez's deep comparator to skip updates for equal contents without changing component identity:

{@file_content: file=react4j/examples/input_semantics/MutableIcons.java}

Supply a replacement when contents change; comparison cannot recover earlier contents after an array has been mutated in place.

Mutable inputs resolve their policy as follows:

1. The comparator explicitly selected by `@Input(equalityComparator=...)`.
2. `@DefaultEqualityComparator` on the exact effective declared value type, including a type resolved through inherited generic declarations.
3. JavaScript `===` when neither provides a comparator.

The type default is read from source or compiled metadata. Bounds, supertypes, implemented interfaces, arrays and their component types do not supply defaults. Custom comparators implement `arez.EqualityComparator` and receive ordinary Java values, including boxed primitives and permitted nulls. They follow the same construction and purity rules as key generators.

The policy applies before input-driven invalidation as well as rendering, and also controls `@OnInputChange` predicates. Other inputs and independent Arez changes can still render the view. Equal replacements remain the latest props; equality does not keep the previous object or normalize the replacement.

Input comparison happens after the parent has rendered and built the child element. An equality comparator on an upstream Arez observable or memoized producer can suppress propagation earlier. Use that upstream policy where the producer owns the meaning of equality.

## Floating-point behavior and migration

| Policy | NaN compared with NaN | Positive and negative zero |
| --- | --- | --- |
| Automatic immutable scalar/array keys | Same identity | Same identity |
| Mutable strict-equality fallback | Changed | Unchanged |
| Selected `ObjectsEqualsComparator` or `ObjectsDeepEqualsComparator` | Equal | Distinct |

J2CL retains extra precision in `float` arithmetic; comparison and key generation preserve the actual runtime values. Long-array keys preserve all 64 bits. Built-in Arez comparators generate direct equivalent calls, while custom comparators retain their own behavior.

Existing mutable inputs now honor their declared type's Arez default comparator. Review types carrying `@DefaultEqualityComparator`, especially where a view previously reacted to replacement identity. An explicit per-input comparator overrides that type default. The [input contract](https://github.com/react4j/react4j/blob/master/docs/specs/input-semantics.md) records these identity and equality guarantees.
