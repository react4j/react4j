# T01 — Expand checked rules through the isolated JVM facade

- Status: `complete`
- Blocked by: `None`
- Spec coverage: `R1`–`R3`, base `R5`, `AC1`

## Delivers

A real pure-JVM compiler facade in its own package that prepares, validates/binds, describes and expands literals, spacing/color families and selector/condition variants with immutable descriptors and structured origins/errors.

## Acceptance criteria

- [x] Establish explicit Bazel package/visibility/dependencies, JVM tests and no reverse processor dependency.
- [x] Parse marker, tokens/scales, literal/family heads, source alternatives, template declarations/holes, supporting CSS and one-slot variants. Check all definitions, collisions and forbidden syntax.
- [x] Split candidates with balanced CSS quoting/brackets/escapes; decode underscores; match exact/longest-prefix; preserve explicit negative rules and written selector order/specificity.
- [x] Expand representative Rose spacing/colors/nth/descendant forms and static media/supports conditions; reject unguarded selectors, malformed values and hidden declaration injection.
- [x] Describe finite/infinite domains; preserve code/origin/related locations and deterministic serialized descriptors. Reuse does not retain earlier candidates; bad versions fail.
- [x] Focused tests prove typed-hole parsing/flattening and supporting-origin feasibility through the actual facade, not a throwaway parser.

## Validation

- `bazel test //css/utility-language:tests --test_output=errors` — exercises the external facade with positive and negative cases, batch failures, roundtrip/version checks and order-independent compilation.
- Narrow formatter/buildifier/checker checks and package dependency audit.

## Evidence

- `bazel test //css/utility-language:tests //css/internal/src/test/java/react4j/css/internal:tests //css/processor/src/test/java/react4j/css/processor:utility_tests --test_output=errors` — all three targets passed; eight facade methods cover Rose values, order-sensitive selector flattening, declaration runs, support CSS, descriptors, batch errors, escapes and rejection paths.
- Re-ran the facade target after lexical/diagnostic tightening: passed.
- `bazel run //tools/buildifier:buildifier_check` and `bazel build //:java_format_check` — passed.
- `bazel query 'deps(//css/utility-language/src/main/java/react4j/css/utility/language:language)'` — no processor/linker/bundler dependency. Canonical class encoding was extracted to shared `CssIdentity` without changing finite behavior.
- Broader type recognition/ranges, binding snapshots and container/anchor semantics remain their planned task-owned work, rather than claims of this base facade.
