# Native JVM utility language implementation

## Source

- Scope: [accepted language design](../../docs/design/utility-language.md), including the final confirmed interview and the explicit request to isolate the language in its own package.
- Execution authority: the user said, “Yes. Lift the design-only restriction and implement the design using the structured delivery workflow. Keep it JVM-only, isolate the language in its own package, and create an implementation PR for review.” No human plan checkpoint was requested. Do not merge either PR.
- Repository evidence: finite [utility contract](../../docs/specs/utility-css.md), [bindings](../../docs/specs/css-bindings.md), [names](../../docs/specs/css-names.md), [liveness](../../docs/specs/css-liveness.md), [source maps](../../docs/specs/css-source-maps.md), and current CSS processor/input packaging/catalogue/linker/bundler code and tests.
- Delivery starts at design commit `54d35cfa9` on `codex/native-utility-language`. Design PR119 remains separate and open. The implementation PR will be stacked on its branch so the code can be reviewed independently. Upstream master has an independent Java baseline update; do not silently include it in this stacked diff.

## Problem

Applications can consume finite utility CSS, but cannot author declarative native utility families and have javac expand evaluated candidates on the JVM. Agent authors need one inspectable rule vocabulary and structured feedback; application authors need checked classes without a scanner, watcher or JavaScript generator.

## Required outcome

The accepted grammar works through a narrow JVM language module, authoring operations, Java annotations, separately compiled providers/consumers and actual linked/browser output. A reviewed implementation PR is ready for human review with current contracts and documentation reconciled.

## Scope and constraints

- Implement the complete accepted first grammar, types, numeric bounds, variants, containers, starting styles, named anchors and selected Rose examples. Preserve independently useful finite CSS input.
- All parsing, validation, expansion, preparation, linking and URL/name finalization run on the JVM. No source scanning, watcher, executable rule callbacks, required external process or JavaScript toolchain is added.
- Isolate grammar/matching/typing/composition/diagnostics in `react4j.css.utility.language`, with explicit package-local Bazel sources and narrow target visibility. Keep annotation processing and shared CSS identities/metadata outside that package; no dependency cycle.
- Preserve ordinary local CSS precedence, explicit global resets, stable provider/candidate identity, exact written variant nesting, explicit container/anchor setup and existing dependency-driven support liveness.
- Honor the design's exclusions: no generic multi-capture/regex rules, slash modifiers, @apply, arbitrary properties/at-rule variants, generic group/peer generation, dynamic container/anchor names, named @position-try resources or new runtime name accessors.
- Keep Rose itself unchanged. The popover example demonstrates usable CSS relationships and layout choices; it is not authority to rewrite Rose or claim its current failures fixed.
- Leave `etc/secrets` untouched. Preserve checker, formatting, hermeticity and coverage policy; do not reduce thresholds to accommodate new code.

## Requirements

- `R1`: Native marker routing is explicit; a malformed native source never falls back to finite CSS. All definitions validate even when unused.
- `R2`: Checked reusable preparation/binding/description/expansion operations expose immutable data, structured diagnostics and origins, with deterministic, stateless candidate batches and no hidden I/O.
- `R3`: Tokens, typed scale/bare/arbitrary sources, one-parameter families, explicit negatives, source disjointness and literal/longest-prefix dispatch follow the accepted grammar.
- `R4`: All 15 specified types and supported numeric math/anchor contexts work. Bounds reject comparable bad constants, preserve browser-dependent values and report deferred checks honestly.
- `R5`: Named and guarded arbitrary selector variants, media/supports/container/starting-style wrappers, one-slot rules, typed nth/size holes and written nesting preserve meaning and specificity. Candidate splitting/underscore/escape rules are checked.
- `R6`: Java processing consumes evaluated annotation constants through existing declaration/inheritance APIs, preserving checked class lists, local markers and local CSS precedence. Inline and explicitly packaged file inputs work through separately compiled/header dependencies.
- `R7`: Checked provider descriptors and per-candidate fragments preserve fingerprints, order, source origins and canonical identities. Link collection deduplicates equivalent fragments, rejects inconsistent providers, and prunes by surviving getters without enumerating infinite families.
- `R8`: Size, custom-property style and scroll-state queries support static owned/external names and checked typed thresholds. Queries do not instantiate or retain setup utilities; nested queries remain separate.
- `R9`: Named/default anchor expressions, static declarations/references and externalAnchors bind in a distinct namespace, rename consistently and reach maps/reports/liveness. Browser scope remains explicit for repeated instances; no Java-generated coordinate/name runtime is added.
- `R10`: Agent-facing validation/description/batch expansion and javac diagnostics share codes/facts, definition locations, decoded candidate offsets and available source spans. Source maps preserve the existing guarantees.
- `R11`: Real JVM, packaged processor, independent compilation, linker/bundler and browser tests prove the behavior, including production pruning/compression, container conditions, starting styles and repeated scoped popovers. Current docs, specifications, glossary and adopted architecture agree with delivered behavior.

## Acceptance criteria

