# T01 — Runnable native teaching app

- Status: pending
- Blocked by: None
- Spec coverage: R1,R2 and local portion of R6; AC1 and local portion of AC2

## Delivers

A separate small app with an explicit native rule source and ordinary Java consumers. Users resize its container, change state and open repeated scoped popovers with an entry transition. Existing app/browser/preview conventions provide the complete path from compilation to a usable page.

## Acceptance criteria

- [ ] Native app builds and is reachable through local development/production preview commands.
- [ ] Checked Java source uses clear base/state getter composition and a small explicit rule vocabulary.
- [ ] Real browser tests verify spacing/colors/state, container resizing, independent anchor scopes, native open/close and keyboard/focus behavior, and an actual entry transition in both build modes.
- [ ] Labels, focus styles, reduced motion and narrow layouts work; app presentation remains restrained and readable.
- [ ] Explicit sources, formatter inputs and root browser suite registration are complete.

## Validation

- Targeted Bazel app builds and browser tests in development and `-c opt`.
- One batched wide/narrow visual check, fix demonstrated defects, then at most one confirmation pass.
- Formatter, buildifier and shell syntax checks for changed sources/wiring.

## Evidence

Pending.
