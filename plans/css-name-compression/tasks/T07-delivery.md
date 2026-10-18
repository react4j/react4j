# T07 — Delivery verification and permanent docs

- Status: `pending`
- Blocked by: T06
- Spec coverage: R9; AC7, full-scope reconciliation of AC1–AC6.

## Delivers

Reviewable production implementation, concise user docs and complete evidence on PR112.

## Acceptance criteria

- [ ] README documents actual APIs, effective annotations, stable authored globals, per-link controls/prefix, reverse maps and debugging boundaries; changelog describes downstream behavior. Remove obsolete claims and update all affected examples.
- [ ] Domain specification-only diff passes replacement audit; every accepted durable requirement/decision has one canonical owner, old compression deferral/backlinks removed, requested mapping deferrals retained.
- [ ] All focused source/binary/golden/real-link/browser/map tests pass. Processor timeout disposition is measured and justified. Coverage roots retain at least configured 95/85 thresholds without weakening exclusions/rules.
- [ ] Run simplify, review exact implementation diff, and run `tools/check.sh`; record full-gate evidence before task completion commit. Preserve distributable shading and optional formatting checks.
- [ ] Fresh read-only implementation review passes under skill budget; fix findings, rerun proportionate gates and commit evidence before closeout.
- [ ] Closeout removes exact completed plan tree and scratch output after preserving useful results in permanent owners/git/PR. Commit and push PR112, refresh its downstream-facing description and attach if needed; no merge.

## Validation

- `tools/check.sh` — formatter, hermetic builds, JVM/browser/golden tests, coverage, downstream consumer, site/docs, unsigned release validation and reproducibility.
- `python3 /Users/peter/.codex/skills/domain-modeling/scripts/validate_doc_links.py .` and exact diff/status inspection.
- Independent reviewer dispositions and final remote branch/PR head evidence; inspect Verify result or report its actual pending state.

## Evidence

Pending.
