# T02 — ID and animation exports

- Status: pending
- Blocked by: T01
- Spec coverage: R1/AC1 (IDs/animations), R4/AC4, R5/AC5, R6/AC6 (new examples)

## Delivers

Checked ID/animation names on component/global/keyframe definitions with parsed ID references and accessor-only animation retention, used by real JVM/J2CL/browser consumers.

## Acceptance criteria

- [ ] All three definition kinds expose appropriate symbols with existing lazy package-access bindings and stateless author constraints.
- [ ] IDs rewrite hashes/pure fragments consistently and diagnose unsupported bound attribute references; strings/assets stay unchanged.
- [ ] Resource bindings conservatively root their CSS in reachable targets; direct Java-only keyframe/global access works without a CSS importer or explicit global root.
- [ ] Local/imported/self animation bindings match CSS and import-only accessors retain keyframes.
- [ ] Positive and invalid author variations, emitted resources, source maps and generated formatting are tested.

## Validation

Run focused units and processor goldens, bundler root/retention tests, separately compiled integration tests, and save-button browser tests in development and optimized modes. Run Java/Bazel formatting checks.

## Evidence

Pending.
