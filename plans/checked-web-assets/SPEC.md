# Checked web assets

## Source

- Accepted scope: the immediately preceding component/assets design in this chat, accepted in full by the user.
- Execution authority: “accept all and start implementing. Use structured-delivery-workflow if useful”. No human plan checkpoint requested. Earlier user authority permits updating the existing branch PR.
- Repository evidence: css/defs.bzl collects J2CL output trees and runs a catalogue and custom Closure linker; StyleCompiler tracks accessor identities through optimization; css/bundler prunes CSS and preserves authored mappings; tools/browser/java.bzl compiles checked Java/J2CL separately; tools/browser/rules.bzl currently copies explicitly mapped assets; tools/site consumes BrowserApplicationInfo. Rose independently demonstrates pinned per-file Brotli actions and manifest-based deployment names.

## Problem

Java components and CSS cannot declare checked file dependencies. Applications manually synchronize paths, packaging, compression and offline inventories, or embed image contents in JavaScript to avoid that coordination.

## Required outcome

Authors declare package-relative assets once in Java and explicitly declare source files in Bazel. Linked Java and retained CSS determine delivery. Application packaging generates content-addressed files, a complete inventory and a precache list, with automatic source-map-preserving URL finalization and no Node tooling.

## Scope

- In scope: @Assets/@Asset declarations, checked CSS asset imports, owned @font-face declarations, Java/CSS asset liveness, final URL substitution, Bazel propagation and independently cached processing, manifest-driven application/site packaging, useful Brotli representations, precache policy, real examples, processor diagnostics, tests and downstream documentation/distribution.
- Out of scope: changing Rose, automatic raster transcoding/font subsetting, a new service-worker lifecycle framework, runtime URL lookup tables, arbitrary dynamic filesystem discovery, inlining/sprites and a generic SVG optimizer. Existing preprocessing tools may supply generated files.

## Constraints

- JVM annotation processing, CSS processing and finalization; no Node dependency. Brotli is a pinned native Bazel tool.
- Explicit Java/Closure source lists and package-local libraries; Yoda equality. Preserve etc/secrets and unrelated edits.
- Only authored base classes reference generated bindings. Generated bindings are package-access, privately constructed lazy singletons. Preserve formatting and all existing definition/interface validation rules.
- Preserve one complete JavaScript link shared by independent CSS variants and complete source content in source maps. Do not change existing CSS naming or retention semantics.
- Preserve normal hooks, existing coverage thresholds (95% lines / 85% branches for CSS roots), and full repository checks.

## Requirements

- R1: Package-relative file declarations expose statically checked String accessors, including nested/public/interface cases, without embedding file bytes in generated Java. Invalid paths and declaration shapes are diagnosed.
- R2: Explicit Bazel asset catalogues propagate through Java/J2CL dependency graphs. All declared Java/CSS references are checked even when later dead; missing and conflicting inputs fail deterministically. Canonical paths cannot escape their logical root.
- R3: Only surviving Java asset values, retained CSS references, and explicit application roots retain assets. Imports alone do not retain every asset. Non-optimizing builds conservatively retain available bindings. Liveness evidence must match the actual intermediate link and catalogue.
- R4: CSS can import assets through @CssImport and use url("asset:alias.method"). Supported URL-bearing CSS syntax must be handled structurally (including image-set string URLs), preserve suffixes and fragments correctly, and distinguish managed files, external/data URLs and same-document IDs. Unsafe/unresolved local file references fail rather than silently ship broken URLs.
- R5: Outer-level @font-face declarations have owned, scoped family names and ordinary browser descriptors. Retained family usage retains needed faces/files; unused owned faces are pruned; dynamic/ambiguous usage is conservative. Multiple weights/styles and fallback families work, with diagnostics for malformed definitions and unsupported dynamic ownership. A checked selected-family import, @CssImport(value = Fonts.class, font = "righteous", name = "heading"), binds font-family: heading to its provider identity across separately compiled definitions; font/id/property selections are mutually exclusive. Provider faces are retained only by surviving usage; imports are checked even when dead.
- R6: Final deployment URLs derive from processed, uncompressed bytes and media type; identical compatible assets deduplicate. URL replacements preserve JavaScript/CSS source-map anchors and source contents. Deployment subpaths and nested document routes resolve correctly.
- R7: Asset byte changes do not invalidate Java/J2CL compilation or expensive Closure optimization. Per-file preparation/compression actions have stable declared outputs. Content-dependent filenames are chosen during execution into a declared output tree/archive, never by reading generated manifests in Bazel analysis.
- R8: Application packaging and site assembly consume generated outputs automatically. The declared inventory identifies every retained file, source/accessor, URL, digest, size, representations and retention reason; debugging evidence includes pruned candidates.
- R9: Useful Brotli representations are independently cacheable, excluded when not smaller, and described for canonical URL content negotiation. Preview can exercise representation selection with correct MIME, Content-Encoding and Vary headers.
- R10: Application-owned validated precache policy selects among retained logical paths without rooting dead assets. Generated precache/on-demand lists feed an existing worker; packaging itself does not fetch on-demand files. Explicit non-code assets such as favicons remain supported.
- R11: Existing examples demonstrate Java SVG/PNG use, CSS image use, owned font loading, dead assets, preprocessing targets and automatic packaging. Permanent docs, public Bazel consumption and published annotation/processor artifacts expose the feature without unrelated README/process narration.

