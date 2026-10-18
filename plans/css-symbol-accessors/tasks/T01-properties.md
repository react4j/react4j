# T01 — Property symbols and imports

- Status: complete
- Blocked by: None
- Spec coverage: R1/AC1 (properties), R2/AC2, R3/AC3, R6/AC6 (property migrations)

## Delivers

Checked property accessors on all three definition kinds, all-local CSS properties and explicit owner-preserving imports/overrides, exercised through migrated applications and separately compiled providers. Extend resource dependency metadata and bundler traversal for property imports across all definition kinds.

## Acceptance criteria

- [x] Annotations/inference, optional resource bindings, property rewriting and usage validation work.
- [x] Imports preserve owner identity, overrides, transitive/header-jar behavior and retention with precise author errors.
- [x] Bundling retains transitive mixed-kind dependencies, emits all required globals once, preserves default/override ordering and allows valid mixed cycles while rejecting global-inclusion cycles.
- [x] All existing example setters/sharing and assertions use resolved property names; cascade still tested using an ordinary property.
- [x] Focused units and formatted/unformatted golden families pass; development browser tests preserve visible behavior.

## Validation

Run CSS compiler/unit and packaged/implementation processor suites, metadata/bundler unit tests (including component → keyframes → global paths), integration tests, Java formatting and affected development browser tests. Inspect regenerated goldens rather than editing generated Java.

## Evidence

Passed tools/update_goldens.sh css (packaged processor and paired goldens), implementation_tests, unit_tests, internal/bundler tests and the separate JVM/J2CL integration test. All 7 development browser suites pass, including all migrated applications. Java format check passes. Reviewed the new property/resource goldens and metadata changes. Resource-binding roots were implemented with the property slice because its optional global/keyframe bindings require the same retention policy.
