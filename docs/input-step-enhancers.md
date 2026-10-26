---
title: Input Step Enhancers
---

Use `@Input(enhancer=...)` to add presets or conversions to an input's generated builder step.
The enhancer is an interface with one result type parameter and one abstract method matching the
input's generated setter name and value type. Its public default methods return the result parameter
directly and delegate to that setter.

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
builder step and cannot select an enhancer. The built-in `children(Stream)` overload retains its
existing completion behavior; a custom helper must coexist with that generated overload.
