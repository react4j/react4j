# T08 — Maven-equivalent publication archives

- Status: `complete`
- Blocked by: `T07`
- Spec coverage: `R8`, `R11`, `AC4`

## Delivers

Assemble reproducible core/DOM/processor binary/source/Javadoc/POM archives, shade dependencies and service metadata, and expose public JVM imports/plugins backed by assembled jars. Verify isolated JVM/processor consumption.

## Acceptance criteria

- [x] Archive checks prove complete generated/authored sources/resources, clean binary/source separation, processor isolation/service loading, correct Java levels and POM inputs. JVM/processor packaged consumer runs and repeatable jars match.

## Validation

- Build release artifact aggregate; inspect archives/POMs/class versions; run isolated packaged JVM/processor consumer and archive reproducibility check — proves the delivered behavior. Record actual labels, commands and outcomes below; do not mark complete with skipped required checks.

## Evidence

- `bazel build //tools/release:maven_artifacts //tools/release:site_javadoc //:core //:dom //:processor`: passes. Three binary/source/Javadoc/POM sets and aggregate API docs use the normalized Arez Java archive tools; processor namespace relocation/service merging is preserved.
- `bazel test //tools/release:archive_checks //tools/release/consumer/src/main/java/isolated:packaged_consumer //:java_format_check --test_output=errors`: both new consumers/check targets and formatting pass. Archive inspection verifies source bytes against direct compiler source jars, generated DOM, native JS/pinned assets, source/binary separation, sorted entries/stable timestamps/manifests, license metadata, class versions, shaded isolation/services, API pages and POM identities/dependencies.
- Inspected counts: core 56 owned Java 17 classes/53 source entries; DOM 74 owned Java 17 classes/74 source entries; processor 26 owned Java 17 classes/25 source entries plus 2700 shaded classes compatible with Java 21 (including Java 21 bytecode). A platform-parent class loader discovers and instantiates the React4j processor with its single jar and no application/dependency jars. The separate compiler consumer asserts Java 21, compiles a generic parent separately, discovers all three packaged processors through services using explicit artifact-only paths, checks the complete five generated Java files and inherited/nested/array qualifiers, runs packaged JVM configuration/key/DOM/factory APIs, and verifies Java 17 consumer/generated output. The public Bazel consumer also compiles all generated bodies under the product checker policy using only assembled public imports/plugins.
- `tools/release/check_reproducibility.sh`: passes; all 13 files match byte-for-byte against a separate Bazel output-base build (145 fresh actions). The temporary server is shut down and its build tree removed; cleanup makes only owned temporary directories writable and does not follow workspace symlinks. Ahab reproducibility declarations cover both new archive tools; final graph audit remains T14.
- `bazel test //tools/release:archive_checks --//tools/release:version=1.2.3-local-check --//tools/release:arez_version=4.5.6-local-check`: passes, proving distinct POM inputs. Restoring defaults and rerunning the two checks/formatting passes. Defaults remain honest local snapshots; tools/release/README.md records the migrated Arez Maven publication prerequisite and shaded Proton/optional Sting distinction.
- `bazel run //tools/buildifier:buildifier_check`, `bash -n tools/release/check_reproducibility.sh` and `git diff --check`: pass. Source Javadocs now describe the effective JSpecify/requiredness contracts instead of removed annotation warnings, keeping the generated API documentation accurate.

- T13 follow-up: public archive boundaries now also pass from a disposable external Bazel root. Bazel ignores dependency-module overrides, so the Java exporter supplies root pins, patches and Maven lock configuration. The external consumer compiles/runs the same real distribution fixture and compiles public J2CL libraries/example in development and ADVANCED production. This closes consumption configuration rather than adding real downstream builds.
