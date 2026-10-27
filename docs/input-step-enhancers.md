---
title: Input Step Enhancers
---

Use `@Input(enhancer=...)` to add presets or conversions to an input's generated builder step.
The enhancer is an interface with one result type parameter and one abstract method matching the
input's generated setter name and builder-facing value type. Its public default methods return the result
parameter directly and delegate to that setter.

{@file_content: file=react4j/examples/input_enhancers/ColorEnhancer.java}

Select it on a method input or constructor parameter:

{@file_content: file=react4j/examples/input_enhancers/Swatch.java}

Helpers on the first step also have static shortcuts:

{@file_content: file=react4j/examples/input_enhancers/Presets.java}

The result parameter describes what setting the input returns:

| Setter behavior | Helper result |
| --- | --- |
| More required inputs remain | The next builder step |
| Several optional inputs share the step | The same builder step |
| Setting this input finishes construction | `ReactNode` |

A terminal helper returns the finished node; it needs no additional `build()` call. Helpers preserve
the existing ordering, required calls, defaults and canonical setters. An optional input may itself
be terminal, so use the generated return type to determine whether construction has finished.

Enhancers may inherit their setter and default methods, including from compiled libraries. Helpers
can declare their own method type parameters, varargs and checked exceptions; static shortcuts retain
those contracts. Keep the interface accessible from the generated builder's package and follow the
[project's JSpecify configuration](project_setup.md). Its setter must accept only values that the
input can accept; a generated nullable setter may implement an enhancer setter that accepts non-null
values. The abstract setter has no method type parameters or declared exceptions. The interface's result
parameter has no Java bound beyond `Object`.

Each generated builder uses one implementation for all its steps. The processor rejects incompatible
helper signatures, erased overloads and generic parent interfaces across those steps. Use distinct
names such as `compactLayout()` and `untitled()` when two inputs would otherwise introduce different
`preset()` methods. Compatible shared inherited defaults and ordinary overloads are allowed.

The interface has only its one abstract input setter; additional abstract capabilities are rejected.
Private and static interface utilities remain implementation details. Tree-context inputs have no
builder step and cannot select an enhancer.

## Composing ready builders

A step that exposes `build()` also implements `Buildable`. Both `Buildable` and `ReactNode` extend
`Renderable`, which composition APIs accept. Supply a ready builder directly to children, DOM helpers,
context providers, roots or portals; the receiving API calls `build()` immediately. Each occurrence
builds separately, and exceptions propagate. There is no implicit caching or freezing.

For example, supplying the required message leaves this builder ready, with an optional class name:

{@file_content: file=react4j/examples/input_enhancers/Notice.java}

Declare an `@Render` method's return type as `Renderable` when it may return a ready builder:

{@file_content: file=react4j/examples/input_enhancers/NoticeHost.java}

Existing `ReactNode` return types and explicit `build()` calls remain valid. A Java variable, field or
helper declared as `ReactNode` still requires a materialized node; use `Renderable` when it should
also hold a ready builder. Steps with required calls remaining cannot be supplied as renderables.
Context consumer callbacks and generated render bridges build their returned value on each invocation.

## Children inputs

An input whose effective name is `children` automatically selects `react4j.ChildrenEnhancer`.
It supplies `children(Stream<? extends Renderable>)`, which consumes the stream once in encounter
order and delegates to the array setter. No enhancer annotation is needed:

{@file_content: file=react4j/examples/input_enhancers/ChildrenPanel.java}

Both `child` and `children` always finish construction and return `ReactNode`, including when
other optional inputs share their step. Set optional properties first, then supply the children.
Optional children may still be omitted through `build()`. First-step stream helpers also have
static shortcuts:

{@file_content: file=react4j/examples/input_enhancers/ChildrenPresets.java}

An explicit enhancer replaces the automatic selection. Extend `ChildrenEnhancer` to keep its
stream helper while adding presets:

{@file_content: file=react4j/examples/input_enhancers/MenuChildren.java}

{@file_content: file=react4j/examples/input_enhancers/MenuPanel.java}

A standalone custom enhancer supplies only its own helpers, so it can also provide its own stream
overload. Omitting `enhancer` or specifying `void.class` selects the standard default for `children`
and no enhancer for other inputs. Tree-context inputs do not receive the default.

Generated setters for `ReactNode` and `ReactNode[]` inputs now accept `Renderable` and `Renderable[]`,
with the same nullness. The view's input declarations can keep their node types: builders are
materialized before storage. Update custom node-input enhancer setters to those widened types.
Overrides of the standard children stream helper must use `Stream<? extends Renderable>` as well.
Ordinary data inputs and untyped property maps are not traversed for builders.

When migrating existing chains, move setters after `child` or `children` before that call and
remove the trailing `build()`:

```java
// Before
BreadcrumbBuilder.label("Overview").child(menu).leaf(false).build();
// After
BreadcrumbBuilder.label("Overview").leaf(false).child(menu);
```
