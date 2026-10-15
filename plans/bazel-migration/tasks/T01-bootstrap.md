# T01 — Pinned Bazel graph and maintenance tools

- Status: `complete`
- Blocked by: `None`
- Spec coverage: `R1`, `R3`, `R12`, `AC1`, `AC2`, `AC7`

## Delivers

Bazel resolves the immutable toolchain/dependency graph and runs buildifier/formatter/Ahab entrypoints. Port relevant reference patches and dependency update configuration; pin the selected dependency commits.

## Acceptance criteria

- [x] Bazel version and toolchain resolution succeed; module graph is locked and all direct dependencies have explicit ownership. Buildifier and formatter tools run; Ahab can analyze a declared seed graph without a fabricated final baseline.

## Validation

- bazel version; bazel mod graph; bazel run //tools/buildifier:buildifier_check; focused formatter/Ahab help or seed graph checks — proves the delivered behavior. Record actual labels, commands and outcomes below; do not mark complete with skipped required checks.

## Evidence

- `bazel version`: 9.3.0; remote tool JDK 25 and application JDK 21 resolved while building/running the formatter and dependency verification test.
- `bazel mod graph`: passed; Arez `6811e57bd0585752876f2de0996385e8d24aaf5f`, Proton `6d50db451021b7880097d0880907aa036a30a689`, Sting `fc89bc7c3f4450d3d1b75eaa58e63edfb37f1267`; module lock generated.
- `tools/update_java_deps.sh`: passed; checksummed depgen 0.26 generated only React4j's selected roots/transitives. `bazel test //third_party/java:verify_config_sha256`: passed (1 test).
- `bazel run //tools/buildifier:buildifier_check`: passed. Formatter compiled and formatted a temporary Java probe successfully; `--help` reports usage with exit 2, so the actual format operation was used as the smoke check.
- `bazel run //tools/ahab:hermeticity` and `:hermeticity.check`: passed on the current seed graph with empty exceptions/baseline. Final graph policy remains owned by T14.
- `bash -n tools/java_format.sh tools/update_java_deps.sh`, Ahab JSON parsing and `git diff --check`: passed. The root formatter-check target will be populated with owned source targets as they land (T03 onward/T14).
- No Ruby/Node commands used; no remote mutations or secret access. Temporary formatting probe removed.
