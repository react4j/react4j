# T04 — Accept broader typed values and numeric limits

- Status: `pending`
- Blocked by: `T01`
- Spec coverage: `R3`, `R4`, `AC4`

## Delivers

Library authors expose the full accepted type set and optional input bounds; users select compact scalar or complete bracketed values with reliable early errors and explicit browser uncertainty.

## Acceptance criteria

- [ ] Support integer, number, percentage, length, length-percentage, angle, time, ratio, string, color, shadow, image, position, grid-tracks and an+b with the specified supported function boundaries.
- [ ] Generalize positive scalar scale steps, keep exact multiplication, accept bare literals/ratios, and reject overlapping alternatives before bounds.
- [ ] Check compatible inclusive intervals, dimensions and comparable constants; reject wrong types/ranges/injection/unknown functions without guessing var() output types.
- [ ] Preserve valid browser variables and deferred comparisons in structured traces, including external property/asset/name ownership after substitution.
- [ ] Test actual accepted complex Rose shadow/color/anchor-ready values plus images, tracks, strings, ratios, math and numeric unit conversions.

## Validation

- Language facade positive/negative behavior suite for every type and bound outcome, nested math dimension errors, escapes and value-position injection.
- Processor tests apply representative newly accepted values through generated accessors and ownership checks.

## Evidence

- `pending`
