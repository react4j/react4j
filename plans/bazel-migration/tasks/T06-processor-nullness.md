# T06 — Effective processor nullness and input rules

- Status: `complete`
- Blocked by: `T05`
- Spec coverage: `R4`, `R5`, `R6`, `R7`, `AC2`, `AC3`

## Delivers

Implement declaration-owned effective JSpecify contracts throughout input/render/observation/constructor/builder/factory generation, including resolved generic/nested types and correct default/omission diagnostics. Remove redundant typed checks while preserving dynamic props/context invariants.

## Acceptance criteria

- [x] Focused source/compiled inheritance and nested/generic tests pass; nullable AUTODETECT and required nullable semantics remain; invalid optional non-null/default contracts are rejected; parametric contracts are preserved or diagnosed without strengthening.

## Validation

- Run affected golden/diagnostic families and new focused source/compiled/default/omission/dynamic-boundary regressions; regenerate expected outputs using the generator — proves the delivered behavior. Record actual labels, commands and outcomes below; do not mark complete with skipped required checks.

## Evidence

- `bazel test //processor/src/test/java/react4j/processor:{jspecify_goldens,jspecify_diagnostics,nullness_contracts} //core:tests --test_output=errors`: all four targets pass. The model inspector runs against source and compiled declarations; generated nested/array/wildcard/inherited and parametric/presence fixtures pass full checker compilation in both formatting modes.
- Legacy javax/JetBrains nullability is deliberately present only in the resolver regression fixture: marked declarations remain non-null despite legacy nullable; unmarked declarations remain unspecified despite legacy non-null. Both source and compiled inspector tests pass. `-XDaddTypeAnnotationsToSymbol=true` is necessary for javac to expose compiled generic bound annotations; isolated source paths avoid implicit compilation of dependency source entries.
- Generated getters/lifecycle narrowing uses `NativeView.requireInputs()` to check the dynamic React props boundary. The nullable `inputs()` accessor remains available to validation code. Typed builder/constructor argument guards are removed. A private terminal builder method is private when no step exposes build(), resolving EffectivelyPrivate without suppression.
- `tools/update_goldens.sh jspecify`: passes and reports `Updated 0 golden files` on repetition; six new declared output paths per formatting mode were bootstrapped by the generator. `bazel build //:java_format_check` and scoped `git diff --check` pass.
- Full original diagnostic baseline was run: obsolete missing-annotation warnings, one changed boxed-primitive diagnostic, and newly explicit nullable handler parameters require fixture migration. T07 owns that complete-suite work and strict enforcement for every old golden; this task does not claim those families pass.
