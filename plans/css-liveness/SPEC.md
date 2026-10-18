# CSS Liveness Delivery Spec

## Source

- Execution authority: the user's request to start a thread implementing removal of unused selectors; subsequent accepted interview decisions and final `confirmed` checkpoint. Latest correction explicitly requests domain-modeling artifacts.
- Accepted scope: the complete grill-me interview in this thread, including revised decisions after Closure investigations. Implementation and push to existing PR #112 are authorized; merging is not.
- Evidence: current processor/generator, `css/defs.bzl`, bundler, browser rules and SaveButton variants; rules_closure 0.17.0 worker and Closure v20260913 probes. Current baseline is c0098a68d; preserve the concurrent unused-property-import correction.

## Problem and outcome

Styles currently survive whenever their Java library is reachable, even if optimization eliminates every use. Deliver selector and resource pruning based on the optimized JavaScript values, through the existing JVM-only build, with correct CSS behavior and source maps.

## Scope and constraints

- In scope: metadata, one complete Closure compilation's liveness evidence, independent CSS bundling, conservative selector/dependency pruning, real examples, negative coverage and durable docs.
- Out of scope: name compression, declaration/value optimization, asset URLs, font-face, a new keep API and per-chunk bundle output. Readable names remain.
- Preserve development and unlinked-library behavior, public css_bundle use with library roots, and multiple themes per JS link. Do not activate every discovered global.
- No Node; keep generated Java JVM usable. Explicit source lists/package-local BUILD files, Yoda equality, generated goldens via updater; do not touch etc/secrets.
- No old-schema compatibility for this unshipped feature. Keep upstream extension generic and bounded; preserve stock worker warnings, strict deps, modes and options.

## Requirements

The durable contract is [CSS Liveness](../../docs/specs/css-liveness.md). Delivery groups:

- R1: build-mode/library retention and reliable complete-link integration (contract 1, 8).
- R2: value liveness under optimization and JVM/runtime behavior (3, 11).
- R3: conservative selector pruning and owner/dependency activation (2, 4–6).
- R4: independently rooted variants and report provenance (7, 8).
- R5: authored source maps after deletion (9).
- R6: closed-world authoring boundary and permanent documentation (10).

## Acceptance criteria

- AC1 (R1,R2): actual J2CL→Closure ADVANCED→CSS bundle→Chromium proves unused owner/modifier deletion, root retention from modifier-only calls, inlined helpers/interfaces, and no runtime marker. BUNDLE and library roots retain full intended CSS.
- AC2 (R3): parsed selector tests cover selector lists, nested :is/:where/:not, negative conditions, attributes/strings, peers, IDs and grouping rules. Specificity and surviving declaration order are unchanged.
- AC3 (R3): live ID/animation/property accessor activates authored includes; imported bindings activate consuming includes; CSS-only property chains retain intermediate overrides; dead rules lose their exclusive animations/globals; explicit globals remain selected.
- AC4 (R4): the existing three SaveButton variants share one link/report; unused theme globals stay out. Missing/bad/version-mismatched/stale reports and catalogues fail with useful errors. A recognized uninstrumented binary cannot fall back to library mode.
- AC5 (R5): source-map consumer queries after earlier deletion resolve selectors and declarations, including nested conditional rules and UTF-16/multiline cases, to exact Java text-block locations.
- AC6 (R6): README/changelog explain downstream behavior and closed-world limit. Glossary/spec/ADR/deferred artifact links validate. Fixtures, standalone processor packaging and separate compilation remain correct.
- AC7 (all): focused suites, dev+opt browser tests and `tools/check.sh` pass; simplify and independent implementation review pass; remove temporary plan, commit, push PR #112, no merge.

## Significant decisions

| ID | Decision | Rationale | Impact | User verification |
| --- | --- | --- | --- | --- |
| D1 | Preserve values as typed compiler-only symbols until final collection/lowering. | Methods inline; early gatherCssNames sees dead names. [ADR](../../docs/adr/0001-css-symbol-liveness.md). | One JVM JS compilation; ordinary runtime strings. | Inline/interface examples keep correct CSS and no markers. |
| D2 | Generic factory/config/report seam in rules_closure, local implementation. | Keep upstream worker/runner checks and avoid forked runner. | Small pinned patch, local worker assembly. | Stock diagnostics and compiler modes still work. |
| D3 | Bundle variants apply roots after linking. | Existing themes share JS. | Report excludes bundle-specific roots; explicit shared CSS retained. | All three SaveButton variants. |
| D4 | Prove impossibility conservatively; never simplify selector functions in ways that change specificity. | Negation and :is specificity invalidate naive token filtering. | Some complex unused selectors remain. | Functional selector counterexamples and browser computed styles. |
| D5 | Keep readable names and closed-world accessor use. | Compression is separately scoped; unknown external strings cannot prove presence. | No keep API or name rewriting now. | README and ordinary class-list/helper usage. |

