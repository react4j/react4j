# Ordered lifecycle callbacks

## Source

- Scope: issues [78](https://github.com/react4j/react4j/issues/78) and [79](https://github.com/react4j/react4j/issues/79), and the confirmed grill-me interview in this chat on 2026-10-26.
- Authority: the user confirmed the complete design and requested structured delivery followed by a reviewable PR. No human plan checkpoint was requested.
- Evidence: the annotation processor stores singleton callbacks; its generator already accepts dual mount/update annotations. Focused Java 21 probes confirmed that Proton groups methods by name and retains override slots, which cannot guarantee the accepted ordering.

## Problem and outcome

Views need several independently authored lifecycle callbacks, including callbacks shared between mount and update. Replace the combined annotation with the two phase annotations and make inheritance order predictable.

## Requirements

- R1: Permit zero or more callbacks for each of `PostMount`, `PostUpdate`, and `PreUpdate`. A dual mount/update callback runs once in each applicable phase.
- R2: Participation follows the effective Java declaration's annotations. An unannotated override disables inherited participation; a reannotated override runs once using virtual dispatch.
- R3: The first ancestor annotation for each phase reserves that callback's position. Later overrides retain that position, including removal and subsequent reintroduction. Newly introduced phase registrations append at their declaring type's position.
- R4: Traverse the complete superclass hierarchy before the current type, and each type's interfaces before its declarations. Interfaces follow written order, ancestors first, shared ancestors once. Within a type use declaration order. Unrelated overloads and unannotated methods do not reserve positions. Apply this to source and separately compiled ancestors.
- R5: Remove `PostMountOrUpdate` outright and migrate repository consumers/tests. Callers replace it with both phase annotations. There is no compatibility alias or new public ordering option.
- R6: Preserve existing method validation, input-change-before-callback order, synchronous invocation, exception propagation, Arez virtual interception, debug handling, no-render views, and disposed-dependency guards.

## Acceptance criteria

- AC1 (R1): Generated code and browser observations show multiple callbacks in all three phases and dual callbacks exactly once per phase.
- AC2 (R2–R4): Fixtures cover multilevel inheritance, overrides, disabled/reintroduced phases, newly added phases, interface defaults/diamonds, overload traps, and separately compiled ancestors. Expected invocation order is asserted explicitly.
- AC3 (R5): No live source/build reference to the removed annotation remains; obsolete tests are removed or migrated, public documentation and changelog explain the replacement.
- AC4 (R6): Existing strict generated-code checks pass; focused Arez and runtime cases protect the retained behavior. The complete repository gate passes.

## Significant decisions

| Decision | Rationale and impact | Review focus |
| --- | --- | --- |
| Deliver 78 and 79 together | Multiple callbacks let shared and phase-specific work coexist. This is a source-breaking annotation removal. | Migration and phase ordering |
| Child controls participation; first phase registration controls order | Protect ancestor sequencing while permitting overrides to opt out. Adding a new phase in a child appends for that phase. | Disable/reintroduce and phase-addition cases |
| Specify interface order | Retains supported default methods with deterministic diamonds and declaration order. | Effective Java method selection and interface precedence |

## Technical and testing decisions

- Keep ordering local to commit-phase hooks; leave inputs and render hooks on their current collectors. Match override families rather than method-name buckets, respecting Java class/interface precedence.
- Update descriptor presence/deprecation queries and generate ordered direct virtual calls. Retain the combined callback temporarily only during T01, then remove it in T02.
- Start with processor fixtures and diagnostics. Add checked Java browser scenarios for development and optimized production; run `tools/check.sh` as the final gate.
- Promote qualifying durable obligations to the existing domain-documentation system before implementation review, validate links, and remove this temporary plan at closeout.

## Boundaries

No deferred callbacks, render-hook changes, broad Proton refactor, release, downstream repository edits, or changes to `etc/secrets`.

## Open questions

None.
