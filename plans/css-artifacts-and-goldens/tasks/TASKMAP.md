# Task map

- Spec: [SPEC.md](../SPEC.md)
- Status: reviewed
- Current frontier: None (closeout)
- Planning reviewer: /root/planning_review (1/3 rounds), Findings: none
- Plan checkpoint: automatic — accepted immediate scope and latest user execution instruction; passing planning review
- Implementation reviewer: /root/implementation_review (1/5 rounds), Findings: none. Reran all three CSS targets uncached; release checks passed cached. Inspected complete delivery and supplied full-gate/reproducibility/updater/drift evidence.

## Full-scope validation

- Gate: tools/check.sh plus css/check_reproducibility.sh and assembled processor jar repeatability
- Evidence: tools/check.sh passed (23 default, 6 optimized browser, 9 coverage targets; consumers/site/release; 13 repeatable artifacts). CSS shaded jar/srcjar/CSS independently reproduced. CSS and existing regression updaters idempotent; golden drift and strict-body negative controls behaved as intended.

## Tasks

| ID | Task | Status | Blocked by |
| --- | --- | --- | --- |
| T01 | [Subproject layout](T01-layout.md) | complete | None |
| T02 | [Standalone shaded artifact](T02-artifact.md) | complete | T01 |
| T03 | [Golden harness and delivery](T03-goldens.md) | complete | T02 |

## Sequencing and ownership

Each task stays green and commits its state. T01 owns target/CI/format/path migration and overview docs; T02 owns artifact construction/isolation; T03 owns fixtures, strict checks, updater, final docs, full gate, draft PR and cleanup. No production language expansion.

## Promoted knowledge

No domain documentation directories exist; no domain-modeling promotion required. Durable conclusions recorded in [CSS README](../../../css/README.md) and [code generation guide](../../../docs/agents/code-generation.md). User instruction to keep #112 draft overrides the workflow default of making a published PR merge-ready.
