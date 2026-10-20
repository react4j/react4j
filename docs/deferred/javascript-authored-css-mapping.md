# How should JavaScript style values map directly to authored CSS?

## Why it matters

A generated style accessor can be inlined into JavaScript. Navigating its returned [CSS symbol](../glossary/README.md#css-symbol) directly to the authored CSS would avoid first visiting a generated Java binding.

## Why deferred

The [source-map contract](../specs/css-source-maps.md) preserves generated Java origins for JavaScript and authored Java origins for CSS. Direct navigation from JavaScript values into CSS text requires a separate origin policy, especially for accessor values containing several symbols or imported aliases.

## Reconsider when

- Debugging generated style values requires direct navigation from JavaScript into authored CSS rather than generated bindings and the reverse-name file.
