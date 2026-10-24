# T01 — Runnable native teaching app

- Status: complete
- Blocked by: None
- Spec coverage: R1,R2 and local portion of R6; AC1 and local portion of AC2

## Delivers

A separate small app with an explicit native rule source and ordinary Java consumers. Users resize its container, change state and open repeated scoped popovers with an entry transition. Existing app/browser/preview conventions provide the complete path from compilation to a usable page.

## Acceptance criteria

- [x] Native app builds and is reachable through local development/production preview commands.
- [x] Checked Java source uses clear base/state getter composition and a small explicit rule vocabulary.
- [x] Real browser tests verify spacing/colors/state, container resizing, independent anchor scopes, native open/close and keyboard/focus behavior, and an actual entry transition in both build modes.
- [x] Labels, focus styles, reduced motion and narrow layouts work; app presentation remains restrained and readable.
- [x] Explicit sources, formatter inputs and root browser suite registration are complete.

## Validation

- Targeted Bazel app builds and browser tests in development and `-c opt`.
- One batched wide/narrow visual check, fix demonstrated defects, then at most one confirmation pass.
- Formatter, buildifier and shell syntax checks for changed sources/wiring.

## Evidence

- `bazel test //examples/native-utilities/src/test/js/react4j/nativeutilities:browser_tests --test_output=errors`: both browser cases pass (5.2s).
- Same target with `-c opt`: both cases pass (9.1s). Covers generated names, real container layout, state styling, scoped native popovers, flip fallback, focus return and midpoint opacity.
- `verify_accessibility.py`: pinned Chromium 145 with actual reduced-motion media emulation reports 0s transition and no animations. Wide 1200px/narrow 375px captures have no horizontal overflow; screenshots under ignored `reports/screenshots/` inspected together.
- In-app browser: narrowed container, opened panel, Tab reached close control, Escape dismissed it and returned focus to the trigger. No application coordinate/focus runtime.
- Palantir Java formatting, buildifier formatting and shell syntax checks completed; no remaining whitespace errors.
- `tools/examples.sh serve native-utilities development` builds and serves the packaged application. Optimized test builds the production package. Desktop/mobile screenshot inspection found no defects requiring another visual pass.
