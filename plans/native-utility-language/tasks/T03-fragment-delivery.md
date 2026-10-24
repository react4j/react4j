# T03 — Link independently compiled candidate fragments

- Status: `complete`
- Blocked by: `T02`
- Spec coverage: `R7`, integration `R6`, `AC3`

## Delivers

Independently requested candidates become one deterministic, named, prunable stylesheet at complete link without exposing native grammar to the bundler.

## Acceptance criteria

- [x] Emit generic candidate fragments with full provider identity, descriptor fingerprint, candidate spelling, order key, CSS/origins and symbol/support dependencies.
- [x] Collect from real compilation trees, deduplicate matching requests, reject conflicting fingerprints/content and unsupported metadata versions, and assemble provider layers in the accepted total order.
- [x] Keep shared support definitions once; preserve tokens, source-map origins, selected assets/imports and canonical class identities across consumer compilation order.
- [x] Integrate link naming/validation and development retention; production removes candidates referenced only by dead getters and preserves local precedence.
- [x] Missing fragments/metadata fail rather than generating an incomplete sheet or silently enumerating a family.

## Validation

- Internal/linker/bundler tests with two independent compilation outputs, duplicate candidates, descriptor mismatches, multiple CSS variants and exact source/name metadata.
- Native J2CL development and ADVANCED liveness integration through existing build rules; finite regression suites.

## Evidence

- Internal, linker, bundler and real J2CL integration targets passed. Two distinct native consumer targets share `gap-137` and `pulsing`; complete output emits each once, preserves total rule order, maps back to the authored CSS and retains owned/imported animations and an imported property. ADVANCED output removes `gap-999` used only by a dead getter; development retains requested expansions without inventing unrequested numeric values.
- Generic collection tests use actual independent resource trees. They prove order-independent collection, canonical getter/name union, source-map/occurrence shifts, fingerprint/content conflicts with contributing origins, missing fragments/providers and malformed/old schemas.
- `bazel test //css/processor/src/test/java/react4j/css/processor:processor_tests --test_arg=-methods --test_arg=react4j.css.processor.CssProcessorTest.goldenFixtures --test_output=errors` — all packaged golden cases passed after the metadata version 11 hard cut. Updated finite goldens add the explicit empty fingerprint; there is no old-schema reader.
- Facade tests passed, including distinct fingerprints when an unrequested definition changes while the requested CSS stays equal.
- Buildifier, root Java formatting and diff whitespace checks passed. Bazel `somepath` queries from both linker and bundler libraries to the language target returned no path.
- Shared CSS resources carry the fingerprint and generic fragments carry candidate/order/CSS/map/name data. The language stays out of complete-link collection, naming and pruning.
