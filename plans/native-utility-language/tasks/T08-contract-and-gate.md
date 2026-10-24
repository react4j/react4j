# T08 — Verify and document the complete delivered contract

- Status: `complete`
- Blocked by: `T07`
- Spec coverage: `R11`, all-task integration, `AC8`

## Delivers

A coherent shipped feature with discoverable author/application documentation, current durable contracts and full project verification, ready for independent review.

## Acceptance criteria

- [x] Audit accepted examples/negative cases end to end; fill meaningful uncovered behavior and stale finite-only assumptions across peers.
- [x] Register new targets in test/analysis/hermeticity/coverage inventories and instrumentation filters; preserve current checker and coverage thresholds.
- [x] Promote accepted behavior into existing specification owners, extend glossary meaning for native libraries/anchors, and replace the finite-only ADR with the adopted JVM rule-compilation rationale; update inbound links.
- [x] Keep usable author reference/examples and changelog/README current; reconcile or remove completed design narration after lasting obligations/rationale move to their canonical owners.
- [x] Run task-level suites, development/optimized browser tests and full tools/check.sh, fixing evidence-based failures. Validate changed docs/config syntax and inspect complete diff.
- [x] Record evidence and all material limitations before independent implementation review; review/cleanup/PR publication remain workflow gates after task completion.

## Validation

- `tools/check.sh` — full local/CI gate, including format, hermeticity, checked publication, JVM/browser tests, coverage, site and unsigned release validation.
- Domain-modeling document-link validator plus spec-only diff audit and package/dependency audit.
- Actual browser examples and independent review inputs cover the final implementation, not abandoned approaches.

## Evidence

- Added malformed unused definitions and known invalid static value/condition cases; the language suite passes 399 cases. Final full JVM coverage reaches 2166/2206 lines (98.19%) and 1629/1850 branches (88.05%), above unchanged 95% / 85% thresholds.
- `tools/check.sh` passed, then passed again against the final source snapshot after the fragment correction: 56 main test targets, 16 optimized browser targets, 25 JVM coverage targets; format/buildifier, development/optimized hermeticity, publication/Javadocs, runtime coverage and every configured threshold, site build/check, and unsigned release validation (12 artifacts / 60 bundle entries). No check was skipped or threshold reduced.
- Focused facade/fragment and packaged Java Unicode tests passed. The complete final suites cover all new stability, malformed definition and type cases; the packaged authoring command/distribution tests and actual separate-target J2CL integration are included in the main gate.
- Package/dependency audit: only the CSS processor imports the language facade; linker/bundler consume shared generic CSS metadata. The package has explicit sources and restricted visibility, is shaded into the existing processor distribution, and adds no runtime/Maven artifact or source scanner. Existing hermetic Java build actions cover the new module; the explicit-file authoring command is not a build action.
- Naming audit found missing native stability propagation; provider and owned resource policies now survive checked snapshots, header compilation, independently requested candidates and actual compressed name-table allocation. HTML whitespace audit found canonical-name and fragment validation used broader Unicode whitespace; a JVM probe reproduced the fragment failure for a string containing EM SPACE. Shared metadata now follows candidate HTML whitespace semantics, with facade/fragment and Java publication coverage.
- Promoted accepted behavior to the native, utility, bindings, names, liveness and source-map specifications, glossary and ADRs 0005/0006. The usable author reference replaces completed design narration; obsolete finite-only ADR 0004 and inbound references are removed. The design remains recoverable at `54d35cfa9`.
- Domain-link validator, shell/JSON syntax checks and whitespace diff audit pass. Specification-only diff audit applied the replacement test to every changed sentence: grammar/type/order/identity/origin details constrain observable conformance; package paths, commands, protocol realization and evidence remain in code/reference/tasks. First glossary uses and changed ADR links resolve. No new deferred question or implementation mismatch remains from this audit.
- Browser evidence is limited to pinned Chrome development/optimized output and the explicit tested layouts. Other engines, writing modes, zoom and every layout combination are not established. Rose is unchanged; its existing popover failures are not claimed fixed.

## Knowledge classification before drafting

- Accepted grammar/value/variant/range/diagnostic behavior passes the specification replacement test and belongs in the existing utility contract plus a native-language specification. Java selection belongs in bindings; identities/stability in names; fragment retention in liveness; available origins in source maps. These remain obligations under a replacement implementation.
- The adopted CSS-oriented pure JVM compiler and consumer-requested publication pattern are separate architectural decisions with non-obvious alternatives and reversal costs; replace finite-only ADR 0004 with ADRs 0005/0006. Package paths, CLI commands and mutable source inventories belong in the author reference/code.
- Extend existing Utility CSS library/CSS symbol meanings and define Utility candidate in the glossary. No new deferred issue or use case is needed: accepted behavior is resolved and existing application goals are unchanged. Existing identifier-token and JavaScript-to-authored-CSS mapping deferrals remain authoritative.
- Browser test results, delivery history and discovered fixes remain task evidence/PR history. Keep the popover recipe and browser limitations in the usable author reference, without a Rose migration/fix claim. Move accepted grammar/reference material out of the completed design document and remove it after inbound references are updated.
