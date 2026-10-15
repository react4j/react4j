# T01 — Pinned Bazel graph and maintenance tools

- Status: `pending`
- Blocked by: `None`
- Spec coverage: `R1`, `R3`, `R12`, `AC1`, `AC2`, `AC7`

## Delivers

Bazel resolves the immutable toolchain/dependency graph and runs buildifier/formatter/Ahab entrypoints. Port relevant reference patches and dependency update configuration; pin the selected dependency commits.

## Acceptance criteria

- [ ] Bazel version and toolchain resolution succeed; module graph is locked and all direct dependencies have explicit ownership. Buildifier and formatter tools run; Ahab can analyze a declared seed graph without a fabricated final baseline.

## Validation

- bazel version; bazel mod graph; bazel run //tools/buildifier:buildifier_check; focused formatter/Ahab help or seed graph checks — proves the delivered behavior. Record actual labels, commands and outcomes below; do not mark complete with skipped required checks.

## Evidence

- pending
