# T01 — Subproject layout

- Status: complete
- Blocked by: None
- Spec coverage: R1, AC1; R4 path/documentation ownership

## Delivers

Move annotation, implementation, examples and integration fixtures into requested subprojects; move processor implementation package; update build, service, tooling and docs without changing generated output.

## Acceptance criteria

- [x] Delivered behavior and corresponding spec criteria verified.

## Validation

Compare pre/post generated Java and CSS; run processor/compiler/integration tests, browser tests and formatting checks; search obsolete paths/labels.

## Evidence

`bazel test //css:tests //css/examples/save-button:browser_tests --test_output=errors`: 4/4 passed (16 JVM methods plus browser suite). `bazel build //:java_format_check`: passed. Pre/post example and integration srcjar/CSS: four byte-for-byte matches. Obsolete source-root/label search: no matches; README local links checked.
