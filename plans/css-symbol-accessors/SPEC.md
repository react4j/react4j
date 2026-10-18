# CSS symbol accessors

## Source and authority

The user requested implementation in this thread, beginning with grill-me. The final interview summary was explicitly accepted with “accept all”. Earlier rounds accepted implicit/explicit names, all-local custom properties and property imports. Implementation and push to existing PR #112 are authorized; no human plan checkpoint was requested. The checkout began clean at d121bb329 on codex/css-in-java-proof. Applicable rules are AGENTS.md, CONTRIBUTING.md and css/README.md.

## Required outcome and boundaries

Java callers obtain checked CSS ID, animation and custom-property names without spelling generated strings. CSS and generated Java agree across separate compilation. Property sharing preserves original ownership and CSS inheritance/cascade. Processing stays on the JVM. Shrinking, compression, general interfaces, asset handling, font-face and external-property interoperability are excluded.

## Requirements and acceptance criteria

- R1 / AC1: Dedicated method annotations CssId, CssAnimation and CssCustomProperty expose zero-argument abstract String accessors on Css, GlobalCss and Keyframes definitions. name defaults to <default>; getter inference follows Arez (getAccent -> accent, otherwise method name), with explicit CSS names supported. Input ID/property names omit #/--. Annotation misuse, malformed/empty names, missing occurrences, conflicting kinds, duplicate local exports and reserved style/factory conflicts fail clearly without partial generated Java. Existing statelessness, access, nullness, formatting and class-selector contracts remain.
- R2 / AC2: All custom-property declarations and var references in Java CSS definitions are local by default, including var-only properties and nested fallback/keyframe/conditional contexts. Parsed names are rewritten consistently; unrelated strings remain unchanged. A property accessor returns its full -- prefixed resolved name.
- R3 / AC3: CssImport(value=Owner.class, property="source", name="alias") binds an optional local alias (default source) for reads and declarations/overrides. Source and alias omit --. Any real property occurrence in the provider can be imported without a Java accessor. Aliases preserve canonical ownership through separate compilation and transitive imports; unresolved ownership cycles and malformed/missing/duplicate imports fail. Used imports retain provider CSS; unused imports are checked but do not retain it. Existing global inclusion cycle checks and named component/animation import behavior remain. Header-jar compilation must not require resource sidecars.
- R4 / AC4: Bound ID hash selectors and pure same-document URI fragments establish occurrences and rewrite to owner-scoped names. Supported selector functions and nested declaration expressions participate. Strings and external asset fragments are unchanged. Known attribute-selector references to bound IDs fail explicitly. Existing component ownership restrictions remain. Returned IDs have no # and are static per definition, not per component instance.
- R5 / AC5: Animation accessors expose checked local names and imported aliases, retaining imported resources even without a static CSS use. Keyframes definitions expose their own existing class-derived animation identity; they do not rename that identity through an accessor. Resource declarations without accessors remain resource-only. Resource definitions with generated Java bindings are conservative bundle roots whenever their build target is reachable, matching current component retention until Closure liveness is available. Bindings use existing package-access Styled naming, private constructors, lazy holders and authored forwarding get methods. Resources require no style accessor; component style() remains mandatory and unannotated.
- R6 / AC6: Examples migrate all affected imports and runtime setters. TodoMVC hover inheritance, save-button themes and dynamic overrides, Drumloop indicator and Speech marker behavior remain correct. Raw shared CSS stays literal; the cross-boundary cascade test moves to an ordinary property. Add meaningful JVM/J2CL/browser examples for the new symbol kinds and source-map evidence after rewrites.
- R7 / AC7: Extensive unit/processor constraint tests and paired formatted/unformatted goldens cover kinds, inference, aliases, malformed input, imports, resource binding/access, retention and source positions. Keep coverage thresholds unchanged. Permanent README/changelog describe downstream behavior; temporary plan/evidence disappears at closeout.

## Decisions and impacts

| Decision | Rationale and impact | User verification |
| --- | --- | --- |
| Arez-style name sentinel and getter inference | Explicit overrides cover non-Java spellings without hidden suffix/kebab conversion. IDs/properties use unprefixed author names. | Inspect annotation APIs and inference fixtures. |
| Properties local unless imported | Import scope does not imply a fixed name. Existing sharing/setters must migrate together. | Review TodoMVC inheritance and theme overrides. |
| Property imports are alias bindings in standard CSS | One mapping handles var reads and declaration writes, without another CSS dialect. | Review import examples and separate-compilation tests. |
| Optional bindings on all three definition kinds | Resource-only declarations stay simple; Java consumers need no dummy component. | Inspect generated resource bindings and direct keyframe names. |
| IDs cover hashes and pure fragments | Structured parsing distinguishes symbols from strings, colors and asset URLs. Attribute references are rejected until deliberate support exists. | Review positive and negative ID fixtures. |
| External raw properties deferred | No false promise of renaming users outside the compilation. | Review shared CSS example migration and README boundary. |

## Technical and testing decisions

Use parsed ph-css models; do not rewrite identifier text throughout strings. Preserve source locations and compose existing maps. Keep processor orchestration/validation, immutable descriptors and JavaPoet/Proton generation separated. Retain annotation information needed to resolve aliases through Bazel header jars. Do not add a speculative full liveness schema. Cycles without a canonical property owner are errors, while valid local CSS variable cycles retain CSS semantics. Global resource inclusion order/cycle rules remain enforced. Dependency closure traverses all definition kinds, including keyframes importing property providers; every retained global emits once, dependency-first where global inclusion edges apply. Valid mixed-kind dependency cycles are allowed; global-only inclusion cycles still fail.

T01 proves complete property behavior and migrates existing applications; T02 adds complete ID/animation exports; T03 hardens full-scope evidence and permanent docs. Each slice runs its focused checks before commit. The final gate is tools/check.sh, including unchanged 95/85 coverage roots, development/optimized browser tests, isolated consumers and distribution checks. No domain-modeling directories exist, so no separate canonical artifact system is introduced.

## Open questions

None requiring user direction. Local implementation choices preserve the accepted contracts above.
