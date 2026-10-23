# T07 — Preserve advanced selector and container behavior

- Status: pending
- Blocked by: T06
- Spec coverage: R5 (containers/markers), R6 (external hooks/containers), R9 (selectors/conditions), R11 (safe pruning); AC3 (containers), AC4, AC5

## Delivers

Group/peer, relational and arbitrary-selector utilities, named container conditions and starting styles behave correctly after renaming and optimized pruning.

## Acceptance criteria

- [ ] Full selector traversal treats an escaped utility token as one identity and embedded actual class references as separate identities, including nested selector functions and marker-only classes.
- [ ] Conservative liveness preserves specificity/negation semantics; dead known positive requirements can eliminate impossible rules/list branches. Ordinary data/ARIA selectors remain unchanged.
- [ ] Managed class-token attribute membership rewrites only where equivalent; unsafe exact/broad class-string comparisons fail clearly. Collision checking uses actual binding order for multi-token values.
- [ ] Infer container names from container-name/container declarations, resolve named queries, rename declarations/preludes consistently, preserve explicit external ancestors and isolate overlapping library names.
- [ ] Container query references do not retain an otherwise dead utility establishing a container. Managed property references inside query conditions participate in dependency/naming/mapping.
- [ ] Starting-style and combinations of supported conditions are traversed throughout compilation, pruning, renaming and maps.

## Validation

- Retained focused fixtures preserve Rose's actual external-hook, child/sibling, is/not/where/has and arbitrary-selector shapes.
- Optimized tests compare live/dead group/peer markers, selector lists and named container references without false DOM-presence activation.
- Browser checks resize an actual container, exercise an external ancestor name, and validate starting-style handling alongside preserved source maps.

## Evidence

- pending
