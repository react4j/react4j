# Task map

- Spec: [SPEC.md](../SPEC.md)
- Status: closing
- Current frontier: exact-tree cleanup, then ordered publication and remote pin verification
- Planning reviewer: dependency_aliases_planning; 1/3 rounds consumed, final confirmation unused. Findings: none; complete plan, native source owners, patch export, standalone Replicant configuration, visibility, existing checker producers and delivery order verified.
- Plan checkpoint: automatic; user explicitly authorized implementation and planning review passed.
- Implementation reviewer: dependency_aliases_review; 2/5 rounds consumed, final confirmation unused. Round 1 found one P2 omission: formatter write/watch commands retained upstream labels. Commit 042b370da fixed both through private aliases and passed focused checks. Round 2: Findings: none; prior full-scope coverage confirmed. Final remote Rose pin verification remains the explicit publication gate.

| Task | Status | Blocked by |
| --- | --- | --- |
| [T01 Dependency facade](T01-facade.md) | complete | None |
| [T02 Canonical runtime compilation](T02-runtime.md) | complete | T01 |
| [T03 Rose convergence](T03-rose.md) | complete | T02 |
| [T04 Verification and publication](T04-delivery.md) | complete | T03 |

## Full-scope gate

All Root tools/check.sh stages passed, with the documentation-only site link correction and affected suffix rerun recorded in T04. Standalone Replicant complete check passes (98 tests plus release checks). Real Acal/Rose development, production and diagnostic links, seven delivery tests, uncompressed fixture and WAR pass. Configured local/compression boundaries and warm no-op were verified; public inventory/visibility, canonical ownership and caller-owned checker failures pass externally. Root/Rose documentation validators and spec-only audit pass. Independent implementation review, exact-tree cleanup, publication and final normal remote pin validation are the remaining ordered closeout gates. Full authenticated Rose UI and full iBazel edit-latency parity remain unverified.

## Sequencing

Primary implements; independent reviewers remain read-only. Keep existing Root/Rose checkouts clean of unrelated changes. No edits to immutable Bazel caches. Root must be published before final Rose pin resolves remotely. New dependency patches can be used in both consumers through their existing configuration paths; no new dependency framework.

## Promoted knowledge

[Bazel integration](../../../docs/specs/bazel-integration.md) owns provider identity, shared runtime compilation, caller selection and private implementation boundaries. [Consumer setup](../../../docs/project_setup.md#configure-bazel) explains aliases and root configuration; BUILD/consumer checks own the exact inventory. Classification and specification-only audit passed; Root and Rose documentation validators passed. No qualifying ADR, glossary, use-case or deferred artifact was introduced.

Final knowledge sweep: the formatter correction adds only private tool wiring and introduces no new durable requirement or decision. Existing classifications remain valid. All tasks and local gates are complete; independent review passed. Remove only plans/dependency-aliases, publish Root, pin isolated Rose to that immutable SHA, resolve/build/test without a local override, publish the draft update and report any remaining CI/UI limits. No merge or deployment.
