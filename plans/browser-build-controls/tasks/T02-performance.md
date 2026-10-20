# Development overhead and cache boundaries

- Status: complete
- Blocked by: T01
- Spec coverage: R3 / AC3

## Delivers

Measure available real Rose baseline payload and new post-link costs, profile unnecessary scanning/parsing/preparation, then implement only safe measured reductions. Check warmed no-op, Java/CSS/asset edits on real public consumer fixture and record blocked real Rose parity gate.

## Acceptance

- [x] Delivered behavior satisfies assigned spec criteria.
- [x] Meaningful verification passes and limitations are explicit.

## Validation

Profiles/action counts; before/after post-link measurements; negative cache controls; source-map/token regression suites.

## Evidence

Real existing Acal/Rose BUNDLE payloads were 37,561,270 / 144,922,513 bytes. In-process finalization with no managed bindings (same bytes, Java 6 GiB heap, no file I/O or JVM startup in the timed section) changed from 1.902 / 4.717 seconds to 0.050 / 0.115 seconds. This measures the avoided whole-bundle parse, not whole-application iteration or a migrated stylesheet population.

Warm public application fixture, recorded execution logs (all listed actions actually executed, no disk/remote hits): no-op 0.249 s and zero actions; image edit 1.171 s with PrepareWebAsset/WebBundle/BrowserApplication only; shared CSS edit 1.452 s with CssBundle/WebBundle/BrowserApplication only; Java edit 2.557 s with Javac/J2cl/Closure/ClosureLink/CssCatalogue/CssBundle/WebBundle/BrowserApplication. Inputs restored after each probe. These small-fixture timings include Bazel execution-log collection and are not Rose latency parity evidence.

`bazel test //css/bundler/src/test/java/react4j/css/bundler:asset_tests //css/linker/src/test/java/react4j/css/linker:tests //tools/verification/src/test/java/react4j/verification:application_tests --test_output=errors` passed, including source-map origins, complete Java content, authored lookalikes, strict directives, capture scopes, missing/duplicate inputs and reserved-token validation. Formatter passed. Full Rose linking/iteration remains a migration gate blocked by independently compiled shared libraries; recheck under T03.
