# CSS Bindings Specification

## Purpose

Define checked Java declarations and imports for each [CSS symbol](../glossary/README.md#css-symbol), including separately compiled interfaces and definitions.

## Requirements

1. Symbol and stability annotations belong to the effective method declaration selected under React4j's ordinary method-collection convention. An untouched inherited method retains its annotations. An override uses its own annotations and defaults; unannotated overrides erase inherited symbol metadata. An explicit override may change symbol kind or name. Sibling declarations use the ordinary selected declaration without an additional CSS annotation-conflict rule.
2. CSS definitions may inherit accessors and ordinary helpers from Java interfaces, including generic contracts, compatible covariant returns and default helpers. Inherited accessors obey the same signature requirements as declared accessors and honor inherited implementations and common throws restrictions.
3. Definition and accessor stability annotations have CLASS retention and apply consistently through source and separately compiled declarations. Only supported annotated definition types and abstract accessors may carry `@CssStable`.
4. `@CssImport` can select one ID using its authored name without `#`, with an optional local alias defaulting to that name. ID, property and font selections are mutually exclusive. Empty or malformed explicit selections, duplicate ID aliases and ownerless alias cycles are errors. ID aliases occupy a separate namespace from component, animation and property aliases.
5. Component, global and keyframe definitions can consume selected IDs, including through transitive aliases and separately compiled providers. Selectors and same-document fragment references resolve to the canonical symbol. A consuming `@CssId` accessor alone may use the import; an import unused by CSS and accessors is an error.
6. Every ID selector in a global definition must resolve through a local ID accessor or a selected ID import. Unbound selectors are errors. Declaring or importing an ID validates identity without asserting that a matching DOM element exists.
7. ID fragments in asset URLs retain their authored spelling; URL binding applies only to same-document fragments.
8. Generated-symbol annotations require an effective abstract accessor. An unannotated concrete or default method remains an ordinary helper.
9. Generated accessors return ordinary strings on the JVM and in shipped JavaScript. Compiler-only symbols do not appear in shipped JavaScript.
10. Outer-level `@font-face` declarations own static family names and accept ordinary face descriptors, including multiple weights/styles per family. Malformed faces, duplicate descriptors and reserved owned families are rejected.
11. Selected `@CssImport(font = ...)` bindings use the provider's family identity through aliases and separately compiled definitions. Unused imports and ownerless cycles are errors. Font aliases have their own namespace.
12. Family lists and supported font shorthand bind resolved family occurrences, including runtime fallbacks. Ambiguous dynamic shorthand containing managed families and managed names hidden in custom-property values are rejected rather than silently misbound, including through definitions in other selectors or conditional rules. Variables supplying weight, size or line-height remain supported.

## Related contracts

[CSS names](css-names.md) governs spelling and stability; [CSS liveness](css-liveness.md) governs retention.
