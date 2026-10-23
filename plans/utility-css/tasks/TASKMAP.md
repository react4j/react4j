# Task Map

- Spec: [SPEC.md](../SPEC.md)
- Status: implementation-review
- Current frontier: follow-up implementation review
- Planning reviewer: utility_planning_review (1/3 rounds; passed; final confirmation unused)
- Plan checkpoint: automatic
- Implementation reviewer: utility_implementation_review (1/5 rounds consumed; both findings fixed; follow-up pending; final confirmation unused)
- Scope source: confirmed utility CSS interview in this chat.
- Execution authority: "Use structured-delivery-workflow and proceed"; full delivery authorized after interview confirmation, with no human plan checkpoint.
- Active plan tree: plans/utility-css. This exact path is the only plan-edit/commit/deletion target for this invocation.

## Full-scope validation

- Gate: tools/check.sh
- Evidence: tools/check.sh passed on 2026-10-23 after T08 fixes: 50/50 normal test targets, 15/15 optimized browser targets, 23/23 instrumented JVM targets; coverage thresholds, formatting, hermeticity, public artifacts, site build/check and unsigned local package validation (12 artifacts, 60 entries). Domain links and final specification-only/diff audits passed.
- Review-fix validation: tools/check.sh passed on 2026-10-23 after both fixes, including 50 normal targets, 15 optimized browser targets, 23 instrumented JVM targets, unchanged coverage thresholds, site checks and local unsigned packaging. All 611 packaged processor methods passed. New browser coverage proves real property-transition interpolation across naming variants; processor coverage verifies original map locations and unsafe-registration diagnostics. Documentation links and specification-only/diff audits passed.

## Tasks

| ID | Task | Status | Blocked by |
| --- | --- | --- | --- |
| T01 | [Parse and trace modern supplied CSS](T01-modern-syntax.md) | complete | None |
| T02 | [Bind inline utility styles through Java and linking](T02-inline-bindings.md) | complete | T01 |
| T03 | [Compile explicit files and dependency catalogues](T03-file-catalogues.md) | complete | T02 |
| T04 | [Activate checked globals with correct precedence](T04-global-inputs.md) | complete | T03 |
| T05 | [Retain and rename property support](T05-property-support.md) | complete | T04 |
| T06 | [Retain animation and font variable dependencies](T06-animation-font-support.md) | complete | T05 |
| T07 | [Preserve advanced selector and container behavior](T07-selectors-containers.md) | complete | T06 |
| T08 | [Verify complete applications and document delivery](T08-application-delivery.md) | complete | T07 |

## Sequencing notes

- Select the lowest-numbered unblocked pending task. Each slice must pass its acceptance checks before its implementation commit and dependent work.
- T01 proves the syntax/traversal seam and promotes accepted domain contracts before code consumers expand. T02 demonstrates basic author-to-link behavior; later tasks extend that same path.
- Retain existing interfaces and nested component behavior throughout. Do not introduce compatibility paths or new public controls for internal capabilities.
- No Rose source migration or external installation is needed for delivery. Retained conformance fixtures, unlike temporary compiler experiments, have continuing regression value.
- Cross-cutting tests/maps/docs accompany each slice; T08 owns full-gate convergence and final reader documentation. Workflow closeout owns plan deletion after promotion and implementation-review pass.

## Review history

- Planning attempt 1: utility_planning_review returned "Findings: none." Checked the complete spec/task tree, external exclusions, repository policies, parser/traversal, bindings, header boundaries, naming/liveness, cascade, maps/assets and full-gate wiring. Residual implementation risks are owned by T01 and T03. Budget 3 attempts plus one final confirmation; 1 consumed; final confirmation unused.
- Automatic checkpoint basis: confirmed interview scope, subsequent explicit instruction to proceed through structured delivery, no human review pause requested, and passing independent planning review.
- Implementation attempt 1: utility_implementation_review inspected the complete delivery at 58869334c and verified the full gate. Two P2 findings: custom-property transition targets were omitted from shared naming/occurrence discovery (R5/R9), and literal-constrained registrations could be invalidated by animation/font name-carrier rewrites (R9/R10). Both accepted for fixes at the shared property-reference and utility name-proof seams. No scope or significant decision changes. Budget 5 attempts plus one final confirmation; 1 consumed, 4 normal attempts remain; final confirmation unused.

## Promoted knowledge

- T01 promotion: [utility input](../../../docs/specs/utility-css.md), [bindings](../../../docs/specs/css-bindings.md), [names](../../../docs/specs/css-names.md), [liveness](../../../docs/specs/css-liveness.md), [source maps](../../../docs/specs/css-source-maps.md), [glossary](../../../docs/glossary/README.md) and [finite-provider ADR](../../../docs/adr/0004-consume-finite-utility-libraries.md). Classification/history and specification-only audit recorded in T01; documentation link validation passed.
- Reader commands and API examples remain in css/README.md; changelog owns release-facing behavior. Task T08 verifies these owners before closeout.

## Review and closeout obligations

- All implementation tasks and the full gate are complete. Review the whole delivery against SPEC.md and promoted contracts with a fresh independent read-only reviewer, record each attempt and disposition, and pass only on Findings: none.
- After review passes, follow workflow closeout: check promotion/inbound references, remove this entire exact active plan tree, validate removal and commit. No push, publication, release or Rose migration is authorized.
- T08 final promotion audit: the utility-input, binding, name, liveness and map specifications, glossary and finite-provider ADR remain the canonical owners for every accepted durable conclusion. T08 adds reader examples, implementation fixes and evidence only; no new durable decision or deferred question is introduced.

- Attempt 1 dispositions: transition targets now use one shared property-value grammar for ownership, rewriting and occurrence metadata, including fallbacks and imported/external boundaries. Unsafe registered name-carrier literal constraints are diagnosed through definitions/aliases while universal/type-only registrations remain supported. Both original reproductions have retained regression coverage; no requirement or significant decision was weakened. The utility-input specification records registration validity; reader docs/changelog describe the boundary and transition support. Follow-up review pending.
