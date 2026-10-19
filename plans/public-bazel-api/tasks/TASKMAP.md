# Task Map

- Spec: [SPEC.md](../SPEC.md)
- Status: closing
- Current frontier: closeout
- Plan checkpoint: automatic (explicit implementation authority, verified user steering and passing revised planning review)
- Planning reviewer: /root/public_bazel_api_planning (2/3 rounds consumed; final confirmation unused); revised plan passed round2, Findings: none.
- Implementation reviewer: /root/public_bazel_api_review (2/5 rounds consumed; final confirmation unused); round2 passes, Findings: none.

## Full-scope validation

- Gate: `tools/check.sh`, complete public-target/file/load audit and independent review.
- Evidence: tools/check.sh passes in the final native/visibility configuration: 45 default suites, eight optimized browser suites, 23 JVM coverage suites, runtime coverage and all floors; external positive/negative matrix in both modes; complete site validation, unsigned release checks and independent publication reproducibility. Buildifier, shell syntax, dependency regeneration, domain links and complete target/file/load audits also pass. Simplify completed before review.

## Tasks

| ID | Task | Status | Blocked by |
| --- | --- | --- | --- |
| T01 | [Native J2CL integration](T01-native-j2cl.md) | complete | None |
| T02 | [J2CL-first root API](T02-labels.md) | complete | T01 |
| T03 | [Enforced downstream boundary](T03-visibility.md) | complete | T02 |
| T04 | [Contract and delivery](T04-delivery.md) | complete | T03 |

## Sequencing notes

Native J2CL integration removes the need for a public fixed-policy checked-library helper before the root names migrate. Then enforce visibility and finally update docs/full validation before fresh implementation review. Workflow owns PR112 publication and exact plan-tree cleanup. The pending optional preference now concerns only application/browser-test conveniences.

## Planning review history

- Round1: Findings none for the initial plan.
- New user steering: asks why separate JVM jars/checker policy are required. Experiments prove native J2CL supports caller Error Prone/NullAway, but JS-only links skip javac unless validated. A metadata-only Closure dependency attaches assets without the checked helper. Supersede the planned combined-provider adapter and public helper; revise all affected slices and return to planning review before implementation.

- Round2: Findings none. Reviewed revised native J2CL integration, superseded adapter, checker/asset probes, validation propagation acceptance, all four slices and visibility/docs/closeout ownership.

## Promoted knowledge

- Entry labels, target/file/load visibility and generated support targets are mutable build configuration owned by [BUILD](../../../BUILD.bazel) and the existing [boundary check](../../../tools/check_bazel_consumer.sh); [Project Setup](../../../docs/project_setup.md#configure-bazel) is the downstream reader's owner.
- Native J2CL checker selection, assertion refinements and optional application/test wrappers belong in Project Setup; the internal Java 17/unstripped compilation and JVM/publication obligations belong in [CONTRIBUTING](../../../CONTRIBUTING.md#browser-and-distribution-boundaries) and the [release guide](../../../tools/release/README.md).
- Ordinary J2CL asset dependencies belong in the [CSS guide](../../../css/README.md#build-integration). Existing asset, liveness and source-map specifications retain their behavior unchanged.
- Domain classification: these conclusions already have durable code/reader-documentation owners, so specification qualification criterion 4 does not hold. Native build wiring and private alias implementation are reversible details; no decision passes all ADR criteria. No new glossary term or user-authorized deferred issue is introduced. Domain link validation passes; the complete site gate validates the changed reader docs.

## Implementation review history

- Round1: one P2 finding: blanket Java 17 wording obscures the native J2CL path. Personally verified native J2CL class major65 and publication class major61. Qualify setup/contributor/IDE/release/changelog wording: publication and JVM verification use Java 17, generated sources remain compatible, pinned J2CL intermediate compilation uses Java 21. No implementation behavior changes.
- Fix validation: complete site build/check passes (27032 local file/fragment links, zero broken); domain links and diff checks pass. Prior full implementation gate remains applicable; only reader documentation and plan evidence changed. Existing durable-owner classification is unchanged.

- Round2: Findings none. Reviewer independently confirms the documentation fix and Java 21/17 bytecode distinction, clean scope/diff and passing 27032-link site check. Prior complete implementation review/full-gate coverage remains applicable; no residual risk identified.

## Closeout gate

All four tasks and their checks pass. Two planning rounds and two implementation review rounds consumed; neither final confirmation allowance used. Final knowledge sweep reuses the recorded classifications: bytecode wording clarifies existing build configuration; no new qualifying domain artifact or unpromoted conclusion remains. Canonical reader docs and their links are validated. Implementation and evidence commits are in history. Remove only plans/public-bazel-api, commit its deletion and publish PR112; never merge.
