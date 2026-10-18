# CSS Bindings Specification

## Purpose

Define checked Java declarations and imports for each [CSS symbol](../glossary/README.md#css-symbol), including separately compiled interfaces and definitions.

## Requirements

1. Symbol and stability annotations belong to the effective method declaration selected under React4j's ordinary method-collection convention. An untouched inherited method retains its annotations. An override uses its own annotations and defaults; unannotated overrides erase inherited symbol metadata. An explicit override may change symbol kind or name. Sibling declarations use the ordinary selected declaration without an additional CSS annotation-conflict rule.
2. Java signature correctness remains independent of annotation selection: generic substitution, compatible covariant returns, common throws restrictions, concrete and Object implementations, default-method validity and access restrictions still apply. Generated-symbol annotations on an effective concrete or default method are invalid; unannotated concrete methods remain ordinary helpers. Re-abstraction alone does not restore erased annotations.
3. Definition and accessor stability annotations have CLASS retention and apply consistently through source and separately compiled declarations. Only supported annotated definition types and abstract accessors may carry `@CssStable`.
4. `@CssImport` can select one ID using its authored name without `#`, with an optional local alias defaulting to that name. ID and property selections are mutually exclusive. Empty or malformed explicit selections, duplicate ID aliases and ownerless alias cycles are errors. ID aliases occupy a separate namespace from component, animation and property aliases.
5. Component, global and keyframe definitions can consume selected IDs, including through transitive aliases and separately compiled providers. Selectors and same-document fragment references resolve to the canonical symbol. A consuming `@CssId` accessor alone may use the import; an import unused by CSS and accessors is an error.
6. Every ID selector in a global definition must resolve through a local ID accessor or a selected ID import. Unbound selectors are errors. Declaring or importing an ID validates identity without asserting that a matching DOM element exists.
7. Imported ID accessors expose the canonical provider's name and policy. Ordinary external URLs and unsupported reference forms keep their existing validation boundaries.

## Related contracts

[CSS names](css-names.md) governs spelling and stability; [CSS liveness](css-liveness.md) governs retention.
