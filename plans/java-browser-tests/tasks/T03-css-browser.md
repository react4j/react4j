# T03 — CSS browser assertions

- Status: `complete`
- Blocked by: T01
- Spec coverage: R1,R2,R4,R5; AC1,AC3

## Delivers

Migrate every SaveButton case into Java, retaining CSSOM traversal, animation control, packaged files/fonts, source maps, independent fragments, naming variants, app mount/nested route checks and failure-safe teardown. Audit the two CSS-link entry fixtures as native callgraph/entry wiring.

## Acceptance criteria

- [x] Delivered behavior and relevant spec criteria preserve the original cases.
- [x] Focused checks and formatting pass; the Java/native boundary is inspected.
- [x] Evidence and task state are committed together.

## Validation

SaveButton default/prefixed/uncompressed browser suites in development and optimized production; real packaged asset requests and all original CSS case identities.

## Evidence

- All 16 original cases passed in default/prefixed/uncompressed suites in development and -c opt ADVANCED production. Real root/nested application routes, image decode, fonts, manifests and resource requests remain exercised.
- Java formatter, buildifier and diff checks passed. Browser helpers perform native DOM/CSSOM/fetch/animation operations and regex execution; Java supplies regex patterns, expected data and control flow.
- Preserved the original 20 ms CSS polling and 25 ms DOM polling intervals. Fixed native RegExp-array handling without Java generic array casts; optimized Closure type checks pass.
- CSS link entry fixtures were audited in T02 and retain only deliberate link/callgraph wiring.
