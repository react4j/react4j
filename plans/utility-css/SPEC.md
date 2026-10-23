# Utility CSS Delivery Spec

## Source

- Accepted scope: the utility CSS interview in this chat, ending with the user's explicit "confirmed" after the final shared-understanding summary. Earlier numbered answers are controlling where they differ from recommendations: external class hooks stay outside utility accessor lists; utility lists must be nonempty; method annotations; hybrid definitions; named containers and starting styles; file-backed globals.
- Execution authority: the subsequent instruction "Use structured-delivery-workflow and proceed." This authorizes planning, implementation, independent reviews, local commits and closeout. No plan-review pause, push, publication, release or Rose migration is requested.
- Repository evidence: [CSS authoring and build contract](../../css/README.md), [bindings](../../docs/specs/css-bindings.md), [names](../../docs/specs/css-names.md), [liveness](../../docs/specs/css-liveness.md), [source maps](../../docs/specs/css-source-maps.md), [JVM-only assets](../../docs/specs/web-assets.md), [contribution policy](../../CONTRIBUTING.md).
- Investigation: exact Tailwind 4.3.3 and 4.1.12 regeneration against an identical Rose source snapshot established that optimized output is flat, but the current ph-css parser rejects valid empty var() fallbacks. Standard generated animation/font variables, named containers and starting styles were separately verified. Scratch installations were removed. Findings establish input cases, not working React4j compatibility.

## Problem

Java views currently obtain checked CSS strings from component style definitions. Applications using finite supplied utility stylesheets need the same Java contracts, renaming, dependency retention and optimized removal without adopting raw utility strings in views or adding a Node.js dependency to React4j's build.

## Required outcome

An application can consume a finite utility library, declare nonempty checked class lists on Java methods, combine utility and local CSS, and render correctly in development and optimized builds. CSS and Java values share one canonical naming/liveness model, including across separately compiled dependencies.

## Scope

- In scope: dedicated utility providers, method bindings, inline/file provider sources, inline/file checked globals, modern flat CSS, owned and explicitly external names, source maps, checked assets, processor/J2CL/browser integration, reader documentation and durable contract updates.
- Out of scope: interpreting Tailwind directives or generating candidate utilities, running Tailwind/Node in consumer builds, general native nesting in supplied utility input, multi-source provider assembly, multiple utility providers on one style type, automatic utility merging, whole-string stability promises, new public container/font Java accessors without a caller, Rose changes, release/push/publication.
- Existing component CSS nesting and interfaces continue working. Tailwind 4.3.3 and current Rose patterns are conformance evidence for the stated grammar, not a promise to accept every plugin or future CSS construct.

## Constraints

- Honor root AGENTS.md and CONTRIBUTING.md: explicit source declarations beside packages, Yoda comparisons, full tools/check.sh gate, user-facing changelog. Leave etc/secrets untouched.
- All annotation processing, CSS processing and finalization remain JVM-only. Sources and dependencies are explicit; no repository scans or consumer dependency on a Tailwind installation.
- Preserve defining-owner naming policy, per-symbol stability, ordinary Java method selection, one name table across CSS variants, checked-only origins of generated names, safe pruning and authored source maps.
- Do not silently alter selector specificity, cascade, variable evaluation, external spelling or asset URL semantics. Reject unsafe unsupported input with source diagnostics; retain uncertain liveness cases.
- The public names accepted in the interview are UtilityCssLibrary, UtilityStyles and UtilityCss. Source selection uses one literal text block or a file attribute, never both. File names are package-relative declared inputs with the same canonical path restrictions as checked assets.

## Requirements

