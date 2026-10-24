# Knowledge routing audit

Readers: maintainers reviewing the extraction and its canonical owners. Source:
`165ef456a^:docs/design/utility-language.md` (733 lines), its extraction commit
`165ef456a`, current contracts, grammar and real compiler/browser checks.

## Classification before specification edits

| Conclusion | Single owner and disposition |
| --- | --- |
| JVM-only marker dispatch, immutable preparation/binding/expansion | Existing native specification requirements 1–4; preserved, no duplicate artifact. |
| Sources, types, bounds, overlap and longest-prefix matching | Existing native specification 5–11; precise current notation and examples in public rule reference. |
| Case-sensitive, separate utility/variant namespaces | Native specification: qualifying omission from old design. Accepted lexical invariant; conforming replacement must preserve distinguishable requests and same-spelling cross-namespace definitions. Independently verifiable through compiler results, independent of implementation structure. No previous canonical removal/supersession found. |
| Empty/duplicate expansion requests fail | Native specification: qualifying omission from old design. Accepted input invariant; conforming replacement must preserve diagnostic failure for these batches. Independently verifiable via facade/CLI, independent of code structure. Java lists' corresponding invariant is clarified in existing bindings requirement 13. |
| Header quoting, identifier spelling, Java escaping and scale notation | Author reference; current concrete language syntax/instructions, verified with compiler. No additional artifact. |
| Selector/variant composition and deterministic cascade | Existing native specification 12,15,16; broad-before-axis guidance and examples in author reference. |
| Containers, starting styles, anchors and explicit DOM setup | Existing native specification 13–19 and names contract; portable recipes demonstrate the obligations. |
| Downstream requests, fingerprints and generic metadata boundary | Existing native specification 20–23, naming/liveness/maps contracts; realization stays in module README/code. |
| CSS-oriented declarative format, narrow internal package and JVM execution rationale | Existing ADR0005; no new architecture choice. |
| Checked provider plus separate consumer contributions | Existing ADR0006; no duplicate decision. |
| Current scoped-symbol, utility-library, utility-candidate and utility-style meanings | Existing glossary headings; no new domain term resolved. |
| CLI arguments, descriptor provenance and build paths | Author guide plus owning CLI/build code; mutable setup is not a specification requirement. |
| Rose table/shadow/negative-margin/apply/default-anchor/popover examples | Portable user recipes; no Rose migration or claim that Rose defects are fixed. |
| Consumer-first learning, public navigation, short Tailwind comparison and runnable app | User documentation/example implementation; reversible delivery choices fail ADR threshold and are not compiler requirements. |
| Browser layout, focus, reduced motion and input behavior in teaching app | App source/tests and user guide; native specification 19 already separates compiler guarantees from application/browser behavior. |
| Exact identifier-token and JS-to-CSS attribution | Existing deferred records; no trigger resolved by documentation work. |
| Unsupported plugins, named position-try resources and dynamic names | Existing native specification scope boundary; no new deferred commitment. |
| Proposed APIs, nonexistent-package claims, feasibility tasks and delivery sequencing | Retired design/history and this temporary plan; do not restore as current documentation. |

Both specification additions satisfy acceptance, durability, implementation independence,
and absence of another canonical normative owner. The replacement test preserves the
observable language contract, not a current package, algorithm, CLI spelling or test.

## Verification

- Original extraction and relevant specification history inspected; no deliberately removed requirement is reintroduced.
- Namespace and duplicate behavior checked against RuleLibrary and UtilityLanguage plus CLI probes.
- Specification-only diff audit completed: each changed contract statement preserves observable source/request identity or admission, independent of implementation. The author-reference link now names its public canonical home. No tooling path, delivery sequence, future possibility or rationale was added to a requirement. Existing glossary first-use links remain valid; domain link validation passes.
