# T03 — CSS assets and owned fonts

- Status: `complete`
- Blocked by: T02
- Spec coverage: R3,R4,R5,R6; AC1,AC2,AC3

## Delivers

Resolve typed asset imports in component/global/keyframes declarations, structurally validate URL-bearing values and preserve suffix/fragment behavior. Add scoped local @font-face families, checked selected-family imports through @CssImport(font=...), and deliberate font-family/font handling, retaining faces from surviving CSS and conservatively for dynamic values. Export retained CSS asset evidence per CSS variant; keep all existing pruning/name/source-map contracts.

## Acceptance criteria

- [x] Delivered behavior and its spec criteria pass focused validation.
- [x] Existing behavior remains covered; new diagnostics and generated artifacts are checked.
- [x] Evidence is recorded and the implementation/task state committed together.

## Validation

Processor formatted/unformatted and cross-library tests; parsed-model URL/font unit tests; bundler/linker integration for dead rules/faces, weights/styles, cross-library shared-family identities and dead imports, external/data/fragment URLs, image-set strings, map preservation and independent CSS variants.

## Evidence

- `bazel test //css:tests --test_output=summary`: all 15 targets passed, including both processor artifacts, paired formatting modes, source/binary imports, existing CSS integration and real ADVANCED/BUNDLE asset fixtures. Shaded processor suite: 587 cases.
- `tools/update_goldens.sh css`: 80 generated outputs updated and verified, including complete new asset/font output sets; temporary logs removed. Java format and buildifier checks passed.
- Parsed-model tests cover URL functions/image-set strings, external/data/blob/fragment values, suffixes and unsafe local paths; static/dynamic font grammar, faces/descriptors, aliases/cycles and case-insensitive compression. Dead modifier font dependencies and files are pruned, both live weights survive, and independent CSS variants union their retained files with preserved Java source-map origins.
- Both web_bundle fixtures built with stylesheet-relative finalized URLs. Full browser/package requests and root coverage remain assigned to T04/T05.
- Standards check during packaging corrected family matching to Unicode Default Caseless Matching (full C/F mappings) with no normalization or quoted-whitespace folding. Pinned Unicode data is packaged into both processor artifacts; Unicode, escape and whitespace tests pass in shaded and implementation suites.
