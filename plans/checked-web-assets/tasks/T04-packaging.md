# T04 — Manifest packaging and compression

- Status: `complete`
- Blocked by: T03
- Spec coverage: R7,R8,R9,R10; AC3,AC4

## Delivers

Integrate generated JS/CSS asset evidence with browser/site packaging. Generate deterministic content-addressed asset output, inventory, reverse/pruned report and checked precache/on-demand lists. Preserve explicit assets, union selected CSS variants and deduplicate compatible bytes. Add pinned per-file Brotli processing with useful-representation filtering and preview HTTP content negotiation. Preserve stable input/output shapes for downstream actions.

## Acceptance criteria

- [x] Delivered behavior and its spec criteria pass focused validation.
- [x] Existing behavior remains covered; new diagnostics and generated artifacts are checked.
- [x] Evidence is recorded and the implementation/task state committed together.

## Validation

Packaging/manifest/path/conflict/deduplication tests, Brotli round-trip/server negotiation tests, actual application trees and variant unions; aquery/incremental evidence and site consumption checks.

## Evidence

- All 18 CSS, real-link asset, HTTP negotiation and site targets pass; pinned native Brotli round-trips and useless-representation exclusion are exercised. Real Drumloop output contains 23 inventoried entries with 21 useful Brotli representations; site assembly copies the complete tree byte-for-byte.
- Per-file image mutation reran only BrotliCompress, PrepareWebAsset and WebBundle; final URL/JS changed and Java/J2CL/Closure stayed cached. Restored/rebuilt source afterward. aquery: 107 compilation/link actions exclude physical asset bytes, Brotli outputs and prepared metadata.
- Focused CSS coverage: processor 98.80/94.49, internal 98.23/92.11, linker 95.74/93.52, bundler 96.43/88.75 (lines/branches). Existing 95/85 policy unchanged. Buildifier, Java format and Ahab default-mode hermeticity passed.
- Canonical per-file metadata drives deterministic content-addressed delivery, complete retained/pruned inventory, validated non-rooting precache selection and on-demand lists. Fixed application roots and public lists are independently compressed; build-only inventory is outside the deployed tree.
