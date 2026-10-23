# T03 — Compile explicit files and dependency catalogues

- Status: complete
- Blocked by: T02
- Spec coverage: R1, R2 (dependency validation), R12 (files/packaging); AC1, AC7 (file origins)

## Delivers

Package-relative explicitly declared CSS files supply utility libraries and checked globals. Separately compiled utility consumers validate against an automatically derived catalogue present during actual Java/header compilation.

## Acceptance criteria

- [x] Exactly one inline block or file is required; reject traversal/absolute/noncanonical paths, absent/conflicting inputs and both source forms. No directory scanning or handwritten export inventory.
- [x] JVM precompilation/catalogue integration works with native J2CL and paired JVM libraries; generated header-visible metadata or explicit catalogue classpath inputs survive real Bazel compilation boundaries.
- [x] A dependency header jar deliberately lacking CSS resources still permits correct consumers and rejects an unused typo in a separate consuming compilation.
- [x] Full jars/J2CL resources preserve CSS, maps and canonical metadata. Standalone javac packaging has an explicit documented way to supply source/catalogue inputs.
- [x] CSS source maps identify the actual CSS file and include its authored content; inline maps continue identifying Java text blocks.
- [x] Public build exports use existing explicit package ownership conventions and remain JVM-only and hermetic.

## Validation

- Real Bazel provider/consumer targets plus direct javac tests inspect the actual header/full jars and test valid and invalid declarations.
- Source-map decoding and resource artifact tests verify file identity/content and separately compiled import metadata.
- Buildifier, hermeticity check and targeted J2CL/integration tests validate the new declared-input rule.

## Evidence

- Added public css_sources paired J2CL/JVM input targets and a packaged JVM CssInputJar tool. Explicit files and source-digest-bound, derived class inventories travel on the compilation classpath; no Node process or export inventory is required.
- Actual Bazel aquery confirmed the consumer uses libutility_dependency_java-hjar.jar plus the inherited utility_inputs_java.jar. A retained test inspects that real header jar, proves CSS resources are absent, accepts a valid consumer and rejects an unused typo through the packaged processor.
- File-backed utility/global J2CL resources retain CSS/maps/metadata and authored file contents. Source-map decoding checks the actual CSS location. Dynamic javac tests cover nested non-package directories, missing/both/empty sources and unsafe paths; unit tests cover stale/malformed catalogues and reproducible input jars.
- Pinned JDK source inspection established that javac skips jar directories invalid as Java package names. Input resource names therefore use a valid private namespace and encoded logical paths; diagnostic and map paths remain authored. This is a bounded plumbing detail under D7, not a scope change.
- Registered deterministic parser/input tools with the existing Ahab reproducibility owner; normal and optimized hermeticity checks passed without new exceptions. The implementation and packaged processor suites passed (including unchanged inline goldens). The unit/utility/internal/integration run passed 11 targets; a final refined run passed all four affected targets after line-ending normalization and source-diagnostic checks. CRLF and CR-only inputs retain original map content. Formatting, buildifier, documentation links and git diff --check passed.
