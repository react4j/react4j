# T09 — J2CL libraries from publication sources

- Status: `complete`
- Blocked by: `T08`
- Spec coverage: `R8`, `R9`, `AC4`, `AC5`

## Delivers

Add public J2CL targets built from assembled source archives, wire native configuration/assets/externs, and compile core/DOM in development and optimized production. Remove GWT library resources as their replacements land.

## Acceptance criteria

- [x] Both modes compile from the exact publication source archives; runtime flags and JVM-only pruning work; no source-target aliases bypass distribution checks.

## Validation

- Build both-mode core/DOM/browser smoke targets; inspect source-archive inputs and configuration/pruning behavior — proves the delivered behavior. Record actual labels, commands and outcomes below; do not mark complete with skipped required checks.

## Evidence

- Built `//:core-j2cl`, `//:dom-j2cl`, `//tools/browser:compile_smoke` (ADVANCED production) and `//tools/browser:compile_smoke_dev` (BUNDLE development). Both link successfully from byte-preserving publication-source projections that exclude only separately loaded React UMD assets. Archive checks verify the exact retained entry set and bytes.
- Inspected J2CL strip actions and stripped ReactConfig: only the public source projection is input; JVM System.getProperty overrides are removed. Native companion retains Closure configuration definitions. Production smoke folds to the expected five flags `false:false:true:false:false`; development runtime definitions are carried into T10 browser verification.
- Shared Arez's Braincheck target to avoid duplicate Closure module definitions. Preserved existing module production/development defaults, including Zemeckis console logging.
- Source archive actions now require checked Java class jars. A deliberate invalid Java return was rejected before source packaging; corrected source then compiled. Archive checks and isolated packaged consumer pass; all 13 publication/API artifacts match an independent Bazel build. Buildifier and Java format checks pass.
- Applied user cleanup correction to completed bootstrap peers: removed duplicate processor goldenSmoke/diagnosticSmoke methods, standalone targets and updater alias after full families covered BasicView/FinalComponent. All original processor tests and strict negative control remain. Temporary browser smoke is explicitly retired in T10.
