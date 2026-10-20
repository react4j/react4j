# Rose convergence

- Status: complete
- Blocked by: T02
- Spec coverage: R3/AC3

## Delivers

Route isolated Rose and vendored Replicant to canonical source owners, including standalone module configuration. Consume new public aliases. Verify actual Acal/Rose links and unchanged downstream packaging/local boundaries.

## Acceptance

- [x] Assigned spec outcome is demonstrably satisfied.
- [x] Validation passes with actual evidence and any independent limits recorded.

## Validation

Affected Replicant gate; both apps BUNDLE and ADVANCED; seven delivery tests and compression-disabled fixture; local/production action graphs; WAR/browser checks where supported.

## Evidence

Rose commits a1eac472fe7 and 9b17ff58a55 route shared dependencies through native owners and public aliases, with standalone Replicant patches. Standalone Replicant tools/check.sh passes (98 tests, release validation and optimized browser smoke). Both Acal/Rose development bundles and all four production/diagnostic applications build and package without duplicate-module diagnostics. Seven delivery tests pass; compression-disabled BrowserApplicationDeliveryTest and //server:rose_war pass.

Configured local graph contains exactly two BUNDLE ClosureLink actions and one RoseLocalDevelopmentAssets action, with no Brotli/WAR/service-worker actions. Production WAR graph has 26 BrotliCompress and one BrotliCompressTree; disabled graph has none. After initial adapter warmup, local build completes in 0.248s, critical path 0.00s, with an empty execution log. Temporary evidence is in evidence/rose-*.log/json and evidence/replicant-full-check.log. Commands use the local React4j module override until publication; final remote pin verification belongs to T04. Full authenticated Rose UI and deployment are outside this check; no credentials or main Rose checkout were used.
