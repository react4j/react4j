# Task Map

- Spec: [SPEC.md](../SPEC.md)
- Active tree: `plans/bazel-migration/`
- Status: `implementing`
- Current frontier: `T07`
- Planning reviewer: `/root/planning_reviewer` (`2/3` rounds; Findings: none)
- Plan checkpoint: `automatic` (user-confirmed grill-me understanding and passing planning review)
- Implementation reviewer: `pending` (`0/5` rounds)

## Full-scope validation

- Gate: `tools/check.sh`, with task-local recorded labels and unsigned release/site/coverage/Ahab checks.
- Evidence: pending.

## Tasks

| ID | Task | Status | Blocked by |
| --- | --- | --- | --- |
| T01 | [Pinned Bazel graph and maintenance tools](T01-bootstrap.md) | complete | None |
| T02 | [Java DOM and React asset generation](T02-generators.md) | complete | T01 |
| T03 | [Core JVM contracts and tests](T03-core.md) | complete | T02 |
| T04 | [DOM JVM contracts and tests](T04-dom.md) | complete | T03 |
| T05 | [Processor Proton and golden baseline](T05-processor-foundation.md) | complete | T04 |
| T06 | [Effective processor nullness and input rules](T06-processor-nullness.md) | complete | T05 |
| T07 | [Complete strict golden verification](T07-strict-goldens.md) | pending | T06 |
| T08 | [Maven-equivalent publication archives](T08-publication.md) | pending | T07 |
| T09 | [J2CL libraries from publication sources](T09-j2cl-libraries.md) | pending | T08 |
| T10 | [J2CL examples and browser verification](T10-browser-examples.md) | pending | T09 |
| T11 | [Java documentation site](T11-java-site.md) | pending | T10 |
| T12 | [Explicit release operations](T12-release-tools.md) | pending | T11 |
| T13 | [Contributor guidance and obsolete infrastructure removal](T13-docs-cleanup.md) | pending | T12 |
| T14 | [Shared CI gate and full verification](T14-ci-gate.md) | pending | T13 |

## Sequencing notes

- Graph/bootstrap precedes Java generation; core and generated DOM are needed before processor integration and its strict fixture checks. T05 captures test inventory; T06 changes contract semantics; T07 closes complete generated-code checking.
- Publication archives precede public J2CL validation; browser outputs precede site integration. Release operations depend on final archive/site shapes. Removal and documentation follow replacements; final Ahab/CI evidence covers the final graph.
- Temporary intermediate states are not final compatibility requirements. Each stage must pass its local validation; final gate is mandatory before review.
- Primary agent implements sequentially. Planning and implementation reviewers are read-only. No remote mutation or dependency-repository edits are authorized.
- Final closeout remains local: commit plan deletion after passing review; do not publish a branch or PR without a separate user request.

## Promoted knowledge

- not-required: no docs/adr, docs/glossary, docs/specs or docs/deferred directories exist at planning time. Record durable discoveries in existing agent/contributor guides.

## Planning review evidence

- Round 1: one blocker, T02 required generated DOM compilation before core/DOM existed. Corrected T02 to validate generator compilation, output signature/patch checks and determinism; T04 owns full generated DOM compilation.
- Round 2: Findings: none. Reviewer confirmed T02/T04 boundaries and ordering.
