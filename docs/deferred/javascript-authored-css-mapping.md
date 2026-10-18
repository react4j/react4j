# How should JavaScript style values map directly to authored CSS?

## Why it matters

A generated style accessor can be inlined into JavaScript. Navigating its returned [CSS symbol](../glossary/README.md#css-symbol) directly to the authored CSS would avoid first visiting a generated Java binding.

## Why deferred

Name compression preserves JavaScript mappings to generated Java bindings and CSS mappings to authored Java. Direct navigation from JavaScript values into CSS text requires a separate origin policy, especially for accessor values containing several symbols or imported aliases.

## Reconsider when

- Debugging generated style values requires direct navigation from JavaScript into authored CSS rather than generated bindings and the reverse-name file.

## Context

The [symbolic compilation boundary](../adr/0001-css-symbol-liveness.md) preserves accessor origins through optimization. Mapping a value to its authored CSS occurrences is a distinct responsibility from preserving that JavaScript source location.