- `AC1` (`R1`–`R3`): A standalone module expands literals, spacing/color families and variants; malformed/unused/ambiguous definitions fail with stable diagnostics. Reused descriptors never accumulate old candidates; serialization/version checks are deterministic.
- `AC2` (`R6`): Inline and file-backed native libraries produce usable checked getters, including concatenated Java constants and inherited declarations. Separate consumers can request candidates absent during provider compilation. Finite regression tests pass.
- `AC3` (`R7`): Two independently compiled consumers share one provider/candidate identity; link output is ordered/deduplicated, unused getter expansions disappear, changed provider fingerprints fail, and support/name/map metadata remains correct.
- `AC4` (`R4`): Positive/negative tests cover all 15 types, math dimensions, ratios/compound values, scale units, ranges and browser-dependent bound traces; injection and wrong-context/function inputs fail.
- `AC5` (`R5`, `R8`): Named/bracket thresholds, size/style/all scroll-state forms, starting-style and order-sensitive selector chains expand and bind correctly; missing names, malformed conditions, prohibited holes and unguarded selectors fail.
- `AC6` (`R9`): Owned/external anchor declarations and every reference form remain consistent under compression/maps; dynamic/unknown names fail; absent setup getters stay absent; repeated browser instances find their own trigger.
- `AC7` (`R2`, `R10`): JVM authoring commands validate/describe/expand published rules with complete structured/human errors, ordering keys and origins; errors in a batch do not mask peers. Packaged distribution works without extra dependencies or scanners.
- `AC8` (`R11`): All task tests, relevant browser tests in development/production, normal formatter/buildifier/checkers and `tools/check.sh` pass. Durable docs are promoted and validated before independent implementation review.

## Significant decisions

| ID | Decision | Rationale | Impact | User verification |
| --- | --- | --- | --- | --- |
| `D1` | One private language facade in a separate source/build package, embedded in existing tools. | Accepted architecture concentrates interacting language rules without a new deployment or plugin seam. | Processor supplies binding data; shared CSS infrastructure owns canonical names and delivery. | Review dependency direction and caller surface. |
| `D2` | Consumer-driven expansions from checked versioned descriptors, merged as generic fragments. | Infinite families cannot have exhaustive provider inventories; independent compilation must work. | Shared metadata/catalogue and input sidecars change; stale artifacts fail rather than gaining shims. | Review fragment/fingerprint/source-map tests. |
| `D3` | Typed structure plus explicit browser uncertainty and contextual anchor checks. | Variables/layout cannot be evaluated statically, but known wrong types/ranges should fail early. | No injected clamp, polyfill or runtime evaluator. | Review bad constants and deferred-check output. |
| `D4` | Static owned/external anchor and container names; per-instance scope is CSS. | Matches existing ownership without inferring rendered elements. | Setup getter liveness stays explicit; names are not custom properties. | Review repeated-instance browser tests and absent-setup pruning. |
| `D5` | Keep the design's dispatch/cascade and selector subset. | Already accepted; avoid Tailwind compatibility inference and unsafe template injection. | Class list order has no precedence effect; templates retain authored order. | Review variant order, ambiguity and selector tests. |

## Technical decisions

- T01 verifies template-hole parsing/flattening and immutable descriptor feasibility through the real facade before downstream integration. A failed assumption triggers narrow replanning under the decision policy; it is not waived by a snapshot test.
- Native preparation is a distinct input-packaging path; finite CssInput helpers do not depend on the grammar package. Bind provider imports/externals and all template definitions before publishing/expanding a checked descriptor.
- Extract only annotation-free utility binding behavior needed by both callers into shared CSS infrastructure. Keep javac Trees/Filer/annotation reading and final link assembly out of the language package. No callback-based ownership SPI.
- Shared candidate fragment metadata contains provider/candidate, descriptor fingerprint, stable order key, CSS/map and generic name/dependency metadata; bundling does not parse native grammar. Schema changes hard-cut old compiled artifacts.
- Commit task results and evidence sequentially; implementations remain primary-agent work. Independent reviewers are read-only and phase-specific.

## Testing decisions

- Use facade behavioral tests with realistic positive/negative grammar data, not mocks of the expander. Share accepted examples as data where useful, without introducing a DSL testing language.
- Exercise both unshaded implementation and distributed processor jar, including input packaging, separate provider/consumer and header-jar paths. Test classpath/provider conflicts at actual collection boundaries.
- Browser tests use generated Java/J2CL accessors and real linked CSS in development and optimized modes, checking computed styles/geometry/transition outcomes, not only emitted strings. Test repeated scopes and missing setup separately.
- Add the new module to root CSS/JVM test suites, hermeticity inventory, coverage roots and instrumentation filters with current thresholds. Use failures to fill real coverage, not exclusions or weakened gates.
- Full final gate is `tools/check.sh`; run narrow targets during tasks. Preserve only evidence needed by active tasks, then remove the plan tree at closeout after independent review.

## Delivery obligations

After all tasks and the full gate pass, promote qualifying durable knowledge, complete independent implementation review, remove this temporary plan tree in a closeout commit and create/attach an implementation PR targeting the design branch. Do not merge. Review gates/cleanup/publication follow task completion and do not make task criteria circular.

## Open questions

None requiring user direction. Concrete internal types, command spelling and schema fields remain bounded implementation choices under the accepted contract.
