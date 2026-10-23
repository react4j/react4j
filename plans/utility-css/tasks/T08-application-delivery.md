# T08 — Verify complete applications and document delivery

- Status: pending
- Blocked by: T07
- Spec coverage: R1–R13 (cross-cutting); AC1–AC8

## Delivers

A documented complete application demonstrates the accepted authoring/build contract in development and optimized production, with durable regression coverage and a passing repository gate.

## Acceptance criteria

- [ ] End-to-end fixture combines file-backed utility/global sources, compiled dependency contracts, hybrid/interface methods, fragments, external hooks, support variables, animations/fonts/assets, named containers and peer selectors.
- [ ] BUNDLE/development, ADVANCED/production and compression-disabled builds preserve expected computed styles, checked values and resources. Multiple CSS variants share names while selecting independent globals.
- [ ] CSS/JS maps include original file/Java content and retain correct locations after renaming, pruning and URL finalization. Existing stale/missing liveness and collision failures remain enforced.
- [ ] Publish clear authoring/build examples and constraints in css/README.md; update Unreleased changelog and canonical specs/glossary/ADR owners without duplicating mutable configuration.
- [ ] Every meaningful behavior has appropriate retained tests; no temporary generated corpus, installations, debug logs, plan-only validation scripts or unused scaffolding enter product code.
- [ ] tools/check.sh passes, specification-only diff and local documentation links are audited, and final diff contains intentional scope only.
- [ ] Provide complete evidence to a fresh independent implementation reviewer; record all attempts/dispositions in TASKMAP.md. Closeout after Findings: none promotes remaining knowledge, removes the entire exact active plan tree, validates removal and commits it.

## Validation

- Run the narrow CSS processor/internal/linker/bundler/integration suites and development/optimized CSS browser targets as changes warrant.
- tools/check.sh — complete formatting, static checks, hermeticity, JVM/coverage, browsers, packaging and site/release validation.
- Domain-modeling link validator, site check and final git diff/status audit — prove documentation integrity and cleanup.
- Independent implementation review — confirms delivered scope, tests, compatibility boundaries and durable promotion.

## Evidence

- pending
