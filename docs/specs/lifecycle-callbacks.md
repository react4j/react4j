# Lifecycle Callback Specification

## Purpose

Define participation and invocation order for a view's commit-phase callbacks across Java class and interface inheritance.

## Requirements

1. A view may have zero or more `@PostMount`, `@PreUpdate`, and `@PostUpdate` callbacks.
2. A method may participate in several phases by carrying each phase's annotation.
3. Each participating method is invoked once per applicable lifecycle event, using normal Java virtual dispatch.
4. Participation follows the effective Java method declaration's annotations. An unannotated override disables inherited participation; annotations on an interface method do not activate an unannotated class implementation.
5. Invocation positions follow declaration order: visit the superclass hierarchy first, then each type's interfaces in written order with interface ancestors first, then that type's own declarations. Visit shared interface ancestors once. This order applies to source and separately compiled ancestors.
6. The first declaration in that traversal annotated for a phase reserves the method's position in that phase. An override retains that position when it participates, including when an intermediate override disabled participation.
7. A phase first introduced by an override takes that declaration's position. Unannotated declarations and unrelated overloads reserve no callback positions.
8. Mount callbacks run synchronously after mounting.
9. Pre-update callbacks run synchronously before DOM changes are committed.
10. Post-update callbacks run synchronously after those changes.
11. Existing input-change notifications for a phase run before its callbacks.
12. An unchecked exception stops the remaining callbacks in that phase and propagates to the lifecycle caller.
13. An immutable disposable input disposed before view construction suppresses construction and all mount callbacks.
14. A disposed view invokes no pre-update or post-update callbacks.

## Architectural basis

[Preserve lifecycle registration order](../adr/0007-preserve-lifecycle-registration-order.md).
