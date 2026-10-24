# T05 — Apply container and starting-style variants

- Status: `pending`
- Blocked by: `T03`, `T04`
- Spec coverage: `R5`, `R8`, `AC5`

## Delivers

Checked utilities respond to size, custom-property style and scroll-state containers, and supply starting styles, with correct independent name/liveness/origin behavior.

## Acceptance criteria

- [ ] Admit static checked wrappers/boolean conditions and typed dimension/ratio threshold holes, including named tokens and bracket values; reject forbidden name/operator/property/whole-condition holes.
- [ ] Bind owned names from all templates and explicitly external containers/properties first used downstream. A query does not instantiate/retain a setup class.
- [ ] Cover stuck, snapped, scrollable and scrolled grammar; preserve each nested query, mixed wrapper order and selector specificity.
- [ ] Emit conditionless starting-style with explicit transition utilities and normal cascade, with useful malformed-condition/slot diagnostics.
- [ ] Map and compress names inside nested conditions, including a valid named query whose setup getter is absent.

## Validation

- Language/processor/internal/linker/bundler tests for conditional grammar, order, ownership, source locations and absent setup pruning.
- Browser tests using actual generated classes for container size/style changes, a scroll-state scenario and an entering opacity transition, development and optimized.

## Evidence

- `pending`
