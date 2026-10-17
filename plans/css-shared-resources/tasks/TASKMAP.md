# Task Map

- Spec: [SPEC.md](../SPEC.md)
- Status: implementing
- Current frontier: T03
- Planning reviewer: planning_review (3/3 rounds; passed; final confirmation unused)
- Plan checkpoint: automatic; latest user implementation authority and passing planning review
- Implementation reviewer: pending (0/5 rounds; final confirmation unused)
- Authority: latest user instruction authorizes implementation and heavy testing; no human plan checkpoint.

## Tasks

| ID | Task | Status | Blocked by |
| --- | --- | --- | --- |
| T01 | [Shared keyframes](T01-keyframes.md) | complete | None |
| T02 | [Global selection](T02-globals.md) | complete | T01 |
| T03 | [Examples and complete validation](T03-examples.md) | pending | T02 |

## Full-scope validation
Gate: tools/check.sh. Evidence: pending.

## Promoted knowledge
Not required unless canonical domain directories are present.

## Planning review history
Round 1: one blocker concerning overlapping root/dependency cascade order. Resolved by specifying first-completed dependency-first traversal, legacy shared files first, and explicit overlapping-root computed-style coverage. Round 2: Findings: none. Complete initial plan coverage and the ordering correction verified.

Implementation evidence requires a plan revision: Bazel header jars omit .css.json resources, so imports cannot read resource sidecars on the compilation classpath. Revised declaration annotations use CLASS retention; sidecars remain the bundler contract. Round 3: Findings: none. Reviewer verified full jars contain CSS sidecars and header jars contain only SharedPulse.class; CLASS-retained declaration contract and testing responsibilities accepted.
