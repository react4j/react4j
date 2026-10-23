# T08 — Verify complete applications and document delivery

- Status: complete
- Blocked by: T07
- Spec coverage: R1–R13 (cross-cutting); AC1–AC8

## Delivers

A documented complete application demonstrates the accepted authoring/build contract in development and optimized production, with durable regression coverage and a passing repository gate.

## Acceptance criteria

- [x] End-to-end fixture combines file-backed utility/global sources, compiled dependency contracts, hybrid/interface methods, fragments, external hooks, support variables, animations/fonts/assets, named containers and peer selectors.
- [x] BUNDLE/development, ADVANCED/production and compression-disabled builds preserve expected computed styles, checked values and resources. Multiple CSS variants share names while selecting independent globals.
- [x] CSS/JS maps include original file/Java content and retain correct locations after renaming, pruning and URL finalization. Existing stale/missing liveness and collision failures remain enforced.
- [x] Publish clear authoring/build examples and constraints in css/README.md; update Unreleased changelog and canonical specs/glossary/ADR owners without duplicating mutable configuration.
- [x] Every meaningful behavior has appropriate retained tests; no temporary generated corpus, installations, debug logs, plan-only validation scripts or unused scaffolding enter product code.
- [x] tools/check.sh passes, specification-only diff and local documentation links are audited, and final diff contains intentional scope only.

## Review and closeout

After implementation acceptance, provide complete evidence to a fresh independent reviewer and record every attempt/disposition in TASKMAP.md. After Findings: none, closeout promotes remaining knowledge, removes the entire exact active plan tree, validates removal and commits it. These are phase obligations tracked by TASKMAP.md rather than implementation prerequisites.

## Validation

- Run the narrow CSS processor/internal/linker/bundler/integration suites and development/optimized CSS browser targets as changes warrant.
- tools/check.sh — complete formatting, static checks, hermeticity, JVM/coverage, browsers, packaging and site/release validation.
- Domain-modeling link validator, site check and final git diff/status audit — prove documentation integrity and cleanup.
- Independent implementation review — confirms delivered scope, tests, compatibility boundaries and durable promotion.

## Evidence

- The packaged assets application reuses the browser fixture's actual render tree and combines an inline library, file-backed dependency provider and explicit global file with inherited utility fragments, hybrid CSS, external hooks, property chains, animations/fonts/files, named containers and group/peer selectors. Browser assertions exercise the combined application at nested routes, independent CSS variants, production naming and uncompressed names. CSS maps retain logical file paths, original file content and inline Java sources.
- Retained generated-shape cases cover Tailwind 4.3.3's verified empty var(--tw-rotate-x, ) fallback and escaped group/peer selector functions. No compiler installation or generated corpus is added.
- Simplification replaces a repurposed selector token record in UtilityNames with its actual name-use record and reuses the shared animation-name decoder.
- Full-gate investigation exposed a pre-existing finalizer limitation when the combined application generated a long uncompressed-name JavaScript line: Closure Node columns saturate at 4095. Asset edits now use the same pinned parser's exact public tree ranges, preserving raw output bytes and source maps. Regression coverage exercises repeated/escaped tokens beyond that column, wrapper directives, comments/regular expressions and mapped origins. The optimized uncompressed browser target and bundler suite pass.
- A Bundle regression verifies outer precedence, private conflicting library layer orders, empty-library omission and mapped output offsets. Focused JVM coverage reports 261/265 lines and 158/166 branches for Bundle, exceeding its unchanged thresholds.
- Reader examples/grammar limits and Unreleased changelog are complete. Canonical requirements/ADR/glossary promoted in T01–T07 still own all accepted durable outcomes; this task adds realization and verification evidence only. Final specification-only audit retains implementation-independent obligations and no delivery sequence, tool version or temporary evidence. Documentation links and git diff --check pass.
- Final tools/check.sh passed on 2026-10-23: all 50 normal test targets, all 15 optimized browser targets and all 23 instrumented JVM targets passed; runtime/JVM coverage thresholds, formatting, hermeticity, public artifact builds, site build/check and unsigned local package validation passed. Package validation verified 12 artifacts and 60 unsigned bundle entries. The owning asset suite separately passed all 10 methods, including the precise-range regression. Final documentation links and git diff --check passed.

- Post-review validation: tools/check.sh passed again after both naming fixes, with all 50 normal, 15 optimized browser and 23 instrumented JVM targets passing, including all 611 packaged processor methods and the new real interpolation test in compressed, uncompressed and prefixed variants. Coverage/site/local packaging, domain links and the final specification-only/diff audit passed.
