# T02 — Default children enhancer

- Status: complete
- Blocked by: T01
- Spec coverage: R2, R3, R4, R5, R6; AC2, AC3, AC4

## Delivers

Public standard ChildrenEnhancer, selected automatically unless a custom interface replaces it, through ordinary enhancer validation and generation. Remove stream-specific generator code. Publish durable contract and migration guidance.

## Acceptance criteria

- [x] Implicit/explicit void default selection, effective renamed children, unrelated arrays and context exclusions behave as specified.
- [x] Explicit replacement, inheritance, overriding, precompiled helpers, nullness and collision cases are compiled and checked.
- [x] First-step stream shortcuts and ordered exactly-once stream consumption render correctly with real React.
- [x] No stream intrinsic remains; source lists and publication include the public interface.
- [x] Guide, checked examples, annotation docs, changelog and existing canonical spec agree; domain links pass.
- [x] Complete tools/check.sh gate passes and implementation review can inspect exact evidence.

## Validation

- Processor enhancer tests, affected generated/jspecify goldens; checked doc examples.
- Development and optimized browser tests.
- Domain link validator and tools/check.sh.

## Evidence

- Focused enhancer suite: 57 cases passed, including automatic/default, override/inheritance, scope, nullness and shared-builder collisions. Checked examples compiled.
- Real React development browser suite passed ordered exactly-once stream materialization, static shortcut and custom default override; full gate also exercises scalar property storage and optimized production.
- tools/update_goldens.sh generated passed; 12 existing golden files now inherit the standard enhancer and omit the intrinsic body.
- New source/precompiled ChildrenView fixtures were generated in isolation, reviewed and declared explicitly; tools/update_goldens.sh jspecify then passed unchanged, including both source and compiled custom inheritance.
- Domain link validator and specification-only contract audit passed. tools/check.sh passed: 58 main targets, 17 optimized browser targets, 26 coverage targets, runtime coverage thresholds, site build/check and unsigned release validation. The interrupted coverage run was restarted; the completed run reported All React4j checks passed.
