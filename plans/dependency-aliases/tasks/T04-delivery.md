# Verification and publication

- Status: in_progress
- Blocked by: T03
- Spec coverage: R4/AC4

## Delivers

Simplify, promote durable public alias/compilation ownership contracts, document supported consumer usage, run full gates and independent review, remove exact plan tree and update existing PRs with final immutable pins.

Durable contract: [Bazel integration](../../../docs/specs/bazel-integration.md).

## Acceptance

- [ ] Assigned spec outcome is demonstrably satisfied.
- [ ] Validation passes with actual evidence and any independent limits recorded.

## Validation

Full Root tools/check.sh; documentation validators; scoped clean diffs; reviewed results; normal remote dependency resolution and clean worktrees.

## Evidence

Domain classification: provider/compilation identity, one shared runtime compilation, caller-owned version/checker selection and private implementation boundaries qualify as durable invariants independent of their implementation. No existing spec owns this contract; the new Bazel integration spec owns it. Alias inventory and mutable pins remain in BUILD/module configuration; setup instructions belong in project_setup; patch mechanics and run evidence remain code/this plan. Reusing native source owners is unsurprising and reversible, so no ADR qualifies; no new domain language, essential use case or deferred question is needed.

Simplify inspected both implementation ranges: explicit native aliases replace obsolete archive compilations; old handwritten spellings and example-local imports are removed; generated imports, Starlark loads and mandatory Bazel labels stay with their owners. No extra facade abstraction or compatibility path is needed. Spec-only audit passes: each statement expresses an accepted observable integration obligation, survives implementation replacement, has no glossary term needing a new definition, and contains no plan chronology or mutable inventory. Documentation link validators pass in Root and Rose, including vendored contexts. Pending full gate and independent review.
