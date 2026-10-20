# Independent browser application settings

- Status: complete
- Blocked by: None
- Spec coverage: R1, R2 / AC1, AC2

## Delivers

Decouple application runtime and optimization, align custom define values in compiled and BUNDLE paths, independently control map publication, support caller-owned launch pages, and retain ordinary defaults. Update private assembly implementation and test the complete public mode/configuration/map matrix with real React.

## Acceptance

- [x] Delivered behavior satisfies assigned spec criteria.
- [x] Meaningful verification passes and limitations are explicit.

## Validation

Unit/packaging tests; browser matrix; invalid configuration tests; formatter and Starlark loading checks.

## Evidence

`bazel test //tools/verification/src/test/java/react4j/verification:application_tests //css/bundler/src/test/java/react4j/css/bundler:packaging_tests --test_output=errors` passed (eight real-runtime compilation/map combinations; nine packaging/configuration tests). Invalid runtime/compilation macro invocations rejected with expected diagnostics. Buildifier applied, `bazel build //:java_format_check` and `git diff --check` passed. Existing mode defaults remain selected from compilation_mode; full example-mode verification follows in T04.
