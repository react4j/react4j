# T03 — Link independently compiled candidate fragments

- Status: `pending`
- Blocked by: `T02`
- Spec coverage: `R7`, integration `R6`, `AC3`

## Delivers

Independently requested candidates become one deterministic, named, prunable stylesheet at complete link without exposing native grammar to the bundler.

## Acceptance criteria

- [ ] Emit generic candidate fragments with full provider identity, descriptor fingerprint, candidate spelling, order key, CSS/origins and symbol/support dependencies.
- [ ] Collect from real compilation trees, deduplicate matching requests, reject conflicting fingerprints/content and unsupported metadata versions, and assemble provider layers in the accepted total order.
- [ ] Keep shared support definitions once; preserve tokens, source-map origins, selected assets/imports and canonical class identities across consumer compilation order.
- [ ] Integrate link naming/validation and development retention; production removes candidates referenced only by dead getters and preserves local precedence.
- [ ] Missing fragments/metadata fail rather than generating an incomplete sheet or silently enumerating a family.

## Validation

- Internal/linker/bundler tests with two independent compilation outputs, duplicate candidates, descriptor mismatches, multiple CSS variants and exact source/name metadata.
- Native J2CL development and ADVANCED liveness integration through existing build rules; finite regression suites.

## Evidence

- `pending`
