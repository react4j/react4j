# T06 — Source content and map fidelity

- Status: `pending`
- Blocked by: T05
- Spec coverage: R8; AC6.

## Delivers

Debuggers resolve compressed CSS to authored Java and optimized JS style values to generated binding Java with complete source content, including nested generated implementation names.

## Acceptance criteria

- [ ] Generated binding companion maps are enriched before normal Closure composition/reset; follow actual source entries, support nested facades, and affect only catalogue-matched binding units.
- [ ] Actual optimized maps retain generated Java origins and full generated Java `sourcesContent`; development maps retain valid contents too.
- [ ] Replacement projection preserves CSS authored selector/declaration anchors and full source content after removal and variable-length edits, including unmapped spans.
- [ ] Concrete map queries before/inside/after replacements cover tabs, UTF-16, CRLF, multiline/nesting, imports, property/animation/fragment edits, and concatenated bundle sections.
- [ ] No accidental adoption of deferred exact-token or JS-direct-to-authored-CSS semantics; errors/missing data fail meaningfully where required.

## Validation

- SourceMap unit tests with real position decoding and original source content assertions.
- Linker companion-map lifecycle tests plus real J2CL/Closure map consumer queries using output positions, not JSON-only validation.
- Bundle map queries across pruning/replacement/layer boundaries in actual example artifacts.

## Evidence

Pending.
