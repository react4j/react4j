# T02 — Standalone shaded artifact

- Status: complete
- Blocked by: T01
- Spec coverage: R2, AC2

## Delivers

Assemble a single relocated processor using shared jar machinery, remove runtime annotation loading, use artifact for extraction and isolated service-discovered integration, audit jar and dependency isolation.

## Acceptance criteria

- [x] Delivered behavior and corresponding spec criteria verified.

## Validation

Run isolated javac positive and application import negative control; inspect class/service/legal entries and processor closure; compare output semantics and independent artifact bytes.

## Evidence

`bazel test //css:tests //css/examples/save-button:browser_tests //tools/release:tests --test_output=errors`: 7/7 passed. Service discovery uses a single packaged processor path; a platform-parent loader cannot load core/parser public packages. Artifact audit checks relocated classes, sole processor service and distinct notices. `bazel cquery` confirms the public runtime closure contains only css-processor.jar. `css/check_reproducibility.sh`: shaded jar/srcjar/CSS match independent output base. Outputs still match pre-move baseline bytes. Java formatting and shell syntax passed.
