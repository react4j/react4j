# CSS interface contracts

## Source and authority

The user requested a new thread to implement Interfaces using grill-me, followed by structured delivery if useful. Four interview rounds were accepted, and the final shared understanding was confirmed with “agreed”. Implementation and push to existing PR #112 are authorized. No human plan checkpoint was requested. This initiative began clean at 77acaf052 on codex/css-in-java-proof. Applicable repository rules are AGENTS.md, CONTRIBUTING.md and css/README.md. The completed CSS-symbol initiative is baseline, not a paused plan to resume.

## Problem and required outcome

Different CSS definitions must satisfy a shared Java interface without repeating every accessor declaration. Inherited symbol contracts must resolve deterministically and work through separately compiled libraries, while each definition keeps its own CSS identity.

## Scope and constraints

Support interfaces on Css, GlobalCss and Keyframes classes. Interfaces remain ordinary Java interfaces with fields and default/static/private helpers; existing annotated-class statelessness, visibility, construction, class inheritance and generic-class constraints remain. Existing lazy package-access generated implementations and authored forwarding factories remain. Resource declarations without effective symbol accessors remain resource-only. General class inheritance, directly annotated interfaces, CSS shrinking/renaming, assets and font-face are excluded. Processing stays JVM-only.

## Requirements and acceptance criteria

- R1 / AC1: Effective inherited abstract methods need no redeclaration. Component root/modifier matching and generated public overrides work through shared interfaces. Resource marker/default-only interfaces do not create bindings, while unsupported abstract resource methods fail clearly. Two implementations keep distinct owned names. Owner-declared state checks do not accidentally reject interface fields/helpers.
- R2 / AC2: Generic interface signatures are substituted in the consuming annotated definition. Effective accessors still require String, no parameters, no method type parameters and no throws; generic definition classes remain unsupported. Existing authored nullness acceptance and generated nonnull values remain.
- R3 / AC3: Interface CssId, CssAnimation and CssCustomProperty annotations form inherited contracts. Unannotated redeclarations retain metadata. Explicit redeclarations may change the local CSS name within the same kind; changing any inherited explicit kind fails. Plain abstract declarations contribute signatures without erasing another explicit contract. Existing owner-dependent animation/name inference, imports and occurrence checks apply at each implementation.
- R4 / AC4: Related redeclarations use the most specific explicit mapping. Unrelated matching effective contracts merge; conflicting names need an explicit annotated redeclaration choosing the mapping; conflicting kinds always fail. Outcomes and diagnostics do not depend on implements traversal order.
- R5 / AC5: The effective method for an explicit symbol contract must be abstract. Authored concrete/default implementations fail; re-abstraction permits generation. Unannotated default/concrete helpers retain Java behavior. Components still require abstract style(). Inherited instance get() conflicts with the generated singleton factory and fails clearly; non-inherited interface static/private get helpers do not conflict.
- R6 / AC6: Interfaces compile on their own with no CSS outputs or contract validation. Every consumed contract is validated against its annotated definition, consistently for source and CLASS-retained separately compiled interfaces. Existing erroneous symbol annotations on non-interface non-definition owners continue to fail. Diagnostics identify author contracts rather than surfacing generated compiler crashes/errors.
- R7 / AC7: Source/binary interface contracts work for all declaration kinds and all symbol kinds. Tests cover inheritance/diamonds, refinement/conflicts, generics and invalid signatures, defaults/re-abstraction, fields/helpers, factory collisions, deprecation/nullness, nested-type name shadowing and source-order invariance. Paired goldens and real JVM/J2CL/browser calls through interfaces demonstrate behavior. Permanent README/changelog explain downstream behavior. Existing coverage thresholds and full local gate pass; simplify and independent review finish before closeout.

## Significant decisions

| Decision | Rationale | Impact | User verification |
| --- | --- | --- | --- |
| All three declaration kinds participate | They all expose Java names now | Resource bindings inherit symbol contracts too | Read resource interface examples |
| Inherited methods and substituted generic interfaces | Avoid repeated declarations while respecting actual Java types | Generic interfaces may resolve to String; definition classes remain non-generic | Review shared-contract and generic examples |
| Metadata persists through unannotated overrides | Preserve the contract expressed by the interface | Local same-kind mapping refinement is explicit | Inspect override fixtures |
| Resolve diamonds explicitly | Implements order must not choose semantics | Same-name contracts merge, conflicting names need explicit mapping, kind conflicts fail | Inspect conflict diagnostics |
| Explicit symbol contracts stay abstract | Generated names must agree with parsed CSS | Default helpers remain available; concrete symbol overrides are rejected | Inspect re-abstraction/default tests |
| Validate only consuming definitions | Interfaces may be generic and owner-dependent | Interface-only compilation emits no CSS; binary inputs get equivalent validation | Review separate-compilation tests |

## Technical decisions

Separate declared-state validation from effective inherited-method collection. Use javax.lang.model type/override/subsignature APIs and existing Proton conventions where correct. Preserve declaration provenance before collapsing Java signatures: pinned Proton ElementsUtil.getMethods replaces one duplicate abstract declaration with another and cannot alone detect annotation conflicts. Resolve metadata in the consuming owner, with deterministic ordering and diagnostics. Avoid adding public configuration or annotations. Keep processor parsing/validation/orchestration, descriptors and generation separated.

## Testing decisions

Existing processor harness checks emitted output sets, paired formatted/unformatted Java/CSS/maps and strict recompilation. Extend it rather than add a parallel harness. Existing integration targets can compile contracts separately through Bazel header jars. A real browser consumer should accept a shared interface and exercise two implementations plus checked symbols. Changed logic must have high focused coverage; tools/check.sh is the final gate with unchanged coverage.json. No domain-modeling directories/config exist. Temporary plan files are deleted only after review/closeout; lasting contracts stay in css/README.md and CHANGELOG.md.

## Open questions

None requiring user direction.
