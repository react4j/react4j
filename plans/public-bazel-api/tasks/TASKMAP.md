# Task Map

- Spec: [SPEC.md](../SPEC.md)
- Status: implementing
- Current frontier: T04
- Plan checkpoint: automatic (explicit implementation authority, verified user steering and passing revised planning review)
- Planning reviewer: /root/public_bazel_api_planning (2/3 rounds consumed; final confirmation unused); revised plan passed round2, Findings: none.
- Implementation reviewer: pending (0/5 rounds; final confirmation unused)

## Full-scope validation

- Gate: `tools/check.sh`, complete public-target/file/load audit and independent review.
- Evidence: pending.

## Tasks

| ID | Task | Status | Blocked by |
| --- | --- | --- | --- |
| T01 | [Native J2CL integration](T01-native-j2cl.md) | complete | None |
| T02 | [J2CL-first root API](T02-labels.md) | complete | T01 |
| T03 | [Enforced downstream boundary](T03-visibility.md) | complete | T02 |
| T04 | [Contract and delivery](T04-delivery.md) | pending | T03 |

## Sequencing notes

Native J2CL integration removes the need for a public fixed-policy checked-library helper before the root names migrate. Then enforce visibility and finally update docs/full validation before fresh implementation review. Workflow owns PR112 publication and exact plan-tree cleanup. The pending optional preference now concerns only application/browser-test conveniences.

## Planning review history

- Round1: Findings none for the initial plan.
- New user steering: asks why separate JVM jars/checker policy are required. Experiments prove native J2CL supports caller Error Prone/NullAway, but JS-only links skip javac unless validated. A metadata-only Closure dependency attaches assets without the checked helper. Supersede the planned combined-provider adapter and public helper; revise all affected slices and return to planning review before implementation.

## Promoted knowledge

Pending classification against existing docs/specs and ADRs; mutable label inventory belongs with root BUILD and reader setup documentation.

- Round2: Findings none. Reviewed revised native J2CL integration, superseded adapter, checker/asset probes, validation propagation acceptance, all four slices and visibility/docs/closeout ownership.
