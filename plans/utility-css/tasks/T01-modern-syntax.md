# T01 — Parse and trace modern supplied CSS

- Status: complete
- Blocked by: None
- Spec coverage: R9, R13; AC4 (syntax), AC8 (contract promotion)

## Delivers

The shared CSS syntax/token seam parses accepted flat modern rules and exposes their bodies and reference-bearing conditions, with accurate source positions and explicit syntax permissions. This removes the demonstrated empty-var-fallback failure before utility APIs depend on modern input.

## Acceptance criteria

- [x] Classify every accepted requirement into its durable owner, inspect relevant existing histories, promote the finite-provider decision and contract changes, and validate documentation links/specification-only diff.
- [x] A bounded parser extension accepts and preserves empty var() fallbacks, layers, property registrations, container and starting-style wrappers, including combinations with media/supports.
- [x] Shared traversal discovers escaped class references and managed-property locations throughout conditions and bodies; model conversion cannot silently discard rules.
- [x] Component/utility important remains rejected; explicit global permission is represented without weakening unrelated modes. Invalid syntax and unsupported rules have correct authored offsets.
- [x] Existing component nesting, font/keyframe, token, URL and source-map tests still pass. Any changed parser realization is evidence-backed and reconciled with the plan before dependent work.

## Validation

- bazel test //css/internal/src/test/java/react4j/css/internal:tests //css/processor/src/test/java/react4j/css/processor:unit_tests — proves parser/model and existing compilation behavior.
- Focused retained CSS cases from verified optimized Tailwind output exercise actual var(--tw-rotate-x, ) and conditional wrappers through token discovery and mapping.
- Domain-modeling link validator plus specification-only diff audit — proves promoted contract ownership and links.

## Evidence

- The pinned ph-css source archive is rebuilt with bounded grammar/model repairs, using the original transitive runtime dependencies without shipping the original class jar alongside replacements. Original non-class resources are preserved. Empty expression fallbacks, layer-body unknown rules, structured conditional bodies and model visitor recursion are repaired at that dependency boundary.
- Shared processed-syntax parsing now feeds tokens, URL edits and pruning. Conditional headers distinguish container names, custom-property query keys, var() arguments and literal values; masked subparsing preserves original UTF-16 positions.
- Final validation: internal and bundler suites, processor unit_tests, implementation_tests and packaged processor_tests all passed (11 targets). tools/update_goldens.sh css regenerated and verified the positive global-important fixture. Java formatting, buildifier, git diff --check and documentation links passed. Runtime inspection confirmed the rebuilt parser replaces rather than duplicates the upstream class jar; all seven original non-class resources remain byte-identical.

## Knowledge routing

| Accepted matter | Canonical owner | Qualification |
| --- | --- | --- |
| Finite provider versus generator | [ADR 0004](../../../docs/adr/0004-consume-finite-utility-libraries.md) | Adopted boundary, meaningful maintenance tradeoff, surprising without rationale |
| R1, R7–R9 and source/catalogue portions of R12 | [Utility CSS specification](../../../docs/specs/utility-css.md) | Accepted, durable, externally verifiable input/inclusion obligations |
| R2–R4 and static name-flow permission in R10 | [Bindings](../../../docs/specs/css-bindings.md) and [utility input](../../../docs/specs/utility-css.md) | Java declaration contract and input grammar have distinct ownership |
| R5–R6 identity and stability | [Names](../../../docs/specs/css-names.md) | Ownership/spelling obligations survive parser replacement |
| R11 and support-resource retention | [Liveness](../../../docs/specs/css-liveness.md) | Surviving-value and dependency invariants survive linker replacement |
| R12 authored origins and asset delivery | [Source maps](../../../docs/specs/css-source-maps.md) and existing [assets](../../../docs/specs/web-assets.md) | Independent observable origin/file contracts; existing JVM constraint unchanged |
| Utility/container language | [Glossary](../../../docs/glossary/README.md) | Definitions only |
| R13, backend realization, task order and validation evidence | Reader guide/changelog, code and this temporary tree | Delivery and mutable details fail the specification replacement test |

- Read relevant existing contracts, ADRs, glossary and recent specification history before drafting. The prior prohibition on managed names hidden in component variables remains for ordinary component declarations; the accepted finite utility path gets explicit static-flow support. Existing map-publication, checked-name and asset-cache obligations remain.
- Specification-only diff audit: accepted obligations are routed to existing owners or the new utility-input owner; no task sequence, tool command, version pin or parser realization was promoted into specifications. Implementation mismatches are owned by T02–T08. Documentation link validator passed after promotion.
