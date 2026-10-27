# T04 — Contract, migration and complete verification

- Status: complete
- Blocked by: T03
- Spec coverage: R7, R8, AC4

## Delivers

Canonical accepted input contract, API/reader migration guidance, changelog and a Rose-agent prompt. Complete local gate precedes implementation review and closeout PR.

## Acceptance criteria

- [x] Durable requirements have a canonical owner and validated links.
- [x] Automatic exact-type defaults and custom hook contracts are documented.
- [x] Full gate passes and evidence is recorded.
- [x] Rose prompt describes adoption only; no remote Rose mutation.

## Validation

Domain document link validation, Java/Bazel formatting, site validation/preview as required, and tools/check.sh. Independent implementation review before closeout.

## Evidence

Passed: tools/check.sh (formatting, hermeticity, all 59 normal test targets, 17 optimized browser targets, 27 JVM coverage targets, runtime coverage in both configurations, unchanged coverage thresholds, 27,420 site links, unsigned archive validation). Domain link validation passed. Refreshed local site preview returned HTTP 200 with compiled examples and migration section. Initial coverage harness failure was a fixed render-count assumption: debug-state storage forces an extra mount render; equal-input assertions now use the actual mount baseline while memoization and callbacks stay exact. No coverage policy changes. Rose-agent prompt is supplied as a local handoff at closeout; no Rose changes.

## Knowledge classification

Accepted identity, provider and equality invariants qualify for docs/specs/input-semantics.md: observable requirements independent of generation layout. The reader guide and compiled examples own usage/migration; CHANGELOG.md owns release impact. Encoding layout, provider metadata and generated field naming remain code. Review history and delivery evidence remain this temporary tree. No new domain term, surprising architectural choice or agreed deferred question requires another artifact. The specification-only audit and local link validation passed.
