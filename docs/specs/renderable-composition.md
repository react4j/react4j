# Renderable Composition Specification

## Purpose

Define how native nodes and ready builders compose through typed React4j APIs. The
[author guide](../input-step-enhancers.md#composing-ready-builders) provides checked examples.

## Requirements

1. Public `ReactNode` and public functional `Buildable` extend `Renderable`. `Buildable.build()` returns a non-null `ReactNode`.
2. Native node values retain their representation and identity when consumed as renderables. Values implementing `Buildable` are materialized through their `build()` method.
3. A generated step interface implements `Buildable` exactly when it exposes `build()`. Required-call ordering and terminal child behavior follow the [input-step contract](input-step-enhancers.md).
4. Typed node composition accepts renderables in core factories, fragment/strict/suspense children and fallback, DOM factories and typed props children, roots, legacy rendering, portals, context providers and consumer results, generated node inputs and `@Render` results.
5. Each builder occurrence is built once when consumed, without caching or freezing the builder. A render callback's result is consumed on each invocation, before post-render callbacks. Build exceptions propagate unchanged.
6. Each consuming position preserves its existing nullness contract, independently of the non-null build result.
7. Array conversion preserves order and caller-owned contents. Arrays without builders retain their identity; arrays containing builders are materialized into a separate array.
8. Children streams are traversed once in encounter order.
9. Generated setters for `ReactNode`/`Renderable` inputs and their arrays accept the corresponding renderable types while preserving declared nullness. Values from setters, defaults and tree context are materialized before storage; view inputs retain their authored types.
10. An `@Render` method may return `Renderable` or a subtype. Its generated bridge returns a materialized nullable node, following the authored method's nullness contract.
11. Nonempty explicit DOM children override typed props children. Empty or null explicit child arrays retain props children. Only the effective children are materialized; caller-owned props remain unchanged.
12. Opaque Object and property-map payloads are not recursively traversed for builders.

## Architectural basis

[Preserve native nodes through a renderable union](../adr/0008-preserve-native-renderables.md).