## Acceptance criteria

- AC1 (R1,R2): Processor tests/goldens cover accepted shapes, nested naming, helpers/interfaces, malformed declarations/paths, cross-library asset imports and conflicting annotations. Bazel tests reject missing/duplicate catalogue entries including dead references.
- AC2 (R3,R4,R5): Optimization integration tests remove dead getters/rules/faces/files, retain shared/live/dynamic dependencies, and validate CSS parsed models including URL variants and font grammars.
- AC3 (R6,R7): A deterministic incremental-build experiment changes image bytes and proves Java/J2CL/Closure optimization actions do not rerun; final URLs and maps change correctly. Tests cover deduplication, collision/conflict errors, path safety, subpaths and source contents/anchors.
- AC4 (R8,R9,R10): Real packaged browser output and server tests verify SVG, PNG and font requests, Brotli negotiation/round-trip, manifest accuracy, precache/on-demand separation, explicit roots and CSS variant union. Static checks prove all filesystem inputs are declared.
- AC5 (R11): Migrate Drumloop font and all four consumers, convert local SVG references in all three TodoMVC variants, and extend the CSS example; development/optimized browser tests and disposable external Bazel consumer pass. Full tools/check.sh passes, including coverage, formatting, site and reproducibility. No temporary scaffolding remains at closeout.

## Significant decisions

| ID | Decision | Rationale | Impact | User verification |
| --- | --- | --- | --- | --- |
| D1 | Add Assets and Asset to react4j.css and the existing processor/plugin, with asset-specific metadata | Reuse existing lazy generation, validation, publication and identity-based linker without conflating file URLs and CSS names | One existing processor dependency for clients | Review Java and CSS example ergonomics |
| D2 | Finalize direct URL literals after Closure optimization | Preserve expensive action caching without a runtime URL table | Final app.js changes when a referenced asset URL changes | Confirm this accepted bandwidth/cache tradeoff remains visible |
| D3 | Cache per-asset processing/compression before delivery filtering | Bazel analysis cannot branch on generated liveness; independent actions maximize reuse | Dead candidates may still be prepared on first build, but never delivered | Inspect incremental-build evidence |
| D4 | Keep fetching/offline policy with application packaging | Reusable components cannot choose an application's offline policy | Checked logical paths in the application policy; dead matches do not retain files | Inspect generated lists and default on-demand behavior |
| D5 | Treat owned fonts like other checked CSS dependencies | Unconditional global faces would defeat font pruning | Local family names are scoped; ambiguous usage retains conservatively | Review font examples and diagnostics |

## Technical decisions

- Keep declaration identities and catalogue metadata independent of asset contents. Late-bound URL references survive Closure as tracked symbols; the finalizer performs structural/token-aware replacements with composed maps, never unrestricted text replacement.
- Extend the existing CSS resource-preservation path to asset metadata. The asset provider/collection aspect traverses the same declared graph; per-file metadata and content outputs are inputs only to finalization/packaging.
- Produce an application output tree and declared build inventory. Keep public precache data small and build-only reverse mappings out of browser delivery. Application base resolution must work at non-root mounts without depending on the current document route.
- Preserve input formats and explicit preprocessing outputs; hash after preprocessing, compress losslessly afterward. Use deterministic byte ordering, media-type identity and robust full-content digests.
- Shared fonts use a selected font attribute on the existing repeatable CssImport, parallel to selected IDs/properties; this current need is demonstrated by Drumloop's four consumers. Imported aliases keep their provider identity and naming policy.
- Owned font grammar: family names are static; support common font descriptors and multiple faces per family. Parse font-family/font deliberately and retain conservatively where runtime values prevent proof. Preserve external/data URLs and same-document fragments; explicitly validate all managed local references.

## Testing decisions

- Reuse formatted/unformatted processor harness, strict generated compilation and separately compiled dependencies. Add focused unit tests for asset paths/metadata, URL parsing, font parsing, finalization/maps and manifests; verify both standalone and implementation processor jars.
- Use actual J2CL/Closure and real packaged browser requests in development and ADVANCED, including removed branches and CSS variants. Use a temporary checkout/file copy for mutation/incremental experiments, restoring inputs reliably.
- Start with narrow checks for each slice, run required task gates, then full tools/check.sh. Keep current coverage thresholds and add new code to the appropriate enforced CSS roots.

## Open questions

None.
