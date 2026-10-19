# T02 — Java asset liveness and late URLs

- Status: `complete`
- Blocked by: T01
- Spec coverage: R3,R6,R7; AC2,AC3

## Delivers

Track asset accessor identities through real Closure optimization, emit validated asset liveness evidence and finalize direct URL literals after linking with preserved JavaScript maps. Verify live/dead/inlined/interface values, BUNDLE behavior, stale evidence and byte-independent compile actions. Demonstrate correct synchronous URLs with application-base handling.

## Acceptance criteria

- [x] Delivered behavior and its spec criteria pass focused validation.
- [x] Existing behavior remains covered; new diagnostics and generated artifacts are checked.
- [x] Evidence is recorded and the implementation/task state committed together.

## Validation

Linker/finalizer unit tests and real J2CL/ADVANCED integration tests, source-map anchor/content assertions, incremental image-byte mutation action evidence.

## Evidence

- Six focused suites passed: internal/linker/bundler, actual J2CL assets, existing CSS integration, and the new real-link finalizer suite (5 scenarios in both modes). Java format and buildifier checks passed.
- ADVANCED retains only live/interface/inlined asset getters; BUNDLE conservatively retains all. A generated-name shadowing fixture passes through real J2CL. Authored lookalike strings remain unchanged; original generated Java contents and URL anchors are preserved in maps.
- Mutated asset.svg bytes, rebuilt asset_optimized_web with --subcommands, and restored/rebuilt in finally. URL and finalized JS changed; only WebBundle ran, with no Javac, J2CL or Closure action. No experiment artifacts retained.
- Packaging validation found output-wrapper isolation needed preserving: capture now lives inside a single synchronous IIFE when all bindings are inside it. Direct/call/apply/arrow/unary wrappers and lazy/argument/global fallbacks pass focused tests.
