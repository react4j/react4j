# Task Map

- Spec: [SPEC.md](../SPEC.md)
- Status: planned
- Current frontier: T01
- Planning reviewer: utility_planning_review (1/3 rounds; passed; final confirmation unused)
- Plan checkpoint: automatic
- Implementation reviewer: pending (0/5 rounds; final confirmation unused)
- Scope source: confirmed utility CSS interview in this chat.
- Execution authority: "Use structured-delivery-workflow and proceed"; full delivery authorized after interview confirmation, with no human plan checkpoint.
- Active plan tree: plans/utility-css. This exact path is the only plan-edit/commit/deletion target for this invocation.

## Full-scope validation

- Gate: tools/check.sh
- Evidence: pending

## Tasks

| ID | Task | Status | Blocked by |
| --- | --- | --- | --- |
| T01 | [Parse and trace modern supplied CSS](T01-modern-syntax.md) | pending | None |
| T02 | [Bind inline utility styles through Java and linking](T02-inline-bindings.md) | pending | T01 |
| T03 | [Compile explicit files and dependency catalogues](T03-file-catalogues.md) | pending | T02 |
| T04 | [Activate checked globals with correct precedence](T04-global-inputs.md) | pending | T03 |
| T05 | [Retain and rename property support](T05-property-support.md) | pending | T04 |
| T06 | [Retain animation and font variable dependencies](T06-animation-font-support.md) | pending | T05 |
| T07 | [Preserve advanced selector and container behavior](T07-selectors-containers.md) | pending | T06 |
| T08 | [Verify complete applications and document delivery](T08-application-delivery.md) | pending | T07 |

## Sequencing notes

- Select the lowest-numbered unblocked pending task. Each slice must pass its acceptance checks before its implementation commit and dependent work.
- T01 proves the syntax/traversal seam and promotes accepted domain contracts before code consumers expand. T02 demonstrates basic author-to-link behavior; later tasks extend that same path.
- Retain existing interfaces and nested component behavior throughout. Do not introduce compatibility paths or new public controls for internal capabilities.
- No Rose source migration or external installation is needed for delivery. Retained conformance fixtures, unlike temporary compiler experiments, have continuing regression value.
- Cross-cutting tests/maps/docs accompany each slice; T08 owns full-gate convergence and final reader documentation. Workflow closeout owns plan deletion after promotion and implementation-review pass.

## Review history

- Planning attempt 1: utility_planning_review returned "Findings: none." Checked the complete spec/task tree, external exclusions, repository policies, parser/traversal, bindings, header boundaries, naming/liveness, cascade, maps/assets and full-gate wiring. Residual implementation risks are owned by T01 and T03. Budget 3 attempts plus one final confirmation; 1 consumed; final confirmation unused.
- Automatic checkpoint basis: confirmed interview scope, subsequent explicit instruction to proceed through structured delivery, no human review pause requested, and passing independent planning review.
- Implementation: not started. Budget 5 attempts plus one final confirmation. Use a fresh reviewer independent of implementation.

## Promoted knowledge

- Pending T01: classify accepted obligations into utility-input, bindings, names, liveness and source-map specifications; update glossary for utility/container ownership and record finite-provider ADR rationale. Read relevant history and audit specification-only diff.
- Reader commands and API examples remain in css/README.md; changelog owns release-facing behavior. Task T08 verifies these owners before closeout.
