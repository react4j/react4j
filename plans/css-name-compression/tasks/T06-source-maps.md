# T06 — Source content and map fidelity

- Status: `complete`
- Blocked by: T05
- Spec coverage: R8; AC6.

## Delivers

Debuggers resolve compressed CSS to authored Java and optimized JS style values to generated binding Java with complete source content, including nested generated implementation names.

## Acceptance criteria

- [x] Generated binding companion maps are enriched before normal Closure composition/reset; follow actual source entries, support nested facades, and affect only catalogue-matched binding units.
- [x] Actual optimized maps retain generated Java origins and full generated Java `sourcesContent`; development maps retain valid contents too.
- [x] Replacement projection preserves CSS authored selector/declaration anchors and full source content after removal and variable-length edits, including unmapped spans.
- [x] Concrete map queries before/inside/after replacements cover tabs, UTF-16, CRLF, multiline/nesting, imports, property/animation/fragment edits, and concatenated bundle sections.
- [x] No accidental adoption of deferred exact-token or JS-direct-to-authored-CSS semantics; errors/missing data fail meaningfully where required.

## Validation

- SourceMap unit tests with real position decoding and original source content assertions.
- Linker companion-map lifecycle tests plus real J2CL/Closure map consumer queries using output positions, not JSON-only validation.
- Bundle map queries across pruning/replacement/layer boundaries in actual example artifacts.

## Evidence

- Passed internal (30 cases), linker (14 cases) and real J2CL integration (10 cases), plus all three SaveButton browser naming modes in development and optimized builds. Concrete emitted JS literal queries resolve to full generated Java bytes for top-level interface/facade and nested bindings; CSS queries preserve authored selector/declaration anchors.
- Direct companion tests cover sourceRoot, a nested implementation whose declared Java name differs, missing/null/partial/present source content, UTF-8/CRLF, malformed maps, missing files, invalid content, non-Java and out-of-tree references. Only matched catalogue implementations are loaded.
- Found and fixed a real-worker path mismatch: preserve relative companion map names for Bazel's source-path remapping; absolute names passed isolated tests but lost real-link contents. The integration compares final embedded content with actual generated tree bytes.
- Closure BUNDLE emits concatenated source and no combined external JS map. Its readable source and companion origins remain intact; companion enrichment is tested independently. No eval/CSP change or extra JS output pass introduced.
- Added shortening/lengthening + pruning queries before/inside/after tokens across tabs, UTF-16, CRLF, unmapped spans and signed anchors; existing parsed-kind/import/fragment/animation and bundle-section tests remain green.
- Java formatter/buildifier applied; domain links and git diff checks passed. Exact-token and direct JS-to-authored-CSS attribution remain deferred.
