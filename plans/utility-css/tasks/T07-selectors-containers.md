# T07 — Preserve advanced selector and container behavior

- Status: complete
- Blocked by: T06
- Spec coverage: R5 (containers/markers), R6 (external hooks/containers), R9 (selectors/conditions), R11 (safe pruning); AC3 (containers), AC4, AC5

## Delivers

Group/peer, relational and arbitrary-selector utilities, named container conditions and starting styles behave correctly after renaming and optimized pruning.

## Acceptance criteria

- [x] Full selector traversal treats an escaped utility token as one identity and embedded actual class references as separate identities, including nested selector functions and marker-only classes.
- [x] Conservative liveness preserves specificity/negation semantics; dead known positive requirements can eliminate impossible rules/list branches. Ordinary data/ARIA selectors remain unchanged.
- [x] Managed class-token attribute membership rewrites only where equivalent; unsafe exact/broad class-string comparisons fail clearly. Collision checking uses actual binding order for multi-token values.
- [x] Infer container names from container-name/container declarations, resolve named queries, rename declarations/preludes consistently, preserve explicit external ancestors and isolate overlapping library names.
- [x] Container query references do not retain an otherwise dead utility establishing a container. Managed property references inside query conditions participate in dependency/naming/mapping.
- [x] Starting-style and combinations of supported conditions are traversed throughout compilation, pruning, renaming and maps.

## Validation

- Retained focused fixtures preserve Rose's actual external-hook, child/sibling, is/not/where/has and arbitrary-selector shapes.
- Optimized tests compare live/dead group/peer markers, selector lists and named container references without false DOM-presence activation.
- Browser checks resize an actual container, exercise an external ancestor name, and validate starting-style handling alongside preserved source maps.

## Evidence

- Development and `-c opt` SaveButton browser suites passed. They resize a named ancestor beyond an external nearest container, prove independent overlapping names in two libraries, exercise group/peer and token membership, preserve starting-style behavior, and omit an unused container utility despite its live query reference.
- Packaged processor suite passed 609/609 cases. Processor/internal/bundler/unit and actual Java/J2CL integration targets passed (12 targets), including header-jar contracts and file origins. Source-map assertions locate a rewritten query name at its authored Java column.
- The final internal suite passed after adding escaped late-variant container collisions. Liveness regressions cover positive group/peer/has/membership absence, retained negation, list-branch removal and unchanged data selectors. Collision regressions use actual cross-owner getter order and stable fragments.
- `tools/update_goldens.sh css` regenerated 66 files; structural comparison proves their sole change is metadata version 9 to 10. Its final run exposed the new selector-macro test; after correcting the parser function spelling, the packaged suite passed. No expected output was hand-edited.
- Java formatting, `git diff --check` and domain link validation passed. Specification-only audit: utility-input requirement 12 owns safe membership; names requirement 4 owns actual getter order; existing names and liveness requirements own scoped containers and query absence. Remaining implementation details stay in code and reader grammar limits in css/README.md.
