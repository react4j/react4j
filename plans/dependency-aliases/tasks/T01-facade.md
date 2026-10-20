# Dependency facade

- Status: complete
- Blocked by: None
- Spec coverage: R1/AC1

## Delivers

Add curated public browser/plugin aliases plus private tool aliases, route handwritten external target references through the facade and migrate public examples/consumer checks. Preserve generated imports, actual provider identity and restricted tool visibility.

## Acceptance

- [x] Assigned spec outcome is demonstrably satisfied.
- [x] Validation passes with actual evidence and any independent limits recorded.

## Validation

Target builds/provider-consuming compilation; copied consumer positive/negative checks; Buildifier.

## Evidence

`bazel build //:core //:dom //third_party:arez_processor_plugin //third_party:sting_processor_plugin //third_party:nullaway_plugin //css/linker/src/main/java/react4j/css/linker:worker //tools/release:maven_artifacts` passed. Release suites plus eight-case application matrix passed (4 targets). Buildifier, bash syntax and diff checks pass. Full tools/check_bazel_consumer.sh passed: exact public inventory, all copied examples built and five browser targets passed in development and optimized modes, caller-owned ArrayEquals/NullAway failures and private dependency/load denials enforced. Examples no longer maintain duplicate bytecode imports. Fourteen public aliases include the existing browser-test TestCase library; all compiler/tool/JVM/React source-file aliases remain repository-only. Only raw dependency import definitions, module/load references and Bazel transition allowlist retain direct external labels.
