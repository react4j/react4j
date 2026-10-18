# T02 — Preserve selectors and resource dependencies

- Status: `pending`
- Blocked by: T01
- Spec coverage: R2, R3, R5; AC2, AC3, AC5; AC1 all-symbol/interface cases

## Delivers

Pruning preserves conditional/functional selector behavior and keeps exactly the conservatively required animation/property/global graph, including imported bindings and CSS-only provider chains.

## Acceptance criteria

- [ ] Parsed model handles selector lists, nested :is/:where/:not, attributes/strings, conditional rules, peer roots and IDs without introducing false presence or changing specificity.
- [ ] Every live checked accessor activates authored owner includes; retained CSS follows animation/property references and immediate providers. Dead rule-exclusive resources disappear. Explicit global roots stay selected; dormant discovered themes remain absent.
- [ ] Root presence and owner activation are separate; imported alias, modifier-only and url(#id) cases verified.
- [ ] Existing layer/cascade order and provider override order remain; cycles respect prior inclusion rules.
- [ ] Exact source-map origins remain correct for selectors/declarations after nested/multiline/UTF-16 deletion.

## Validation

- Heavy parser/pruner/resource-graph unit matrix, metadata/processor goldens where affected.
- Real J2CL optimized and development browser coverage for ID/animation/property/interface calls and shared global variants.
- CSS map consumer assertions; TodoMVC/Drumloop/WebSpeech/HeartRate builds/tests.

## Evidence

- Pending.