## Technical decisions

- Extend processor sidecars with checked binding identities and symbol/dependency information. Identify generated implementation (including facade nested implementations) and literal returns; never scan arbitrary matching user strings. Cover generic bridges and separately compiled definitions.
- Collect transitive CSS trees using existing aspect. Build a deterministic catalogue for the complete link. A local macro instruments Closure binaries used by browser applications/tests; library roots remain direct. Pass configuration as a declared input and report as a declared output. The binary provider path is checked before its library provider.
- Generic upstream CompilerFactory creates a compiler from opaque configuration/report paths when configured and finishes evidence after the runner writes JavaScript; fail on missing/multiple implementations. Split unchanged upstream worker main into a reusable java_library plus existing binary wrapper. Local worker runtime adds factory service; no CSS-specific upstream code or runner copy.
- Local compiler overrides initChunks to add typed immutable external symbols and BEFORE_CHECKS substitution exactly once. It collects and lowers in performPostCompilationTasks, which probes verified runs after final optimization. BUNDLE bypasses these passes: explicitly emit retain-all evidence through factory/run lifecycle rather than treating absent report as empty.
- Report binds version, link identity, actual JavaScript bytes and deterministic input catalogue identity, validates known binding IDs, and has explicit optimized/retain-all mode. Link identity uses the linked output artifact owner so aliases are supported; catalogue content includes CSS/resources, not merely owner names. Bundle-specific roots are excluded.
- JVM bundler parses the generated CSS model for conservative selector decisions and retained animation/property references. Metadata preserves immediate provider ownership when canonical property name belongs upstream; owner activation and DOM-symbol presence are separate states. Iterate retention to a fixed point. Preserve authored includes and existing layer/order semantics.
- Delete complete impossible rules/list branches only when safe; preserve uncertain functional-selector internals and original bytes of retained spans. Project existing source-map segments onto surviving spans; verify actual origin queries, not map shape alone.
- Hard-cut metadata schema with updated readers/goldens; no second JS compile, runtime CSS helper, generic plugin framework or new public keep/compression controls.

## Testing decisions

- Unit tests exercise parser/model decisions, metadata/report validation and map projection. Processor goldens cover generated identity for nested, inherited generic, collision/facade and formatting paths; JVM output compiles with strict checker policy.
- Real complete links verify ADVANCED dead-code behavior plus BUNDLE retention. Test library root fallback deliberately and uninstrumented binary rejection. Build examples in both modes; source-map assertions use a consumer.
- SaveButton supplies runtime computed styles/animations/IDs/property/theme variants; integration fixtures exercise provider-only CSS, explicit exported library links and invalid reports. Coverage targets use configured thresholds; new owned Java code needs meaningful unit coverage.
- Full gate, simplify, independent review and cleanup precede push.

## Knowledge classification

Specification gates 1–2 completed before drafting: searched existing docs/README and code; no prior domain artifact owners exist. Each accepted conclusion has one canonical owner below.

| Conclusions | Owner and qualification |
| --- | --- |
| Meaning of symbol, authored owner, complete link, report, variant | [Glossary](../../docs/glossary/README.md) |
| Mode behavior; value liveness; conservative pruning; owner/dependency activation; peers/IDs; variants/provenance; maps; closed-world boundary; ordinary runtime values | [Specification](../../docs/specs/css-liveness.md), each accepted, verifiable invariant survives replacement and otherwise lacks a durable contract owner |
| Single symbolic JS compilation and independent variant bundling | [ADR](../../docs/adr/0001-css-symbol-liveness.md): costly integration boundary, non-obvious final-pass timing, real scan/two-compile alternatives |
| Compression policy after liveness | [Deferred issue](../../docs/deferred/css-name-compression.md), explicitly deferred by user; trigger is selecting compression implementation |
| Factory SPI, schemas, pass APIs, parser and map projection algorithms, test layout | Code; delivery choices here, not durable specification requirements |
| Gates, sequencing, review budgets and evidence | This temporary plan |
| Assets/font-face/per-chunk output | Scope boundaries here; no additional artifacts without a current accepted contract |

No essential use case: the specification Purpose expresses this one goal without a redundant task dialogue. Spec gates 3–4: requirements 2–9 currently have implementation work owned by T01/T02; all spec sentences pass the replacement test and contain no delivery sequencing. Re-audit at promotion/closeout.

## Open questions

None requiring user direction.
