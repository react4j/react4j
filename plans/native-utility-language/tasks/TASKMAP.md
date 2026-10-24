# Task Map

- Spec: [SPEC.md](../SPEC.md)
- Status: `implementation-review`
- Current frontier: independent implementation review
- Planning reviewer: `implementation_plan_review` (`1/3` rounds; passed; final confirmation `unused`)
- Plan checkpoint: `automatic` — explicit implementation authority and passing independent planning review; no human pause requested.
- Implementation reviewer: `native_implementation_review` (`1/5` rounds; changes required; final confirmation `unused`)
- Authority: explicit user implementation authorization lifting design-only; JVM-only and separate package remain mandatory.

## Full-scope validation

- Gate: task tests; development/optimized browser scenarios; `tools/check.sh`; domain-link validation; complete diff and package/dependency audit.
- Evidence: pre-review `165ef456a` passed the final `tools/check.sh` twice (56 main targets, 16 optimized browser targets, 25 JVM coverage targets; publication/Javadocs, format/hermeticity, JVM/browser coverage thresholds, site and unsigned release validation). Language coverage was 98.19% lines / 88.05% branches against 95% / 85%. Review fixes pass focused language/internal suites and two packaged processor methods; their complete gate rerun is in progress. Domain links and specification-only diff/package audits pass; see T08 for scope and browser limitations.

## Tasks

| ID | Task | Status | Blocked by |
| --- | --- | --- | --- |
| `T01` | [Expand checked rules through the isolated JVM facade](T01-language-facade.md) | `complete` | None |
| `T02` | [Consume native rules through checked Java declarations](T02-java-consumption.md) | `complete` | T01 |
| `T03` | [Link independently compiled candidate fragments](T03-fragment-delivery.md) | `complete` | T02 |
| `T04` | [Accept broader typed values and numeric limits](T04-value-domains.md) | `complete` | T01 |
| `T05` | [Apply container and starting-style variants](T05-conditional-variants.md) | `complete` | T03, T04 |
| `T06` | [Deliver checked named anchors and scoped popovers](T06-named-anchors.md) | `complete` | T05 |
| `T07` | [Expose agent authoring feedback and packaged commands](T07-authoring-tools.md) | `complete` | T06 |
| `T08` | [Verify and document the complete delivered contract](T08-contract-and-gate.md) | `complete` | T07 |

## Sequencing notes

- Select the lowest-numbered unblocked pending task. T04 may be unblocked while T02/T03 run; it does not require their metadata changes. Keep only one task in progress.
- Exact active tree: `plans/native-utility-language/`. Resolve it before every plan edit, commit and deletion. The prior design-delivery tree was closed and is not resumed.
- Workflow owns post-task independent review, PR publication and plan cleanup. The PR is stacked on `codex/react4j-explore-tailwind-language-and`; human merge remains pending.
- Promotion owners: accepted behavior belongs in utility/binding/naming/liveness/source-map specifications, resolved terms in the glossary, adopted JVM rule compilation rationale in the replacing ADR, and usable author reference/examples beside the tooling. Design-only claims must be reconciled with implementation.

## T08 audit leads

- Verify native provider/individual resource stability annotations across checked snapshots and candidate fragments; existing naming promises apply to the new provider route.
- Verify non-HTML Unicode whitespace inside a candidate value against HTML token splitting and shared canonical-name validation.
- Replace the stale removed `Names.java` coverage override with its current owner, and register the language root/instrumentation without reducing thresholds.

## Review history

- Planning round 1: `implementation_plan_review` reported `Findings: none`. Checked all eight tasks, accepted scope, task graph/coverage, processor/sidecar/header/catalogue/linker/bundler/name boundaries, distribution/browser/full-gate verification. Residual uncertainties (typed-hole flattening, shared binding extraction, browser semantics) have explicit task-owned checks. No finding dispositions required; two ordinary rounds and one final confirmation remain.
- Implementation round 1: fresh read-only `native_implementation_review` checked complete scope `54d35cfa9..165ef456a`, all requirements/tasks/promotions and ten focused targets. Three P2 findings verified and fixed below; confirmation pending. Four ordinary rounds plus one final confirmation remain.
- F1 (inline header origins): publish the decoded input's authoritative map inside the provider's ordinary CSS map; generic collection composes header-derived locations through it. No language dependency enters linker/bundler. A real packaged provider/header-consumer regression collects the outputs, compresses names and asserts exact authored Java file/content/line/UTF-16 column. Generic tests cover unmapped spans, stale input content and invalid coordinates.
- F2 (source overlap): use the actual bare matcher at zero when checking scale intersections. Unused utility/variant definitions fail in either source order, including common length/percentage types and ranges excluding zero; genuinely disjoint time/percentage spellings remain accepted.
- F3 (unused anchor tokens): check every token's anchor occurrences, including nested fallbacks, against the complete owned/external universe and attribute failures to that token entry. Owned, external and default-anchor forms pass; unknown references fail through both facade and packaged provider compilation.
- Related reference correction: remove bare length-percentage from the advertised list, matching the accepted design and implementation. Existing normative contracts already own all three obligations; these are bug fixes and reference corrections, not scope or architectural replanning. Domain-link and specification-diff audits pass. Focused suites pass; full gate is running before closeout and second-round confirmation.

## Promoted knowledge

- Accepted grammar, values, variants and feedback: [native contract](../../../docs/specs/native-utility-language.md); input/cascade/reset obligations remain in [utility CSS](../../../docs/specs/utility-css.md).
- Java selection and hooks: [bindings](../../../docs/specs/css-bindings.md); canonical names/stability/anchors: [names](../../../docs/specs/css-names.md); requested fragment retention: [liveness](../../../docs/specs/css-liveness.md); available origins: [source maps](../../../docs/specs/css-source-maps.md).
- Resolved terms: [glossary](../../../docs/glossary/README.md). Adopted decisions: [JVM compilation](../../../docs/adr/0005-compile-declarative-utility-rules-on-jvm.md) and [checked expansion publication](../../../docs/adr/0006-publish-checked-utility-expansions.md), replacing ADR 0004.
- Practical grammar/commands/examples and browser limitations: [author reference](../../../css/utility-language/README.md), replacing the completed design document. Domain-link and specification-only diff audits passed before review. Existing mapping deferrals remain unchanged; no new deferred issue is needed.
