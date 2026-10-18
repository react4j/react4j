# T01 — Inherited Java accessors

- Status: complete
- Blocked by: None
- Spec coverage: R1/AC1, R2/AC2, R5/AC5 (plain methods and factory conflicts)

## Delivers

Component CSS bindings implement ordinary and generic Java interfaces through inherited abstract methods, while resource marker/default interfaces and normal helpers preserve their existing behavior.

## Acceptance criteria

- [x] Inherited root/modifier methods generate public implementations without redeclaration, with independent owner names.
- [x] Substituted signatures satisfy current accessor rules or produce author diagnostics; generic owner/method exclusions remain.
- [x] Interface fields and non-inherited static/private methods do not enter declared-state validation; defaults remain helpers.
- [x] Resource unsupported abstract methods and inherited factory conflicts fail clearly; marker/default-only resources remain resource-only.
- [x] Positive/negative processor tests and paired goldens prove these behaviors without weakening existing constraints.

## Validation

Run CSS implementation and assembled-jar processor suites, focused units and Java formatting. Review golden diffs and emitted output sets.

## Evidence

Both assembled-jar and implementation processor suites pass, including inherited/generic accessors, covariance in both interface orders, invalid signatures, default root re-abstraction, ordinary helpers/constants, resource-only interfaces and inherited factory conflicts. tools/update_goldens.sh css regenerated and verified 16 new output files for two independently named implementations, in both formatting modes. All six processor unit targets, tools/java_format.sh check and buildifier_check passed. Golden Java/CSS and source maps were inspected.
