# T04 — Manifest packaging and compression

- Status: `pending`
- Blocked by: T03
- Spec coverage: R7,R8,R9,R10; AC3,AC4

## Delivers

Integrate generated JS/CSS asset evidence with browser/site packaging. Generate deterministic content-addressed asset output, inventory, reverse/pruned report and checked precache/on-demand lists. Preserve explicit assets, union selected CSS variants and deduplicate compatible bytes. Add pinned per-file Brotli processing with useful-representation filtering and preview HTTP content negotiation. Preserve stable input/output shapes for downstream actions.

## Acceptance criteria

- [ ] Delivered behavior and its spec criteria pass focused validation.
- [ ] Existing behavior remains covered; new diagnostics and generated artifacts are checked.
- [ ] Evidence is recorded and the implementation/task state committed together.

## Validation

Packaging/manifest/path/conflict/deduplication tests, Brotli round-trip/server negotiation tests, actual application trees and variant unions; aquery/incremental evidence and site consumption checks.

## Evidence

- pending
