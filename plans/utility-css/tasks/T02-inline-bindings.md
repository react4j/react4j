# T02 — Bind inline utility styles through Java and linking

- Status: pending
- Blocked by: T01
- Spec coverage: R2, R3, R4, R5 (class ownership), R6 (class exclusions), R7, R11 (basic), AC1 (inline), AC2

## Delivers

A handwritten inline finite utility provider supplies checked method lists to ordinary generated Java accessors, including hybrid component rules, interfaces and peer targets. Existing linker lowering joins all canonical class symbols without a two-token limit.

## Acceptance criteria

- [ ] Add CLASS-retained UtilityCssLibrary, UtilityStyles and method UtilityCss annotations with documented single-provider/nonempty managed-list semantics and existing stateless type/signature checks.
- [ ] Infer provider class identities throughout selectors and exclude explicitly external classes. A nonempty list may select an inferred marker with no standalone declaration.
- [ ] Reject unknown/empty lists, wrong provider kinds, incompatible annotations, duplicate/invalid metadata and invalid inherited overrides during Java compilation, even when a declaration is unused.
- [ ] JVM values and linked JavaScript use exact declared list order plus root/modifier markers; no base utilities or utility merging is implicit.
- [ ] Hybrid Css accessors need no dummy selector; local-only validation, existing typed support accessors, inherited metadata and peer roots remain correct.
- [ ] A real optimized link removes an unused simple utility and consistently compresses a list of more than two classes, activating the provider through binding dependencies.

## Validation

- Processor unit/fixture tests compile emitted Java and verify diagnostics and metadata, including compiled interfaces.
- Internal/linker/bundler tests exercise multi-owner composition, provider activation, full managed inventory and root/modifier behavior.
- Targeted integration link compares JavaScript names with CSS and verifies exact class composition; run affected goldens through tools/update_goldens.sh css.

## Evidence

- pending
