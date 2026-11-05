# CSS nesting specification

## Purpose

Define how authored declarations inside groups apply to enclosing selectors in shared and checked stylesheets.

## Requirements

1. Shared stylesheets and checked global definitions accept direct declarations inside nested `@media`, `@supports`, `@container`, `@layer` and `@starting-style` groups. Component definitions accept them inside nested `@media` and `@supports` groups, including inside groups nested under the implicit component root.
2. A direct declaration matches the nearest enclosing style rule's elements and pseudo-elements with the same specificity for each selector branch. Successive groups inherit the same style rule until a nested selector establishes a new one. Declarations retain their position relative to other declarations and child rules.
3. A direct declaration in a group without an enclosing style rule fails with an authored source location. Each input's selector, rule placement and declaration checks also apply inside nested groups.
4. Nested declarations participate in [CSS name resolution](css-names.md), [retention](css-liveness.md), [asset processing](web-assets.md) and [source mapping](css-source-maps.md).
