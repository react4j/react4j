# CSS Source Maps Specification

## Purpose

Preserve source origins and content for CSS bundles and JavaScript values exposing a [CSS symbol](../glossary/README.md#css-symbol) or [Checked asset](../glossary/README.md#checked-asset).

## Requirements

1. Source maps for generated CSS resolve retained selectors and declarations to their authored Java locations after bundling, pruning, name replacement and final asset URL replacement, including changes in line and UTF-16 column positions.
2. CSS source maps include complete authored source content, including shared CSS files.
3. JavaScript source maps preserve origins in generated CSS/asset-binding Java after final URL replacement.
4. JavaScript source maps include complete generated CSS/asset-binding source content.
5. Browser source-map publication is independent of JavaScript optimization and React runtime selection.
6. Withholding generated browser source maps omits those files and their final references from delivered JavaScript/CSS without disabling the internal maps needed for linking and URL finalization.

## Boundaries

[Exact identifier-token attribution](../deferred/css-identifier-token-mapping.md) and [direct JavaScript-to-authored-CSS mapping](../deferred/javascript-authored-css-mapping.md) remain deferred.
