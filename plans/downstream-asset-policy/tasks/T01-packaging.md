# T01 — Policy-free plain packaging

- Status: complete
- Blocked by: None
- Spec coverage: R1, R2, R3, R4; AC1, AC2, AC3

## Delivers

Uncompressed assets from metadata preparation through application assembly and preview, with no offline policy. Remove the planner, Brotli module/patch/tool wiring, attributes, generated lists and obsolete tests. Update example/browser consumers and reproducibility inventory.

## Acceptance criteria

- [x] Plain preparation and packaging validate bytes, destinations, retained/pruned inventory and exact output file sets; formerly reserved list names are ordinary explicit files.
- [x] No compressor or offline planner action, metadata or dependency remains in either mode.
- [x] Preview and example tests consume canonical files; existing source-map/liveness checks remain.

## Validation

Focused bundler and preview tests, actual example builds and Java browser tests; action graph audit in development and optimized builds. Formatter and relevant coverage checks.

## Evidence

Focused packaging/asset/preview suites passed; all three CSS browser suites passed in both fastbuild and opt. Added alias deduplication and empty-asset application cases also passed. Java formatting and buildifier applied. Bundler coverage: 97.27% lines, 90.74% branches; AssetContent 100/100 and ApplicationBundle 100/97.06. Both aquery graphs have one BrowserApplication, 15 PrepareWebAsset and zero compression/planner actions; resolved module graph has no Brotli dependency. `git diff --check` passed.
