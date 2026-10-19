# T07 — Delivery verification and permanent docs

- Status: `complete`
- Blocked by: T06
- Spec coverage: R9; AC7, full-scope reconciliation of AC1–AC6.

## Delivers

Reviewable production implementation, concise user docs and complete evidence on PR112.

## Acceptance criteria

- [x] README documents actual APIs, effective annotations, stable authored globals, per-link controls/prefix, reverse maps and debugging boundaries; changelog describes downstream behavior. Remove obsolete claims and update all affected examples.
- [x] Domain specification-only diff passes replacement audit; every accepted durable requirement/decision has one canonical owner, old compression deferral/backlinks removed, requested mapping deferrals retained.
- [x] All focused source/binary/golden/real-link/browser/map tests pass. Processor timeout disposition is measured and justified. Coverage roots retain at least configured 95/85 thresholds without weakening exclusions/rules.
- [x] Run simplify, review exact implementation diff, and run `tools/check.sh`; record full-gate evidence before task completion commit. Preserve distributable shading and optional formatting checks.

## Validation

- `tools/check.sh` — formatter, hermetic builds, JVM/browser/golden tests, coverage, downstream consumer, site/docs, unsigned release validation and reproducibility.
- `python3 /Users/peter/.codex/skills/domain-modeling/scripts/validate_doc_links.py .` and exact diff/status inspection.
- Independent reviewer dispositions and final remote branch/PR head evidence; inspect Verify result or report its actual pending state.

## Evidence

- Simplify applied across the implementation diff: use cached compiled getter compositions, remove the redundant table-rebuilding API and its test-only use, avoid map serialization for no-op resolution, and preserve companion path identity explicitly. Eight focused JVM targets passed afterward.
- Migrated the remaining Drumloop/Web Speech browser assertions from hardcoded generated names to DOM elements and actual custom properties. Both optimized suites passed; full gate now passes all 40 ordinary test targets, all 8 optimized browser suites and all 20 JVM coverage targets.
- Coverage passed unchanged policy: CSS processor 1535/1554 lines and 1093/1147 branches; internal 988/1002 and 587/636; linker 174/174 and 100/106; bundler 399/406 and 244/260. All other roots and class overrides pass too.
- Added exact WebDriver loopback exceptions for the two new naming suites, matching the existing browser runner; normal and optimized hermeticity checks pass.
- Domain replacement audit rechecked the committed specifications against observable behavior. Existing canonical owners cover all durable conclusions; source-path enrichment details stay in code/README. Link validation passes. Both requested mapping deferrals remain.
- Complete `tools/check.sh` passed: formatters, normal/optimized hermeticity, 40 ordinary test targets, 8 optimized browser suites, 20 JVM coverage targets, runtime coverage, external Bazel consumer in both modes, site build/check, unsigned release validation and all 13 publication/API artifacts matching an independent build. Domain links and exact diff checks passed.
- Implementation review and closeout are subsequent workflow gates recorded in TASKMAP, not implementation task criteria.
