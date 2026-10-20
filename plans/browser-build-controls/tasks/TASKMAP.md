# Task map

- Spec: [SPEC.md](../SPEC.md)
- Status: implementing
- Current frontier: T02
- Plan checkpoint: automatic; user explicitly authorized implementation, no human checkpoint, planning review passed.
- Planning reviewer: browser_controls_planning; 1/3 rounds consumed, final confirmation unused; Findings: none. Complete scope, controls/defaults, optional HTML/config, assembly/cache boundaries, Rose delivery contracts and blocked full-Rose gate checked.
- Implementation reviewer: pending (0/5; final confirmation unused)

| Task | Status | Blocked by |
| --- | --- | --- |
| [T01 Independent browser application settings](T01-controls.md) | complete | None |
| [T02 Development overhead and cache boundaries](T02-performance.md) | pending | T01 |
| [T03 Rose public application delivery](T03-rose.md) | pending | T02 |
| [T04 Validation and documentation](T04-validation.md) | pending | T03 |

## Full-scope gate

React4j full gate plus Rose adapter fixture tests, compressed/uncompressed/local configured graphs and performance evidence. Pending. Shared-library blocked whole-Rose gates remain explicit rather than waived or reported passing.

## Sequencing

Primary implements. Independent review, PR publication and exact tree deletion are workflow gates after task completion. Avoid formatter chat-owned work; inspect git status before edits/commits. React4j commit must be published before Rose’s final immutable pin.

## Knowledge promotion

Pending.
