# T03 — Compile explicit files and dependency catalogues

- Status: in_progress
- Blocked by: T02
- Spec coverage: R1, R2 (dependency validation), R12 (files/packaging); AC1, AC7 (file origins)

## Delivers

Package-relative explicitly declared CSS files supply utility libraries and checked globals. Separately compiled utility consumers validate against an automatically derived catalogue present during actual Java/header compilation.

## Acceptance criteria

- [ ] Exactly one inline block or file is required; reject traversal/absolute/noncanonical paths, absent/conflicting inputs and both source forms. No directory scanning or handwritten export inventory.
- [ ] JVM precompilation/catalogue integration works with native J2CL and paired JVM libraries; generated header-visible metadata or explicit catalogue classpath inputs survive real Bazel compilation boundaries.
- [ ] A dependency header jar deliberately lacking CSS resources still permits correct consumers and rejects an unused typo in a separate consuming compilation.
- [ ] Full jars/J2CL resources preserve CSS, maps and canonical metadata. Standalone javac packaging has an explicit documented way to supply source/catalogue inputs.
- [ ] CSS source maps identify the actual CSS file and include its authored content; inline maps continue identifying Java text blocks.
- [ ] Public build exports use existing explicit package ownership conventions and remain JVM-only and hermetic.

## Validation

- Real Bazel provider/consumer targets plus direct javac tests inspect the actual header/full jars and test valid and invalid declarations.
- Source-map decoding and resource artifact tests verify file identity/content and separately compiled import metadata.
- Buildifier, hermeticity check and targeted J2CL/integration tests validate the new declared-input rule.

## Evidence

- pending