- R1: A dedicated utility provider supplies exactly one finite, flattened CSS source, inline or from a declared local file. Checked globals support the same source forms. Missing, conflicting or invalid source declarations fail deterministically.
- R2: A stateless utility style type selects exactly one provider. Effective abstract parameterless String methods with UtilityCss bind a nonempty whitespace-separated list of managed classes. Java compilation rejects every invalid name/list, including unused declarations and compiled provider contracts.
- R3: Each utility getter returns its exact declared utilities plus local root/modifier identity markers, with no implicit base utilities. Ordinary Java composition, helpers, interfaces, inherited annotations, replacement overrides and forwarding factories work. Existing typed symbol methods remain distinct and mutually exclusive with UtilityCss.
- R4: UtilityStyles may coexist with Css on the same type. A utility binding implements a class accessor without requiring a dummy local selector. Local selectors still require accessors; peer imports target the local root. Existing local-only accessors keep their existing validation.
- R5: Infer managed class identities from the full selector tree, including marker classes with no standalone rule. Properties/registrations, animations, fonts and container names have defining owners. Rewrite all resolved references consistently and isolate independent utility libraries, including their internal layers.
- R6: Explicit external names retain their spelling and remain outside managed absence proofs. Utility lists contain managed classes only; views append external class hooks as ordinary strings. Reuse checked imports for cross-owner properties, animations, IDs, fonts, peers and assets where supported.
- R7: Library rules must be guarded by managed classes, except supporting definitions and custom-property initialization. Optional reset/ordinary element defaults live in separately supplied explicit global inputs. Selecting utilities does not implicitly activate page defaults.
- R8: Ordinary component Css declarations win over utility declarations. Preserve authored ordering within a library and normal important semantics in explicit global inputs; component and utility inputs continue rejecting important declarations.
- R9: Flat supplied CSS supports escaped classes, selector lists/functions including group/peer/has, media/supports, layers, property registration, keyframes, font faces, container queries including named containers and managed property references in query conditions, and starting-style. Valid empty var() fallbacks survive unchanged. Unsupported constructs fail clearly.
- R10: Follow finite static variable definitions and aliases carrying managed animation/font names, preserving browser variable resolution and recording dependencies at their actual occurrences. Reject ambiguous managed-name rewrites, including variables whose renaming would change ordinary text. Runtime values retain the existing checked-name origin contract.
- R11: Development/unlinked builds retain available bindings and required dependencies. Optimized links follow surviving Java values, activate required providers, prune provably unused utility rules and support resources, retain uncertain selectors, and consistently rename every CSS variant. Selector/query references alone do not establish DOM presence.
- R12: Source maps retain authored inline Java or file CSS origins/content after pruning, renaming and URL finalization. Retained font/asset dependencies enter the existing finalizer; unused ones disappear. Resource packaging and header-visible catalogues make separately compiled consumers behave like same-compilation consumers.
- R13: Public documentation, durable domain contracts and changelog describe supported behavior and limits. Existing CSS behavior and the complete local gate remain green; no temporary plan or experiment scaffolding remains at closeout.

## Acceptance criteria

- AC1 (R1, R2, R12): Inline and package-relative file providers/globals compile; malformed paths, missing/duplicate sources, whitespace-only lists and unknown classes fail at Java compilation. A real Bazel consumer against a provider header jar passes for valid names and fails for an unused typo.
- AC2 (R2, R3, R4): JVM and J2CL getters return checked class lists longer than two symbols. A browser uses base/independent state fragments, inherited interfaces and a hybrid style; a peer selector targets its root. Overrides replace inherited lists, and missing metadata is distinguished from invalid empty metadata.
- AC3 (R5, R6, R7): Two libraries using the same authored classes, properties, animations, fonts, layer names and containers remain independent. External hooks/properties/container names retain spelling. A global element style in utility input is rejected; selecting a utility never introduces an unselected reset.
- AC4 (R8, R9): Browser computed styles prove component precedence and explicit global important behavior. Parsed conformance cases cover every supported wrapper, nested wrapper combination and valid empty fallback; malformed/unsupported inputs fail at authored locations.
- AC5 (R5, R9, R11): Optimized fixtures remove unused utility classes never exposed by an accessor, prune dead group/peer and conditional branches safely, preserve selector-function specificity, handle marker-only library classes in nonempty lists, and preserve named-container query behavior without query-driven DOM liveness.
- AC6 (R10, R11, R12): Live utilities retain static property aliases, property registrations, animation frames, font faces and files; dead utilities release their sole dependencies. Managed names inside static name-carrier values rename correctly; mixed semantic uses are diagnosed. Checked global imports share utility properties.
- AC7 (R11, R12): Development, ADVANCED production, compression-disabled and multiple-CSS-variant fixtures agree on Java/CSS names, preserve map origins/content and finalized URLs, and keep existing stale/missing-liveness diagnostics.
- AC8 (R13): Targeted tests and tools/check.sh pass, the specification-only diff and documentation links are audited, independent implementation review passes, intended commits exist locally, and the active plan tree is removed only after promotion and closeout.

## Significant decisions

