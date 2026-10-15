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

## Implementation-review corrections

- Round 1 found that explicit optionality bypassed parametric omission validation and that input/default/handler matching checked only root qualifiers. Reject unsupported parametric omissions for method and constructor inputs before `require=DISABLE`; compare complete effective type trees for defaults, validators and change callbacks. Generated validator locals retain nested qualifiers while omitting meaningless root local-variable qualifiers.
- Real bad-input fixtures cover explicit optional generic method/constructor inputs, nested nullable method defaults (including generic upper bounds), nullable-element array fields, validators and callbacks. The original parametric-AUTODETECT rejection remains. The first added diagnostic test failed before the fix by accepting the optional generic input.
- Source and compiled-declaration inspector cases cover invariant/nested arguments, both wildcard directions, nested wildcard invariance, arrays, enclosing arguments and inherited generic containers. `Types.directSupertypes` demonstrably drops substituted annotations; the matcher binds declared parent arguments instead. The existing source/compiled golden fixture now exercises valid defaults (including subtype, array-field and wildcard defaults) and matching inherited generic handlers with strict generated-body checking in both formatting modes.
- A further source/compiled bound probe failed before the correction (`boundedNullableStrings -> strings` was accepted). The matcher now checks reference/intersection upper bounds instead of treating type variables as leaf types. The real bounded-default rejection fixture and both inspector modes pass after correction.
- Final `tools/check.sh` passed after all review corrections: all 15 targets, optimized browser tests, nine instrumented JVM targets, external packaged/public J2CL consumer, site/release checks and all 13 independent artifact comparisons. The new bad-input cases and both source/compiled inspector modes pass as part of the gate; the source/compiled matched-contract goldens pass strict checking in both formatting modes.
