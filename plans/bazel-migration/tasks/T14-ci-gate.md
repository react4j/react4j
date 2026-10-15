# T14 — Shared CI gate and full verification

- Status: `complete`
- Blocked by: `T13`
- Spec coverage: `R1`, `R3`, `R7`, `R8`, `R9`, `R10`, `R11`, `R12`, `R13`, `AC1`, `AC2`, `AC3`, `AC4`, `AC5`, `AC6`, `AC7`, `AC8`

## Delivers

Provide shared local/CI check entrypoint and GitHub Actions workflow, finalize reviewed Ahab reproducibility/sandbox declarations and coverage report checks, run the strongest full gate and inspect the complete change before implementation review.

## Acceptance criteria

- [x] Formatting/buildifier/Ahab, all JVM/processor tests and coverage, archive/consumer checks, both browser modes, site and unsigned release checks pass. CI runs the same gate; obsolete infrastructure is absent; full evidence is recorded.

## Validation

- tools/check.sh; final clean-checkout/readiness checks appropriate to the gate; diff inspection and task/artifact linkage audit — proves the delivered behavior. Record actual labels, commands and outcomes below; do not mark complete with skipped required checks.

## Evidence

- `tools/check.sh` passes on the final implementation, ending `All React4j checks passed.` It checks Bazel 9.3.0, buildifier/Java formatting, Ahab in both compilation modes, product/assets/publication builds, all 15 test targets, optimized browser tests, JVM coverage, a fresh external Bazel consumer, site build/links, unsigned release validation and independent archive repeatability. Repeated after the portal extern correction; the final full gate passed.
- Browser suite: four real cases per mode, covering DOM render/update/context/configuration plus portal/batched rendering and unmount cleanup. A native-API audit deliberately reproduced createPortal's optimized wrong-arity failure before correcting its existing two-argument Java boundary. No global compiler policy relaxation.
- JVM coverage: all nine owned test targets pass under instrumentation. Real LCOV summary: core 47 sources, 113/278 lines and 28/90 branches; DOM 69 sources, 19/1780 lines and 14/20 branches; processor 22 sources, 2795/2893 lines and 1319/1500 branches. No invented percentage floors. Java report checker rejects empty/missing/invalid owned data, external dependency substitution and stale summaries; one-off negative probes were removed.
- Ahab initially found host-shell asset actions plus Chromium's runner boundary. Replaced shell copy/concatenation actions with deterministic Java BrowserAssets. Final Ahab checks pass with an empty violation baseline and exactly two target-specific Chromium exceptions (native launcher and declared HTTP route). Site tool is reproducible only for `build`; other build programs have reviewed deterministic declarations.
- Fresh disposable external consumer passes real JVM/processor/Sting integration and public J2CL/example builds in development/production. Its workspace and independent Bazel output base are both removed by the successful cleanup trap. Site checks 26,925 local file/fragment links with zero broken. Unsigned release validates 12 real artifacts/60 entries. All 13 publication/API artifacts match a second independent output base; its temporary state is removed and primary output links restored.
- GitHub Actions workflow loads as YAML, uses the exact shared gate, read-only permissions, disabled checkout credential persistence and commit-pinned actions. Verified official tag refs and peeled the annotated setup-bazel tag to its actual commit. Remote Linux CI is configured but was not executed because branch publication is outside authority; local macOS gate is complete.
- Bash syntax, complete current diff/secret-path checks and maintained-source formatting pass. No domain-model directories exist; durable conclusions are in the existing contributor/agent/release/setup guidance. Ready for the fresh read-only implementation review.
