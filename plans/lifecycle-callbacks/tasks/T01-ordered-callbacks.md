# T01 — Ordered multiple callbacks

- Status: pending
- Blocked by: None
- Spec coverage: R1–R4, R6; AC1 (processor), AC2, AC4 (processor)

## Delivers

Multiple callbacks with child-controlled participation and phase-specific inherited positions, while keeping the old combined annotation functional until T02.

## Acceptance criteria

- [ ] All three phases generate ordered callback lists; dual callbacks run once per applicable phase.
- [ ] Inherited registration positions survive overrides; disabling/reintroducing and newly added phases follow the spec.
- [ ] Class/interface precedence, interface diamonds, declaration order, overload traps, and separately compiled ancestors have meaningful fixtures.
- [ ] Existing validation, Arez actions, deprecated-member handling and no-render generation remain checked.

## Validation

- Processor goldens and diagnostics, then `bazel test //processor:all_tests --test_output=errors`.
- Regenerate only affected goldens with the repository updater; inspect complete generated output and source/build lists.

## Evidence

Pending.
