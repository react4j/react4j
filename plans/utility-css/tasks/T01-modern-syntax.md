# T01 — Parse and trace modern supplied CSS

- Status: pending
- Blocked by: None
- Spec coverage: R9, R13; AC4 (syntax), AC8 (contract promotion)

## Delivers

The shared CSS syntax/token seam parses accepted flat modern rules and exposes their bodies and reference-bearing conditions, with accurate source positions and explicit syntax permissions. This removes the demonstrated empty-var-fallback failure before utility APIs depend on modern input.

## Acceptance criteria

- [ ] Classify every accepted requirement into its durable owner, inspect relevant existing histories, promote the finite-provider decision and contract changes, and validate documentation links/specification-only diff.
- [ ] A bounded parser extension accepts and preserves empty var() fallbacks, layers, property registrations, container and starting-style wrappers, including combinations with media/supports.
- [ ] Shared traversal discovers escaped class references and managed-property locations throughout conditions and bodies; model conversion cannot silently discard rules.
- [ ] Component/utility important remains rejected; explicit global permission is represented without weakening unrelated modes. Invalid syntax and unsupported rules have correct authored offsets.
- [ ] Existing component nesting, font/keyframe, token, URL and source-map tests still pass. Any changed parser realization is evidence-backed and reconciled with the plan before dependent work.

## Validation

- bazel test //css/internal/src/test/java/react4j/css/internal:tests //css/processor/src/test/java/react4j/css/processor:unit_tests — proves parser/model and existing compilation behavior.
- Focused retained CSS cases from verified optimized Tailwind output exercise actual var(--tw-rotate-x, ) and conditional wrappers through token discovery and mapping.
- Domain-modeling link validator plus specification-only diff audit — proves promoted contract ownership and links.

## Evidence

- pending
