# T05 — Retain and rename property support

- Status: complete
- Blocked by: T04
- Spec coverage: R5 (properties), R6 (external properties), R9 (registration/conditions), R11 (property dependencies); AC3 (properties), AC6 (property chain)

## Delivers

Live utility classes retain the custom-property definitions, registrations and aliases they require, with consistent scoping/renaming and explicit external variable integration.

## Acceptance criteria

- [x] Properties declared/read by the provider have canonical ownership unless explicitly imported/external; registrations, declaration names, var() references and condition-prelude references agree.
- [x] Static alias chains, fallbacks, empty fallbacks, scoped alternative definitions and dependency cycles preserve browser semantics and terminate analysis.
- [x] A dead utility releases sole support dependencies; shared support needed by live rules remains. Uncertain scope choices are conservative.
- [x] Two providers with identical authored property names remain independent; explicit external properties retain authored spelling and integrate with literal external CSS/runtime values under the existing name-origin contract.
- [x] Runtime custom-property access through existing checked getters remains usable; property-only accessors activate the correct provider without inventing class presence.

## Validation

- Processor/internal/bundler tests verify occurrence spans, property/registration rewriting, liveness fixed points and conditional references.
- Optimized integration fixture with overlapping provider names and external variables asserts correct values and pruned sole dependencies.

## Evidence

- Registrations, declaration names, variable reads and conditional property keys now share canonical names. Literal query values stay unchanged. Explicit external properties are validated, remain outside managed ownership and cannot become owned imports. Registered initial values participate in URL discovery.
- Optimized reachability grows property dependencies to a fixed point, preserving aliases, fallbacks, alternative definitions and cycles without inlining. Unneeded general initialization and registrations disappear. Checked property-only values retain their provider/default registration without class presence.
- A focused failing regression exposed that a selected variable-only utility must retain its assignment and imported provider. The fix shares the positive-class guarantee between compilation and pruning, preserves class-bound assignments and live keyframe assignments, and limits default-initialization trimming accordingly. The canonical regression now passes.
- Dynamic javac tests verify registration-only accessors, condition/literal distinctions, external property diagnostics and canonical imports. Unit graph cases cover aliases, missing fallbacks, cycles, dead sole dependencies and property-only activation. Real integration remains green.
- Browser checks pass with actual registrations: an owned property-only getter yields its registered 17px default while its unexposed class disappears; two providers keep independent values, external --host-gap remains literal, inherited aliases preserve browser evaluation and an unused 97px default disappears in optimized output.
- The broad internal/compiler/bundler/integration run passed 12 targets. After the assignment fix, all four affected targets passed and the optimized browser result remained valid. Formatting, buildifier, git diff --check and domain links passed. Durable getter/assignment obligations were reconciled in bindings/liveness; reader guidance was updated.
