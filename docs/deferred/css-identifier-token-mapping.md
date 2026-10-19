# How should CSS source maps identify individual authored name tokens?

## Why it matters

Several occurrences of a [CSS symbol](../glossary/README.md#css-symbol) can appear within one selector or declaration. Mapping each occurrence to its exact authored identifier would improve navigation while debugging rewritten names.

## Why deferred

The [source-map contract](../specs/css-source-maps.md) preserves selector and declaration origins. Exact identifier-token attribution requires additional source tracking through CSS parsing and Java text-block decoding.

## Reconsider when

- CSS debugging requires distinguishing identifier locations within a mapped selector or declaration.
