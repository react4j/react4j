# T02: Isolated verification and closeout

- Status: complete
- Blocked by: T01
- Coverage: R3, AC3, AC4

Extend the existing disposable downstream consumer check to copy the real examples unchanged, build every application variant and run their browser tests in both modes. Keep existing positive and negative API/compiler checks. Run the full local gate, simplify, classify durable knowledge and pass independent implementation review. Commit evidence, remove this exact plan tree and push the updated PR without merging.

Validation: `tools/check_bazel_consumer.sh`, `tools/check.sh`, `git diff --check`, independent read-only review.

Evidence:
- `tools/check_bazel_consumer.sh` passes: unchanged copied examples build all twelve application variants; four copied suites plus the native consumer suite pass in both modes; existing denied loads/targets and compiler-error checks pass. Public inventory unchanged.
- `tools/check.sh` passes: 50 default tests, 14 optimized browser suites, 22 JVM coverage suites, unchanged coverage gates, external consumer, 27,032 site links with zero broken, unsigned release validation and independent artifact reproducibility.
- Simplify review completed across the migration, compiler settings, helper consumers, verification script and affected docs; no additional changes needed. `git diff --check`, shell syntax and documentation-link checks pass.
- User follow-up: fetched remote master and verified local/remote master at `adfbc02527d0afed1d4998bf35710821d0d65d34`, already an ancestor of HEAD. Requested `git merge --no-edit origin/master` reports `Already up to date.` No integration changes required.
- Independent implementation review and final closeout are workflow gates tracked in the task map.

Final verification scheduling: after the formatter fix, a full-gate rerun built all downstream variants but three of five simultaneous browser-service startups failed before executing tests. The pinned `rules_webtesting/go/wtl/service/wsl/wsl.go` sets a one-second health deadline. The expanded external check now limits local test jobs to one; fresh verification passes all five suites in both modes and the complete final `tools/check.sh`, including site, unsigned release and all 15 independent artifacts. Third implementation review: Findings: none. No retry or test suppression added.
