# T07 — Regenerate release evidence and run the full gate

- Status: `done`
- Blocked by: `T05`, `T06`
- Spec coverage: `R10`, `R11`, `AC10`, `AC11`, `D9`

## Delivers

The actual release workflow consumes the pushed temporary branches, every configured downstream application passes, and React4j records the resulting `0.229` size evidence.

## Acceptance criteria

- [x] The downstream statistics workflow fetches and builds all ten pushed upgrade branches.
- [x] `statistics.properties` contains regenerated `0.229` values from the migrated outputs.
- [x] Full React4j tests, packaging, API-diff validation, GWT compilation, and downstream checks pass.
- [x] Generated core and DOM POMs omit Akasha and active source/generated contracts contain no Akasha reference.
- [x] All React4j and downstream diffs are intentional; no temporary scratch output or unrelated edit remains.
- [x] Task map records complete full-gate and remote-branch evidence and enters implementation review.

## Validation

- `bundle exec buildr update_downstream_build_stats` and the complete repository test/package gates — prove release integration and refresh the expected measurements.
- Generated metadata, active-reference, git diff, status, and remote-ref checks — prove dependency absence and delivery integrity.

## Evidence

- `J2CL=yes bundle exec buildr update_downstream_build_stats` completed successfully and fetched, built, and measured all ten pushed upgrade branches. The generated `0.229` fixture contains the eight GWT application measurements plus the J2CL TodoMVC result (`3245` bytes).
- `J2CL=yes PRODUCT_VERSION=0.229 PREVIOUS_PRODUCT_VERSION=0.228 bundle exec buildr clean package` completed successfully. The gate ran the core tests, 468 processor tests, DOM tests, all React4j GWT links, API-diff validation (38 accepted differences), package generation, and all nine downstream test methods including the J2CL compile/transpile/Closure ADVANCED link.
- Some historical `0.228` comparison builds cannot compile with the current GWT/J2CL toolchain because their old GWT user jars and generated factories are incompatible. The downstream harness treats those unavailable baselines as non-fatal; every migrated `0.229` branch built successfully.
- `xmllint --noout` validated the generated core and DOM POMs. Case-insensitive scans found no Akasha reference in either POM or in `core/src`, `dom/src`, `dom/generated`, `processor/src`, `doc-examples/src`, and `core/generated`.
- Fresh remote fetches confirmed that every upgrade tip is exactly one commit above, and directly parented by, its unchanged permanent branch. The six TodoMVC tips are `ea6d54bd`, `62fc5f1c`, `3bb2e1e3`, `65691f8e`, `990b811f`, and `cce3b265`; the browser-application tips are `5c493541`, `ecc84d1d`, `e7415425`, and `e6f66b0b`.
- `gh pr list --state all --head ...` returned an empty result for all ten branches. Every downstream worktree is clean; the only React4j worktree change before this evidence update was the regenerated statistics fixture, and `git diff --check` passed.
