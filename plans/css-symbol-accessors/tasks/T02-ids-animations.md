# T02 — ID and animation exports

- Status: complete
- Blocked by: T01
- Spec coverage: R1/AC1 (IDs/animations), R4/AC4, R5/AC5, R6/AC6 (new examples)

## Delivers

Checked ID/animation names on component/global/keyframe definitions with parsed ID references and accessor-only animation retention, used by real JVM/J2CL/browser consumers.

## Acceptance criteria

- [x] All three definition kinds expose appropriate symbols with existing lazy package-access bindings and stateless author constraints.
- [x] IDs rewrite hashes/pure fragments consistently and diagnose unsupported bound attribute references; strings/assets stay unchanged.
- [x] Resource bindings conservatively root their CSS in reachable targets; direct Java-only keyframe/global access works without a CSS importer or explicit global root.
- [x] Local/imported/self animation bindings match CSS and import-only accessors retain keyframes.
- [x] Positive and invalid author variations, emitted resources, source maps and generated formatting are tested.

## Validation

Run focused units and processor goldens, bundler root/retention tests, separately compiled integration tests, and save-button browser tests in development and optimized modes. Run Java/Bazel formatting checks.

## Evidence

Passed all CSS unit tests, packaged and implementation processor suites and paired symbols/diagnostic goldens. JVM/J2CL integration verifies header-compiled resource APIs, symbol agreement and original selector/property/fragment source locations. Save-button browser tests pass in development and ADVANCED optimized modes, exercising IDs, SVG fragments, imported/local/direct animations and Java-only globals. Bundler tests reproduce and fix the mixed property-cycle/inclusion ordering edge; both entry roots now preserve inclusion order. Java formatting and buildifier checks pass.
