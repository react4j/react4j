# T01 — Policy-free plain packaging

- Status: pending
- Blocked by: None
- Spec coverage: R1, R2, R3, R4; AC1, AC2, AC3

## Delivers

Uncompressed assets from metadata preparation through application assembly and preview, with no offline policy. Remove the planner, Brotli module/patch/tool wiring, attributes, generated lists and obsolete tests. Update example/browser consumers and reproducibility inventory.

## Acceptance criteria

- [ ] Plain preparation and packaging validate bytes, destinations, retained/pruned inventory and exact output file sets; formerly reserved list names are ordinary explicit files.
- [ ] No compressor or offline planner action, metadata or dependency remains in either mode.
- [ ] Preview and example tests consume canonical files; existing source-map/liveness checks remain.

## Validation

Focused bundler and preview tests, actual example builds and Java browser tests; action graph audit in development and optimized builds. Formatter and relevant coverage checks.

## Evidence

Pending.
