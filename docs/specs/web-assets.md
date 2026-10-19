# Checked Web Assets Specification

## Purpose

Bind Java and CSS file references to [Checked asset](../glossary/README.md#checked-asset) declarations and deliver the files required by an application with traceable URLs for downstream deployment tooling.

## Requirements

1. Stateless abstract `@Assets` classes expose package-relative files through parameterless abstract `String` methods annotated `@Asset`. Public/package classes, static nested classes, effective interface methods and concrete/static helpers are supported. Fields, explicit constructors, initializers and non-static inner classes are rejected.
2. Logical paths use canonical relative `/` segments. Absolute paths, traversal, empty segments, queries, fragments, escapes and URL delimiters are rejected. Every Java or CSS declaration must resolve against explicit build inputs, including references later removed as dead; missing and conflicting declarations fail deterministically.
3. Only surviving Java values, retained CSS references and explicit application assets retain files. Imports alone do not root assets. Development builds conservatively retain available asset bindings. Liveness evidence must match the actual [Complete link](../glossary/README.md#complete-link) and checked declarations.
4. `@CssImport` of an asset definition binds `asset:alias.method` references in parsed CSS URLs, including `image-set()` strings. Query/fragment suffixes retain their meaning. HTTP(S), protocol-relative, data and blob URLs remain external; same-document fragments remain document references. Other unresolved local URLs fail validation.
5. Deployment URLs derive from processed, uncompressed contents and media type. Identical compatible contents deduplicate; conflicting logical inputs or output destinations fail. Retained files from every selected [CSS variant](../glossary/README.md#css-variant) contribute to one application inventory.
6. Java file URLs resolve from the external classic application script's location; CSS file URLs resolve from the deployed stylesheet's location. Both work at application subpaths and nested document routes without depending on the current document route.
7. Changing only file bytes preserves Java/J2CL compilation and Closure optimization caches. Per-file preparation is independently reusable. Identical inputs and settings produce identical delivered files and inventories.
8. A complete build-only [Asset inventory](../glossary/README.md#asset-inventory) records retained and pruned candidates, source/accessor identities, canonical URLs, uncompressed digests/sizes, media types and retention reasons. It remains available to downstream deployment actions without entering the browser delivery tree.
9. Application output contains exactly the uncompressed retained files and explicit application files described by the inventory. Multiple logical assets may share one delivered file.
10. Downstream applications own compression, deployment cache policy and offline selection. React4j does not generate compressed sidecars or precache/on-demand lists, or reserve filenames for those policies.
11. Annotation processing, CSS processing and URL finalization run on the JVM; the build does not require Node.js.

## Architectural basis

[Finalize asset URLs after linking](../adr/0003-finalize-asset-urls-after-linking.md).

## Related contracts

[CSS source maps](css-source-maps.md) governs preserved origins after final URL replacement; [CSS liveness](css-liveness.md) governs retained CSS and font dependencies.
