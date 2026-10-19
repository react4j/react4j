# T04 — Example scenarios and full delivery

- Status: `complete`
- Blocked by: T02,T03
- Spec coverage: R1,R2,R3,R4,R5,R6; AC1,AC2,AC3,AC4

## Delivers

Migrate Drumloop, Heart Rate Monitor, all TodoMVC variants and Web Speech Demo scenarios into Java. Retain native external-service instrumentation, moving application expectations into Java. Preserve real resource loading, timing, transitions and teardown. Update permanent contributor policy, simplify the whole migration, run full gates, promote qualifying knowledge and pass implementation review.

## Acceptance criteria

- [x] Delivered behavior and relevant spec criteria preserve the original cases.
- [x] Focused checks and formatting pass; the Java/native boundary is inspected.
- [x] Evidence and task state are committed together.

## Validation

All affected example browsers in both modes, CSS variants, complete tools/check.sh including unchanged coverage, external consumer/site/release/reproducibility; native/assertion boundary audit and final canonical link validation.

## Evidence

- All four example suites pass in development and ADVANCED production, including raw/Arez/Sting TodoMVC. Original runner case names retained (Drumloop 2, HRM 3, TodoMVC 3, Speech 3, CSS 16), with the original scenario assertions inspected against 424077f39.
- `tools/check.sh` passed: 45 default tests, 8 optimized browser targets, 23 JVM coverage targets, runtime collector in both environments, unchanged coverage floors, hermeticity, external Bazel consumer in both modes, site/release validation and all 15 independently rebuilt publication/API/CSS artifacts.
- Final CSS runner configuration passed all three variants in both modes after the full gate. Prior Linux CI logs showed a 1007 ms harness timeout while loading the second packaged page; the runner now allows 30 seconds while each render keeps its original 10-second limit. One local WebDriver HTTP-501 startup failure before test execution passed on an isolated retry with identical source.
- Simplify completed: native helpers expose only observations/operations, removed the unused error-observer option, kept frame sizing on Frame, narrowed test visibility and kept constructor/helpers private. Java owns Bluetooth service expectations, utterance type checks and the cache throw expectation. Native observation keys cross separately compiled iframe boundaries verbatim; optimized tests verify them.
- Complete PR JavaScript audit retained only thin runners, native browser/service/overlay operations and deliberate measurement/link fixtures. No new production browser dependencies or coverage obligations; no Node dependency.
- Java formatter, buildifier, diff checks and domain document-link validation pass. Contributor test-language policy is the sole durable documentation change; canonical classification is in TASKMAP.md.
