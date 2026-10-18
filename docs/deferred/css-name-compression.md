# How should generated CSS names be compressed?

## Why it matters

JavaScript and CSS must agree on compressed class, ID, animation and custom-property names, while source maps retain authored origins.

## Why deferred

The accepted liveness work keeps readable names. A compression policy and its distribution requirements need their own design and validation.

## Reconsider when

- Name compression is selected for implementation after the liveness boundary is available.

## Context

The [symbolic compilation boundary](../adr/0001-css-symbol-liveness.md) can resolve each surviving [CSS symbol](../glossary/README.md#css-symbol) through a shared name table. Choosing that boundary does not select a compression algorithm or expose a new public option.
