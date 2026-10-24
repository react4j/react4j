# T02 — Consume native rules through checked Java declarations

- Status: `pending`
- Blocked by: `T01`
- Spec coverage: `R1`, `R2`, `R6`, `AC2`

## Delivers

Application authors request native candidates through existing checked Java APIs, using inline or explicitly packaged native sources and published provider metadata.

## Acceptance criteria

- [ ] Route native/finite sources by marker without fallback; validate unused provider definitions, tokens/imports/external permissions and owned name universes.
- [ ] Package native prepared-rule sidecars instead of pretending an infinite library has a finite classes.json inventory. Bind/check provider snapshots before expansion; preserve source origins.
- [ ] Generate normal utility class values and local markers for evaluated constants, inherited/replaced annotation metadata and local CSS hybrids.
- [ ] Publish/load checked descriptors across independent provider/consumer and header-resource paths; downstream candidates need not be known at provider compilation.
- [ ] Keep finite CSS and source declaration validation behavior, packaged processor isolation and no application scanning.

## Validation

- `bazel test //css/processor/src/test/java/react4j/css/processor:utility_tests //css/processor/src/test/java/react4j/css/processor:input_jar_tests --test_output=errors` with native cases added.
- Packaged `processor_tests` and integration javac/J2CL cases for real independent/header compilations.

## Evidence

- `pending`
