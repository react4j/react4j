# T01 — Inherited Java accessors

- Status: pending
- Blocked by: None
- Spec coverage: R1/AC1, R2/AC2, R5/AC5 (plain methods and factory conflicts)

## Delivers

Component CSS bindings implement ordinary and generic Java interfaces through inherited abstract methods, while resource marker/default interfaces and normal helpers preserve their existing behavior.

## Acceptance criteria

- [ ] Inherited root/modifier methods generate public implementations without redeclaration, with independent owner names.
- [ ] Substituted signatures satisfy current accessor rules or produce author diagnostics; generic owner/method exclusions remain.
- [ ] Interface fields and non-inherited static/private methods do not enter declared-state validation; defaults remain helpers.
- [ ] Resource unsupported abstract methods and inherited factory conflicts fail clearly; marker/default-only resources remain resource-only.
- [ ] Positive/negative processor tests and paired goldens prove these behaviors without weakening existing constraints.

## Validation

Run CSS implementation and assembled-jar processor suites, focused units and Java formatting. Review golden diffs and emitted output sets.

## Evidence

Pending.
