# CSS Source Maps Specification

## Purpose

Preserve source origins and content for CSS bundles and JavaScript values exposing a [CSS symbol](../glossary/README.md#css-symbol).

## Requirements

1. Source maps for generated CSS resolve retained selectors and declarations to their authored Java locations after bundling, pruning and name replacement, including changes in line and UTF-16 column positions.
2. CSS source maps include complete authored source content, including shared CSS files.
3. JavaScript source maps preserve origins in generated CSS-binding Java.
4. JavaScript source maps include complete generated CSS-binding source content.

## Boundaries

[Exact identifier-token attribution](../deferred/css-identifier-token-mapping.md) and [direct JavaScript-to-authored-CSS mapping](../deferred/javascript-authored-css-mapping.md) remain deferred.
