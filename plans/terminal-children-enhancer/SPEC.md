# Terminal children and default enhancer spec

## Source

- Accepted scope: this conversation after PR #122 merged. The user accepted mechanical downstream translations, then requested automatic children-enhancer selection unless an explicit enhancer is supplied, and instructed delivery, PR creation, babysitting until merge, and chat archival afterward.
- Authority: latest user “Proceed ... structured-delivery-workflow ... create a PR ... babysit-pr until it lands”. Grill-me is conditional on missing decisions; none currently requires reopening. There is no human plan checkpoint. Babysitting authorizes focused repairs and normal pushes, never merging, approval, auto-merge or queue entry.
- Evidence: InputComparator sorts child/children last; BuilderGenerator currently leaves optional child arrays chainable but always terminates the stream intrinsic. InputEnhancer validates one setter and R-returning defaults. Rose has 18 terminal plural calls, no children-stream calls, and 13 scalar child/leaf/build chains. Local examples have no generated child/children calls.

## Problem and outcome

Child inputs have inconsistent completion depending on overload and optional grouping. Standard stream conversion is hard-coded despite the new enhancer mechanism. Make setting either child input complete construction, and give plural children the standard stream enhancer automatically through the ordinary enhancer mechanism.

## Scope and constraints

- React4j only; document downstream reordering, do not edit Rose or change its dependency pin.
- Keep scalar ReactNode.of wrapping, the React children property binding, defaults, key computation, required input ordering, context behavior and build availability otherwise unchanged.
- Keep the one-setter/R-returning enhancer contract; no terminal capability, registry, opt-out flag, empty ChildEnhancer, compatibility mode or unrelated refactor.
- Leave etc/secrets untouched. Follow explicit source lists, JSpecify, Yoda comparisons, regenerated golden policy and tools/check.sh.

## Requirements

- R1: Supplying scalar child or array/stream children always returns the finished ReactNode, including alongside multiple optional inputs. Optional children can still be omitted using the existing build method.
- R2: A non-context input whose effective name is children selects the public standard ChildrenEnhancer when enhancer is void.class, whether omitted or explicit. Ordinary inputs still have no automatic enhancer.
- R3: An explicit custom enhancer replaces the standard selection; extending ChildrenEnhancer retains its helpers. Explicit tree-context enhancers remain invalid; implicit selection skips tree-context inputs.
- R4: The standard stream helper materializes its stream in order and delegates once to the canonical array setter. Inherited defaults and first-step shortcuts use existing enhancer rules and validation, without a duplicate stream intrinsic.
- R5: Preserve scalar wrapping and child property binding, initialization and key behavior. Diagnose incompatible default/custom contracts through ordinary validation.
- R6: Author documentation and canonical enhancer specification explain selection, terminal semantics and migration. Validate real rendered output in development and optimized production, processor diagnostics, compiled enhancers and publication/site integration.

## Acceptance criteria

- AC1 (R1, R5): Compile required/optional method and constructor child cases with other optional inputs; terminal results are used directly as ReactNode and omitted optional children still build. Real React rendering preserves scalar and plural values.
- AC2 (R2, R4): Default children steps inherit ChildrenEnhancer<ReactNode>; both implicit and explicit void selections gain ordered stream conversion, first-step shortcuts and renamed-input support. Other array inputs receive no such parent. Generated output contains no stream intrinsic body.
- AC3 (R3, R5): Standalone custom enhancer replaces the standard parent and overload, inherited custom enhancer retains/overrides defaults, context skip/error behavior remains; nullness and shared-builder collisions are checked. Source and precompiled custom enhancers work.
- AC4 (R6): Checked examples and guide include automatic selection, replacement/inheritance and before/after terminal call migration; spec, annotation docs and changelog agree. Focused tests plus tools/check.sh pass.

## Significant decisions

| ID | Decision | Rationale | Impact | User verification |
| --- | --- | --- | --- | --- |
| D1 | Both special setters always terminate | They are ordered after every required input | Reorder optional setters before child/children and remove trailing build; 13 known Rose scalar calls | Review migration example |
| D2 | void.class uses normal selection; custom replaces | Preserve annotation surface and one enhancer per input | Explicit custom children enhancers inherit standard interface to retain streams | Review replacement example |
| D3 | Keep React binding in processor | A default matching its canonical setter cannot wrap by recursively calling itself | Scalar wrapping and stable children key remain intrinsic | Review browser rendering and unchanged key path |

## Technical and testing decisions

Public ChildrenEnhancer<R> lives beside core sources and uses existing JSpecify scope. Select it before ordinary parsing/validation; remove stream-specific generator metadata. Use focused compile tests and existing exact fixture regeneration, real React browser cases and the complete gate. Promote accepted contract to the existing [input-step-enhancers specification](../../docs/specs/input-step-enhancers.md); implementation details and verification stay in this temporary tree. These choices do not meet the surprising architectural-tradeoff threshold for a new ADR.

## Open questions

None.
