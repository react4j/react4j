# T07 — Expose agent authoring feedback and packaged commands

- Status: `complete`
- Blocked by: `T06`
- Spec coverage: `R2`, `R10`, `AC7`

## Delivers

Rule authors can validate, describe and expand native libraries on the JVM with the same checked behavior and diagnostic facts as application compilation.

## Acceptance criteria

- [x] Provide packaged JVM authoring entry points for provider-aware validation/description/batch expansion, using the facade rather than a second matcher.
- [x] Return structured and human diagnostics with matching codes, payload/request offsets, definition/token locations and suggestions; preserve aggregate batch failures.
- [x] Describe infinite sources and bounds; show ordered rules/conditions, canonical identities, fingerprint, origins and browser-dependent checks in expansion feedback.
- [x] Preserve evaluated constant annotation locations/spans under current source-map promises. No complete identifier-map promise or scanner is added.
- [x] Validate distribution includes required classes/data and no new external executable/Maven/browser runtime dependency; malformed descriptors fail clearly.

## Validation

- Real command invocations and packaged processor tests, positive/negative batches, provider snapshots, descriptor roundtrips/invalid versions and javac diagnostics.
- Distribution build and artifact tests; verify new module is included under shading/relocation policy.

## Evidence

- The shipped `react4j.css.processor.UtilityRules` entry point supports explicit-file validate/describe and checked-descriptor batch expansion; JSON is default, `--text` is human output. Preparation clearly reports pending provider ownership/import/hook checks. Source/binding processing delegates to the language facade.
- `bazel test //css/utility-language:tests //css/processor/src/test/java/react4j/css/processor:authoring_tests //css/processor/src/test/java/react4j/css/processor:utility_tests --test_output=errors`: passed (307 facade cases), including payload spans, suggestions, token origins, written trace order, mapped Java definitions, contextual errors and malformed descriptors.
- Complete packaged `processor_tests` passed (134.9 seconds); complete processor unit suites and actual integration suite passed. A legacy diagnostic message drift was found by compiler tests, restored, and its complete compiler suite passed again.
- Packaged command, namespace isolation, literal/constant diagnostics and all golden fixtures passed together after the correction (filtered `processor_tests`, 16.8 seconds). The commands execute the actual shaded jar in a new JVM without extra exports, aggregate mixed batch failures and preserve successful peers. Integration was rerun separately without processor-only test arguments and passed.
- `bazel run //css/processor/src/main/java/react4j/css/processor:utility_rules -- --text describe <absolute native.utilities.css path>` passed and reported prepared/pending ownership. Bazel launchers require explicit absolute file arguments from outside runfiles; direct Java invocation resolves ordinary process-relative paths.
- `bazel build //tools/release:maven_artifacts` passed. Java formatting, Buildifier and whitespace checks passed. No extra executable or published Maven artifact was introduced.
