# T08 — Verify and document the complete delivered contract

- Status: `pending`
- Blocked by: `T07`
- Spec coverage: `R11`, all-task integration, `AC8`

## Delivers

A coherent shipped feature with discoverable author/application documentation, current durable contracts and full project verification, ready for independent review.

## Acceptance criteria

- [ ] Audit accepted examples/negative cases end to end; fill meaningful uncovered behavior and stale finite-only assumptions across peers.
- [ ] Register new targets in test/analysis/hermeticity/coverage inventories and instrumentation filters; preserve current checker and coverage thresholds.
- [ ] Promote accepted behavior into existing specification owners, extend glossary meaning for native libraries/anchors, and replace the finite-only ADR with the adopted JVM rule-compilation rationale; update inbound links.
- [ ] Keep usable author reference/examples and changelog/README current; reconcile or remove completed design narration after lasting obligations/rationale move to their canonical owners.
- [ ] Run task-level suites, development/optimized browser tests and full tools/check.sh, fixing evidence-based failures. Validate changed docs/config syntax and inspect complete diff.
- [ ] Record evidence and all material limitations before independent implementation review; review/cleanup/PR publication remain workflow gates after task completion.

## Validation

- `tools/check.sh` — full local/CI gate, including format, hermeticity, checked publication, JVM/browser tests, coverage, site and unsigned release validation.
- Domain-modeling document-link validator plus spec-only diff audit and package/dependency audit.
- Actual browser examples and independent review inputs cover the final implementation, not abandoned approaches.

## Evidence

- `pending`
