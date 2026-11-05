# Task map

- Spec: [SPEC.md](../SPEC.md)
- Status: `implementation-reviewed`
- Current frontier: none
- Planning reviewer: planning_review passed (1/3 attempts). Findings: none. Reviewed scope, dependency path, acceptance coverage, and project gate.
- Plan checkpoint: automatic (complete). The user authorized execution. No intermediate approval was requested.
- Implementation reviewer: implementation_review passed (1/5 attempts). Findings: none. Reviewed R1–R5 and AC1–AC5, independently probed mixed and opaque nested groups, selector parsing, invalid placement, preserved shared output and maps, and verified the full gate evidence and restored Chromium configuration.

## Tasks

| ID | Task | Status | Blocked by |
| --- | --- | --- | --- |
| T01 | [Carry nested declarations through CSS tooling](T01-nested-declarations.md) | complete | None |

## Full-scope validation

- Gate: `tools/check.sh`
- Evidence: `tools/check.sh` passed. The task records the exact coverage and local browser setup.

## Promoted knowledge

- [CSS nesting specification](../../../docs/specs/css-nesting.md) owns the accepted matching, ordering, placement and toolchain requirements. The pinned parser patch extends the existing build path. The CSS authoring guide and changelog describe the user-visible change. Domain link validation passed.
