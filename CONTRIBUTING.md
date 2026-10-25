# Contributing to React4j

Follow the [Code of Conduct](CODE_OF_CONDUCT.md). Describe the problem, resulting behavior and relevant verification in a pull request. Add user-facing changes, including processor diagnostics and generated APIs, under `Unreleased` in [CHANGELOG.md](CHANGELOG.md). Keep logical changes in separate commits.

## Build and checks

Use Bazel 9.3.1 (pinned in `.bazelversion`, typically selected by Bazelisk) and Bash. Bazel downloads the Java toolchains, compiler dependencies, React assets and Chromium. Owned Java sources, publication bytecode and generated sources target Java 21; annotation processing requires Java 21 or later; build tools use the pinned Java 25 toolchain. Dependency maintenance uses a host Java 17+ command.

| Task | Command |
| --- | --- |
| Complete local/CI gate | `tools/check.sh` |
| Product archives | `bazel build //tools/release:maven_artifacts` |
| Core and DOM dependency checks | `bazel test //core:tests //dom:tests` |
| Processor tests | `bazel test //processor:all_tests` |
| Distribution and bundle tests | `bazel test //tools/release:tests` |
| Browser tests, development | `bazel test //:browser_tests` |
| Browser tests, optimized production | `bazel test -c opt //:browser_tests` |
| Format Java / check Java | `tools/java_format.sh write` / `tools/java_format.sh check` |
| Format Bazel / check Bazel | `bazel run //tools/buildifier:buildifier` / `bazel run //tools/buildifier:buildifier_check` |
| Review hermeticity | `bazel run //tools/ahab:hermeticity.check` |
| Update Java dependencies | Edit `third_party/java/dependencies.yml`, then `tools/update_java_deps.sh` |
| Preview example | `tools/examples.sh serve [hello_world\|drumloop\|heart-rate-monitor\|todomvc-raw\|todomvc-arez\|todomvc-sting\|native-utilities\|utility-css\|webspeechdemo] [development\|production]` |
| Build / check / serve site | `tools/site.sh build` / `tools/site.sh check` / `tools/site.sh serve` |

The complete gate runs local verification only. Publication commands are separate and documented in the [release guide](tools/release/README.md). Encrypted `etc/secrets` is retained untouched and is not needed for builds or checks.

## Java and nullness

Use the checked-in formatter and the complete Error Prone/NullAway policy in `examples/checks.bzl`. Mark owned packages/types with JSpecify `@NullMarked`; use JSpecify `@Nullable` for values that may be null. Legacy nullness annotations do not define processor contracts. Prefer specified contracts over suppressions. Keep runtime checks that establish a fact about nullable or JavaScript data; remove argument checks already proven by the static contract.

The processor distinguishes required builder calls from nullable values. Nullable AUTODETECT inputs are optional; explicitly required nullable inputs require a call and accept null. Optional non-null inputs need a non-null default or tree-context source. Parametric type variables preserve their conditional nullness; generation diagnoses missing evidence where it cannot safely initialize or omit a value. See [Project Setup](docs/project_setup.md) for consumer compiler configuration.

## Generators and fixtures

Edit generator sources and regenerate expected output; do not hand-edit generated Java. DOM templates and Java generators are in `tools/generation/`. React and ReactDOM inputs are checksum-pinned in `MODULE.bazel` and patched by the Java asset tool.

Processor fixtures are in `processor/src/test/fixtures`. The harness checks complete output sets and exact formatted/unformatted goldens, then compiles every emitted body with the full checker policy, including integrated Arez/Sting output. Update only the affected family with `tools/update_goldens.sh generated`, `jspecify` or `regressions`. The updater protects concurrent edits, copies only changed candidate outputs and reruns the family. Review the resulting diff, including diagnostics and file additions/removals. CSS fixtures use `css/processor/src/test/fixtures` and `tools/update_goldens.sh css`, with paired formatted/unformatted goldens covering generated Java, CSS resources and diagnostics.

## Browser and distribution boundaries

Public `//:core` and `//:dom` provide J2CL libraries derived from the complete publication source archives; `//:css` supplies CSS annotations. `//:processor` and `//:css_processor` are annotation processor plugins. Root `//:defs.bzl` re-exports the public build rules, macros and providers. Curated dependency aliases in `//third_party` share upstream library compilations and offer optional processors/checkers. Implementation targets and Starlark modules restrict access to repository consumers. See the [Bazel integration contract](docs/specs/bazel-integration.md).

Examples use native `j2cl_library` with explicit checker settings from `examples/checks.bzl`, public React4j labels and example-owned test support. The formatter aspect checks their authored Java sources directly. Repository browser tests use the checked `j2cl_library` in `third_party/java/rules.bzl` with the same policy. Neither requires a companion JVM compilation. Paired JVM/J2CL builds remain for repository artifacts with JVM consumers. Publication source archives depend on checked Java 21 class outputs, and bytecode dependency checks inspect those outputs without executing the runtime. Downstream applications use native J2CL with their own checker selection. CSS linking requests J2CL's existing javac outputs as validations so JavaScript-only builds run those checks.

J2CL publication inputs exclude only separately loaded UMD asset directories; Java sources, native configuration and extern bytes are preserved.

Keep React/ReactDOM versions and browser mode switches aligned with the runtime and asset checks. Development uses BUNDLE plus runtime Closure defines; `-c opt` selects ADVANCED production. Browser tests load the real pinned runtime assets and verify rendering, generated inputs, context and configuration in both modes.

Write browser scenarios and assertions in checked Java/J2CL, called by thin Closure test-suite modules. Keep native JavaScript for browser/service instrumentation, generated-overlay discovery and interop fixtures. Compare observations in Java using explicit assertion failures that remain active in optimized builds.

## Coverage

The full gate enforces [coverage.json](tools/verification/coverage.json), including per-class overrides. It writes `reports/coverage/jvm.lcov`, `runtime.lcov` and `summary.txt`.

Core and DOM execute in pinned Chromium with real React and Arez assets, in both runtime configurations. Their coverage bundle preserves every owned J2CL module without optimization or pruning. Publication class archives supply the complete Java line and method inventory without loading or executing runtime classes; source maps and parsed Java expressions/callables establish browser execution. Every runtime hit comes from Chromium. Ambiguous or unmapped execution remains uncovered. Other configured roots use JVM coverage directly.

Browser branch obligations cover conditions, ternaries, short circuits, assertions, loop exits and switch arm entries, including fallthrough and unmatched defaults. These source decisions have stable identities independent of JVM branch numbering. Missing modules, source maps, declared sources, method inventories or counters fail collection. When investigating a coverage failure, retain the raw reports and verify attribution before changing tests or rules.

## Documentation and releases

Reader documentation lives in `docs/`; Java/CommonMark rendering, templates and navigation live in `tools/site/` and `website/`. Snippets come from checked example source jars. Site generation validates local links, assets, fragments, navigation and snippet boundaries. Rebuild and refresh the local preview after edits. Site deployment replaces published content with the generated site and preserves Git metadata.

Before signing or publishing, follow the [release guide](tools/release/README.md), verify the migrated Arez Maven prerequisite and review the exact tag and bundle. Keep validation independent of credentials and remote mutation.
