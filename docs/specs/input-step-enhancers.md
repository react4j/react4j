# Input Step Enhancers Specification

## Purpose

Define how component authors add fluent presets and conversions to input builder steps. The
[author guide](../input-step-enhancers.md) provides checked examples.

## Requirements

1. An input declared on a method or constructor parameter may select one enhancer interface using `@Input.enhancer`.
2. The enhancer is accessible from the generated builder's package and has one unbounded result type parameter.
3. The enhancer has one effective abstract setter matching the input's generated name and Java value type, without method type parameters or declared exceptions. Its parameter nullness permits only values accepted by the input.
4. Every public effective default helper returns the result parameter directly. Inherited helpers, method type parameters, varargs and declared checked exceptions are supported for source and precompiled enhancers.
5. A helper's result has the same continuation as the input setter: advance to the next step, remain on the current optional-input step, or return `ReactNode` when construction finishes.
6. Enhancers preserve existing required input calls, input ordering, default values and canonical setter behavior.
7. First-step helpers have static shortcuts preserving their parameter and result types, nullness, method type parameters, varargs and checked exceptions. Later-step helpers do not gain static shortcuts.
8. Static shortcuts preserve user-authored helper effects and do not assume purity.
9. Invalid interfaces, inaccessible helper signature types and incompatible setter contracts produce processor diagnostics attached to the input.
10. Incompatible helper signatures, erasure clashes with generated members, and inconsistent generic parent instantiations across a builder produce processor diagnostics. Compatible shared defaults and overriding inherited methods remain supported.
11. An enhancer cannot be selected for a tree-context input, which has no builder step.
