# T01 — Ordered multiple callbacks

- Status: complete
- Blocked by: None
- Spec coverage: R1–R4, R6; AC1 (processor), AC2, AC4 (processor)

## Delivers

Multiple callbacks with child-controlled participation and phase-specific inherited positions, while keeping the old combined annotation functional until T02.

## Acceptance criteria

- [x] All three phases generate ordered callback lists; dual callbacks run once per applicable phase.
- [x] Inherited registration positions survive overrides; disabling/reintroducing and newly added phases follow the spec.
- [x] Class/interface precedence, interface diamonds, declaration order, overload traps, and separately compiled ancestors have meaningful fixtures.
- [x] Existing validation, Arez actions, deprecated-member handling and no-render generation remain checked.

## Validation

- Processor goldens and diagnostics, then `bazel test //processor:all_tests --test_output=errors`.
- Regenerate only affected goldens with the repository updater; inspect complete generated output and source/build lists.

## Evidence

- `tools/update_goldens.sh generated`: passed; reviewed exact source/compiled callback sequences and complete formatted/unformatted output.
- `bazel test //processor:all_tests --test_output=errors`: all 7 targets passed, including strict generated-body checks, diagnostics and checker negative control.
- Formatter, buildifier, and `git diff --check`: passed.
- Used pinned Bazel 9.3.1 with an isolated output base; no product configuration change was needed.
