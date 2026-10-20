# Task map

- Spec: [SPEC.md](../SPEC.md)
- Status: implementing
- Current frontier: T04
- Planning reviewer: dependency_aliases_planning; 1/3 rounds consumed, final confirmation unused. Findings: none; complete plan, native source owners, patch export, standalone Replicant configuration, visibility, existing checker producers and delivery order verified.
- Plan checkpoint: automatic; user explicitly authorized implementation and planning review passed.
- Implementation reviewer: pending (0/5 rounds; final confirmation unused)

| Task | Status | Blocked by |
| --- | --- | --- |
| [T01 Dependency facade](T01-facade.md) | complete | None |
| [T02 Canonical runtime compilation](T02-runtime.md) | complete | T01 |
| [T03 Rose convergence](T03-rose.md) | complete | T02 |
| [T04 Verification and publication](T04-delivery.md) | in_progress | T03 |

## Full-scope gate

Pending full Root gate, affected Replicant checks, real Rose links and delivery fixtures, public boundary checks, canonical promotion, review and remote pin validation.

## Sequencing

Primary implements; independent reviewers remain read-only. Keep existing Root/Rose checkouts clean of unrelated changes. No edits to immutable Bazel caches. Root must be published before final Rose pin resolves remotely. New dependency patches can be used in both consumers through their existing configuration paths; no new dependency framework.

## Promoted knowledge

Pending classification during T04. Public alias inventory remains owned by BUILD/consumer checks; durable identity and caller-selection requirements belong with existing Bazel integration contracts.
