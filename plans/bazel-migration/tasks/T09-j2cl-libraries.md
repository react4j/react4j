# T09 — J2CL libraries from publication sources

- Status: `pending`
- Blocked by: `T08`
- Spec coverage: `R8`, `R9`, `AC4`, `AC5`

## Delivers

Add public J2CL targets built from assembled source archives, wire native configuration/assets/externs, and compile core/DOM in development and optimized production. Remove GWT library resources as their replacements land.

## Acceptance criteria

- [ ] Both modes compile from the exact publication source archives; runtime flags and JVM-only pruning work; no source-target aliases bypass distribution checks.

## Validation

- Build both-mode core/DOM/browser smoke targets; inspect source-archive inputs and configuration/pruning behavior — proves the delivered behavior. Record actual labels, commands and outcomes below; do not mark complete with skipped required checks.

## Evidence

- pending
