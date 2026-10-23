# Utility CSS Specification

## Purpose

Consume a finite [Utility CSS library](../glossary/README.md#utility-css-library) through the existing checked Java style, naming and delivery contracts.

## Requirements

1. A utility library consumes already compiled, flattened CSS. It does not interpret utility-generation directives or require a generator during application compilation.
2. A utility library or checked global definition supplies exactly one literal Java text block or one explicitly declared, package-relative CSS file. Missing, conflicting and noncanonical source declarations fail during compilation, including unused declarations.
3. The managed class inventory is inferred from the complete selector tree, including referenced markers without standalone rules. Explicit external classes are excluded from managed ownership.
4. Ordinary declarations in a utility library require a managed class guard. Supporting definitions and custom-property initialization may be global; ordinary unguarded element defaults belong in separate global inputs.
5. Selecting a utility does not implicitly activate page-wide resets or defaults. Applications select their global definitions explicitly.
6. Ordinary component declarations take precedence over utility declarations. A library's authored cascade order is preserved; independent libraries do not merge internal layer identities.
7. Explicit global inputs permit standard important declarations. Component and utility definitions reject important declarations.
8. Supplied flat CSS supports escaped class names, selector lists and relational functions, media and supports conditions, layers, property registrations, keyframes, font faces, container queries including named containers, and starting styles. Valid empty variable fallbacks preserve their meaning.
9. Conditions and supporting definitions participate in the same checked name resolution, dependency discovery and source mapping as ordinary style rules. Unrecognized or unsafe unsupported syntax produces a diagnostic rather than silently losing rules.
10. Finite static variable definitions and aliases may carry managed animation and font names. Resolution retains their dependencies and rewrites their name occurrences without inlining browser variable evaluation. Analyze all potentially applicable definitions rather than predicting the browser cascade. Preserve literal value types. Ambiguous rewrites that would alter another semantic use, including visible text or a style-query comparison, are rejected.
11. Declared source files and compiled library catalogues remain available across separately compiled dependencies. Consumers need no duplicate handwritten class export inventory.

## Architectural basis

[Consume finite utility libraries](../adr/0004-consume-finite-utility-libraries.md).

## Related contracts

[CSS bindings](css-bindings.md) governs Java declarations; [CSS names](css-names.md) governs ownership; [CSS liveness](css-liveness.md) governs retention; [CSS source maps](css-source-maps.md) and [checked assets](web-assets.md) govern delivered origins and files.
