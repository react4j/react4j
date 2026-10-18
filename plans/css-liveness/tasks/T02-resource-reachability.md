# T02 — Preserve selectors and resource dependencies

- Status: `complete`
- Blocked by: T01
- Spec coverage: R2, R3, R5; AC2, AC3, AC5; AC1 all-symbol/interface cases

## Delivers

Pruning preserves conditional/functional selector behavior and keeps exactly the conservatively required animation/property/global graph, including imported bindings and CSS-only provider chains.

## Acceptance criteria

- [x] Parsed model handles selector lists, nested :is/:where/:not, attributes/strings, conditional rules, peer roots and IDs without introducing false presence or changing specificity.
- [x] Every live checked accessor activates authored owner includes; retained CSS follows animation/property references and immediate providers. Dead rule-exclusive resources disappear. Explicit global roots stay selected; dormant discovered themes remain absent.
- [x] Root presence and owner activation are separate; imported alias, modifier-only and url(#id) cases verified.
- [x] Existing layer/cascade order and provider override order remain; cycles respect prior inclusion rules.
- [x] Exact source-map origins remain correct for selectors/declarations after nested/multiline/UTF-16 deletion.

## Validation

- Heavy parser/pruner/resource-graph unit matrix, metadata/processor goldens where affected.
- Real J2CL optimized and development browser coverage for ID/animation/property/interface calls and shared global variants.
- CSS map consumer assertions; TodoMVC/Drumloop/WebSpeech/HeartRate builds/tests.

## Evidence

- `bazel test //css:tests //:browser_tests`: all 18 targets passed, including raw/standalone processor suites, unit graphs and real source-map integration.
- `bazel test -c opt //:browser_tests`: all 6 targets passed (SaveButton, Drumloop, TodoMVC, Web Speech, Heart Rate, doc examples).
- `tools/update_goldens.sh css`: 508 processor cases passed, 64 metadata/map goldens regenerated, verification passed.
- New real link covers separately compiled interfaces and a nested facade implementation; exact surviving selector/declaration positions resolve to authored Java after deletion of an emoji-containing rule and a selector-list branch.
- Parsed-model units cover resource activation, imported alias providers, dormant themes, supports conditions, local/shared keyframes, peer/ID absence, negation/functions, cycles and ordered overrides. Source-map projection tests cover signed fields, UTF-16 and invalid spans/VLQ.
- Java formatting/buildifier completed; domain glossary clarified CSS-only symbols and specification clarified development retention. Domain link validation passed.
- Delivery hardening found two additional implementation details for T03: bind actual JS bytes to reports, and resolve source paths without a quadratic scan.
