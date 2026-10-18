# How should CSS source maps identify individual authored name tokens?

## Why it matters

Several occurrences of a [CSS symbol](../glossary/README.md#css-symbol) can appear within one selector or declaration. Mapping each occurrence to its exact authored identifier would improve navigation while debugging rewritten names.

## Why deferred

Name compression preserves the existing selector/declaration mapping boundaries. Exact identifier-token attribution requires additional source tracking through CSS parsing and Java text-block decoding, and is not needed to preserve those guarantees.

## Reconsider when

- CSS debugging requires distinguishing identifier locations within a mapped selector or declaration.

## Context

The parser exposes individual locations for some selector and expression members, while property names, keyframe names and URL fragments need finer tracking. The reverse-name file provides symbol lookup independently of positional map precision.
