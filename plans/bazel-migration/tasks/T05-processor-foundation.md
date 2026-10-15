# T05 — Processor Proton and golden baseline

- Status: `complete`
- Blocked by: `T04`
- Spec coverage: `R1`, `R3`, `R4`, `R7`, `AC2`, `AC3`

## Delivers

Upgrade processor infrastructure to the pinned Proton API and JSpecify, adapting the existing test families into Bazel golden/diagnostic targets without losing no-Sting or formatter subprocess coverage. Preserve test inventory and deliberate regeneration.

## Acceptance criteria

- [x] Processor compiles under reference checking. Every existing relevant fixture/test is inventoried and has a migrated owner or explicit obsolete-GWT disposition; formatted/unformatted expected output is regenerated through bounded commands.

## Validation

- Compile processor; run representative positive/negative golden families plus no-Sting/formatter subprocess tests; record fixture inventory and ownership — proves the delivered behavior. Record actual labels, commands and outcomes below; do not mark complete with skipped required checks.

## Evidence

- Mechanical formatting checkpoint: equivalent Java token comparison passed for 21 maintained processor main/test files; original fixtures/test inventory captured before migration.

- `bazel build //processor` and the maintained test-class target: passed under the complete reference compiler policy against pinned Proton. Owned processor nullness uses JSpecify; generated roots are explicitly marked. The Arez nullness resolver replaces removed legacy Proton queries.
- `tools/update_goldens.sh smoke`: passed; unformatted/formatted generated file sets and exact contents verified. Repeated update reports `Updated 0 golden files`.
- `tools/update_goldens.sh regressions`, followed by the strengthened `bazel test //processor/src/test/java/react4j/processor:regressions`: passed. Preserved no-Sting classpath compilation in both modes and missing-JDK-exports subprocess diagnostics. No-Sting verification now also checks complete own output sets and cleans temporary compilation directories.
- `bazel test //processor/src/test/java/react4j/processor:smoke_diagnostics`: passed. Full generated/diagnostic families are declared with all 38 original TestNG declarations owned; their semantic completion is explicitly T06/T07.
- Original 1461 fixture paths remain (351 input, 158 invalid, 476 expected per mode). Explicit Bazel file groups declare them; see processor-coverage.md and baseline inventories. Fixture annotation conversion preserves outer nullable array placement and marks maintained examples; unknown-contract regression expectations are replaced in T06/T07.
- Golden updates clone runfiles without symlinks into a temporary directory. A Java copy tool preflights all destinations; a negative probe confirmed an undeclared output is rejected before any maintained file changes. Bash syntax check passed.
- `bazel build //:java_format_check`, buildifier check and `git diff --check`: passed. Full generated-body checker enforcement and remaining golden regeneration are T07; default/omission diagnostics and resolved/nested nullness are T06.
