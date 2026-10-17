# T03 — Golden harness and delivery

- Status: complete
- Blocked by: T02
- Spec coverage: R3, AC3; R4, AC4

## Delivers

Add complete Java/CSS output and diagnostic fixtures, strict generated-code checks with failing control, intentional updater and docs. Run full gate and reproducibility; review and update draft PR, remove temporary plans at closeout.

## Acceptance criteria

- [x] Fixture harness, updater, documentation and full validation complete. Draft PR delivery and temporary-tree removal follow the workflow review/closeout gates.

## Validation

Run golden/parser/processor/integration tests, verify updater idempotence and golden drift rejection, strict-check negative control, tools/check.sh, css/check_reproducibility.sh and processor artifact repeatability; inspect diff and draft PR status.

## Evidence

26 fixture cases (8 positive, 18 negative), 10 generated Java and 10 CSS goldens, 26 diagnostic files. Processor tests: 36 invocations pass; parser/mapping: 7 methods pass; integration passes. Every successful dynamic/fixture compile passes the shared strict checker. Deliberately invalid generated null return is rejected by NullAway.

`tools/update_goldens.sh css`: passes, Updated 0. A temporary copy with changed Java content, an extra output and a missing output failed exactly those three cases. `tools/update_goldens.sh regressions`: passes (2 methods), Updated 0. Shell syntax, changed JSON and local documentation links checked.

`tools/check.sh`: passed all stages: 23 default targets, 6 optimized browser targets, 9 JVM coverage targets, external consumers, site, release validation and 13 independently reproduced publication/API artifacts. `css/check_reproducibility.sh`: current shaded processor/source archive/CSS match independent output base. Existing named-owner example and integration output bytes match pre-move baseline; only default-package generated output gains its unavoidable DefaultPackage warning suppression.

Canonical CSS README, contributing guide, changelog and agent code-generation guide updated. No docs/adr, docs/glossary, docs/specs or docs/deferred directories exist. PR publication and temporary plan cleanup remain lifecycle closeout actions; PR must stay draft per user instruction.
