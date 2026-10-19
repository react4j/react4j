# T01 — Checked asset declarations

- Status: `complete`
- Blocked by: None
- Spec coverage: R1,R2; AC1

## Delivers

Deliver @Assets/@Asset declarations and lazy generated accessors, an independent versioned metadata contract, Bazel web_assets catalogues and Java/J2CL propagation without file-byte compilation inputs. Validate package-relative path resolution, generated inputs and duplicate logical paths. Preserve metadata in J2CL/source archives. Include strict processor tests/goldens and Bazel catalogue validation tests.

## Acceptance criteria

- [x] Delivered behavior and its spec criteria pass focused validation.
- [x] Existing behavior remains covered; new diagnostics and generated artifacts are checked.
- [x] Evidence is recorded and the implementation/task state committed together.

## Validation

Processor suites, asset metadata/path unit tests, package-local build/formatter checks and a small J2CL asset declaration build.

## Evidence

- Added @Assets/@Asset, lazy package-access Assets_* generation, immutable versioned metadata/path/catalogue validation, transitive byte-independent Bazel catalogues, and J2CL metadata preservation.
- `tools/update_goldens.sh css`: passed; generated/reviewed four new formatted/unformatted golden files (no manual generated-source edits).
- Combined processor implementation + shaded-jar, unit, internal and real J2CL asset integration run: all 10 targets passed; implementation suite 581 tests and shaded suite 583 tests pass.
- Actual collection aspect traverses the Java/J2CL graph via an asset-only CSS bundle; metadata and source-file catalogue validate without embedding SVG contents.
- `bazel aquery` over the asset fixture: checked all 86 Java/J2CL actions; no SVG/PNG/WOFF2 or asset catalogue file is a compiler input.
- `bazel run //tools/buildifier:buildifier_check` and `tools/java_format.sh check`: passed.
