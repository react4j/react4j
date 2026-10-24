# T01 — Expand checked rules through the isolated JVM facade

- Status: `pending`
- Blocked by: `None`
- Spec coverage: `R1`–`R3`, base `R5`, `AC1`

## Delivers

A real pure-JVM compiler facade in its own package that prepares, validates/binds, describes and expands literals, spacing/color families and selector/condition variants with immutable descriptors and structured origins/errors.

## Acceptance criteria

- [ ] Establish explicit Bazel package/visibility/dependencies, JVM tests and no reverse processor dependency.
- [ ] Parse marker, tokens/scales, literal/family heads, source alternatives, template declarations/holes, supporting CSS and one-slot variants. Check all definitions, collisions and forbidden syntax.
- [ ] Split candidates with balanced CSS quoting/brackets/escapes; decode underscores; match exact/longest-prefix; preserve explicit negative rules and written selector order/specificity.
- [ ] Expand representative Rose spacing/colors/nth/descendant forms and static media/supports conditions; reject unguarded selectors, malformed values and hidden declaration injection.
- [ ] Describe finite/infinite domains; preserve code/origin/related locations and deterministic serialized descriptors. Reuse does not retain earlier candidates; bad versions fail.
- [ ] Focused tests prove typed-hole parsing/flattening and supporting-origin feasibility through the actual facade, not a throwaway parser.

## Validation

- `bazel test //css/utility-language:tests --test_output=errors` — exercises the external facade with positive and negative cases, batch failures, roundtrip/version checks and order-independent compilation.
- Narrow formatter/buildifier/checker checks and package dependency audit.

## Evidence

- `pending`