| ID | Decision | Rationale | Impact | User verification |
| --- | --- | --- | --- | --- |
| D1 | Consume a supplied finite flat library | Reuse JVM linking without a second utility generator | Publishers compile/assemble input beforehand | Library CSS is prepared before React4j consumption |
| D2 | Method lists plus a single provider per type | Local declarations and unambiguous ownership | Plain inherited contracts need redeclarations for differing lists | Getter source matches intended independent fragments |
| D3 | Nonempty managed-only lists, with separate external hooks | Explicit user choices 2B and 3B | Empty annotations fail; caller supplies literal external hooks | View hook strings remain intentional |
| D4 | Preserve markers and allow hybrid CSS | Interchangeable Java contracts and checked peer targeting | Extra classes are intentional; base utility lists are not inherited | Hybrid and peer computed styles match expectations |
| D5 | Split utilities/support from explicit globals | Avoid page-wide side effects from selecting a class | Publisher prepares separate inputs; global important can override ordinary component rules | Reset activation and override policy are explicit |
| D6 | Preserve symbolic linking and extend one shared traversal for accepted syntax | Current bindings already compose symbols; opaque rules cannot be safely scanned or pruned | Parser work must cover consumers, not just acceptance | Modern cases retain identical CSS semantics through optimization |
| D7 | Catalogue visible during actual consumer compilation | Bazel dependency headers omit generated resources | File sources need JVM precompilation/catalogue plumbing, never a duplicated handwritten export inventory | Valid/invalid header-jar consumer tests exercise actual actions |
| D8 | Infer scoped support identities and follow static name-carrier dependencies | Standard Tailwind output uses variables for names | Ambiguous rewrites fail; browsers still resolve var() | Animation, font, container and two-library browser cases pass |

## Technical decisions

- Reuse canonical owner/kind/name symbols, binding composition, occurrence spans and existing Closure symbolic lowering. Remove the two-class binding limit; do not introduce browser runtime style objects.
- A shared parsed-rule traversal must expose every accepted conditional body and prelude to validation, token discovery, dependency analysis, pruning and mapping. Retain useful ph-css models; T01 must prove a bounded grammar repair/extension against real failure cases before later slices depend on it. A parser substitution requires evidence and plan reconciliation, not a silent second parser/fallback.
- Public file inputs are explicit compile dependencies. Derive machine-readable class catalogues before dependency header consumers need them, using authored annotation metadata or generated header-visible source. Verify the selected plumbing against actual Bazel header jars and direct javac packaging.
- UtilityCssLibrary declares external exclusions through empty-by-default String-array fields externalClasses, externalProperties, externalAnimations, externalFonts and externalContainers. Entries use decoded authored names; property entries omit the leading -- as existing property imports do. Validate identifier kinds, duplicate entries and relevant occurrences. Excluded classes are not valid UtilityCss list entries. These fields realize the accepted external boundary without a separate registry or Java accessor API.
- Bind property/animation/font/container references by grammar and provenance, not identifier substring replacements. Preserve external exclusions. Class-token membership can be rewritten when proven equivalent; whole-attribute/order/substrings that cannot survive added markers or renaming fail rather than gaining a compatibility flag.
- Keep component/global/utility syntax permissions distinct. Preserve existing component nesting restrictions while supporting flat modern library/global inputs. Isolate each library's internal layers and place utilities below components without changing ordinary global-root selection.
- Existing spec owners cover bindings, names, liveness and maps. The [utility-input specification](../../docs/specs/utility-css.md) owns source/boundary/grammar obligations; [finite-provider ADR](../../docs/adr/0004-consume-finite-utility-libraries.md) owns the library-vs-generator decision. No new essential use case or deferred document is justified. Task T01 records contract classification/history and promotes these accepted decisions; subsequent tasks update their implementation evidence.

## Testing decisions

- Start each task with its narrow behavioral seam; use actual javac compilation, real header jars, metadata/linker tests and source-map decoding. Do not rely on API-shaped mocks.
- Use small retained generated CSS fixtures that preserve exact grammar failures and representative Rose selectors; record compiler provenance beside the fixture. No Node installation is needed to execute repository tests.
- Use checked Java/J2CL browser assertions with the actual pinned runtime in development and optimized production. Browser computed styles prove CSS effects, liveness and precedence rather than mere string inclusion.
- End with tools/check.sh, including coverage, formatting, hermeticity, browser, site and release validation. Investigate full failures before fixes; preserve final evidence in commits/test records.

## Open questions

None requiring user direction.
