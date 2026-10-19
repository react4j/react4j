# T05 — Examples, downstream contracts and full gates

- Status: `complete`
- Blocked by: T04
- Spec coverage: R1,R2,R3,R4,R5,R6,R7,R8,R9,R10,R11; AC1,AC2,AC3,AC4,AC5

## Delivers

Extend the colocated CSS example with SVG/PNG/font and CSS-only/dead assets; migrate Drumloop's font and all four consumers to one checked shared family; convert toggle/toggle_checked SVG references in all three TodoMVC variants using shared declarations/catalogues and remove redundant manual mappings; demonstrate generated/preprocessed inputs. Add real browser assertions for asset fetches, deployment subpaths and precache policy. Update permanent user docs/changelog, downstream exports and publication/coverage wiring. Promote durable contracts, simplify verified implementation, run full repository gates and resolve review findings before closeout.

## Acceptance criteria

- [x] Delivered behavior and its spec criteria pass focused validation.
- [x] Existing behavior remains covered; new diagnostics and generated artifacts are checked.
- [x] Evidence is recorded and the implementation/task state committed together.

## Validation

Development and optimized browser suites, external consumer, complete tools/check.sh, source-map checks, deterministic archives, coverage >=95/85, diff/simplify review and canonical-doc link validation.

## Evidence

- Development browser suites pass against real packaged SVG/PNG/CSS-background/font requests, both at an application mount and nested page. Optimized/default, prefixed and uncompressed naming variants verify live/dead delivery and precache/on-demand separation. All six TodoMVC SVG URLs migrated; Drumloop's four consumers share one checked owned family and load the real font.
- Explicit SVG preprocessing uses a declared Bazel template output. Java/CSS source layouts match package conventions; package-local libraries list sources explicitly. Native Brotli and application inventory/HTTP tests remain in the normal gate.
- Disposable external consumer builds Java/J2CL/CSS/assets/owned font/application packaging in development and ADVANCED using exported root pins/patches. It exposed and now covers caller-repository label resolution in checked Java/browser macros.
- Simplify pass reviewed the complete feature diff and affected peers: removed redundant example exports/visibility, retained separate metadata/list assembly to preserve cache boundaries, and preserved existing no-compatibility/no-Node conventions. Reverse inventory now records declaring accessors even for CSS-only retention, with a real-link regression.
- Previous Linux CI trace points exactly to Drumloop's 400px resize assertion (offset from the waitFor throw to its call). Assertion now measures independently available document width, accounting for visible scrollbars; local development/optimized browser checks pass. New Linux CI will confirm platform behavior after publication.
- Final full gate: 45 development/JVM targets, 8 optimized browser targets and 23 JVM coverage targets passed. CSS roots: processor 1812/1834 lines and 1252/1325 branches; internal 1775/1807 and 1144/1242; linker 180/188 and 101/108; bundler 1002/1039 and 576/649. External consumer in both modes, site build/check, release validation and all 15 independent publication/API/CSS archive comparisons also passed; tools/check.sh ended with All React4j checks passed.
- Permanent user guide/changelog updated; full case folding, typed declarations/imports, URL/cache contracts and inventory ownership promoted to canonical docs. Domain link validator passes. No new deferred issue.
