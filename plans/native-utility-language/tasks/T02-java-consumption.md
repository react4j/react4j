# T02 — Consume native rules through checked Java declarations

- Status: `complete`
- Blocked by: `T01`
- Spec coverage: `R1`, `R2`, `R6`, `AC2`

## Delivers

Application authors request native candidates through existing checked Java APIs, using inline or explicitly packaged native sources and published provider metadata.

## Acceptance criteria

- [x] Route native/finite sources by marker without fallback; validate unused provider definitions, tokens/imports/external permissions and owned name universes.
- [x] Package native prepared-rule sidecars instead of pretending an infinite library has a finite classes.json inventory. Bind/check provider snapshots before expansion; preserve source origins.
- [x] Generate normal utility class values and local markers for evaluated constants, inherited/replaced annotation metadata and local CSS hybrids.
- [x] Publish/load checked descriptors across independent provider/consumer and header-resource paths; downstream candidates need not be known at provider compilation.
- [x] Keep finite CSS and source declaration validation behavior, packaged processor isolation and no application scanning.

## Validation

- `bazel test //css/processor/src/test/java/react4j/css/processor:utility_tests //css/processor/src/test/java/react4j/css/processor:input_jar_tests --test_output=errors` with native cases added.
- Packaged `processor_tests` and integration javac/J2CL cases for real independent/header compilations.

## Evidence

- `bazel test //css/processor/src/test/java/react4j/css/processor:utility_tests //css/processor/src/test/java/react4j/css/processor:input_jar_tests //css/utility-language:tests --test_output=errors` — passed.
- Packaged `processor_tests` and real `integration_tests` — all passed (packaged suite 164.9s). Native cases cover evaluated annotation constants, inheritance/hybrids, provider symbol accessors, unused definitions/tokens/hooks, distinct prepared sidecars and separate downstream requests. Packaged relocation/isolation checks passed.
- Actual Bazel header jars contain the provider annotations without generated CSS resources. Header consumers bind the same checked snapshot from those annotations plus explicitly packaged prepared inputs; full J2CL emits the published checked descriptor. The integration test exercises both boundaries and rejects an unused bad candidate.
- After removing the temporary Names forwarding class and adding stale-sidecar rejection, all ten facade/compiler/unit/utility targets passed. Final facade/utility rerun passed after closing a synthetic-probe hook exemption.
- Final missing-descriptor test passed: an incomplete full provider fails; a genuine header without CSS resources rebuilds its snapshot. Stale prepared sidecars fail against changed input bytes.
- `bazel run //tools/buildifier:buildifier_check`, `bazel build //:java_format_check` and `git diff --check` — passed.
- Shared annotation-free binding code now serves finite compilation and the native facade. Native snapshots check all templates/tokens and keep static owned-name registries while allowing explicit external permissions to be first used downstream. Fragment collection/liveness remains T03.
