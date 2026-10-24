# T04 — Accept broader typed values and numeric limits

- Status: `complete`
- Blocked by: `T01`
- Spec coverage: `R3`, `R4`, `AC4`

## Delivers

Library authors expose the full accepted type set and optional input bounds; users select compact scalar or complete bracketed values with reliable early errors and explicit browser uncertainty.

## Acceptance criteria

- [x] Support integer, number, percentage, length, length-percentage, angle, time, ratio, string, color, shadow, image, position, grid-tracks and an+b with the specified supported function boundaries.
- [x] Generalize positive scalar scale steps, keep exact multiplication, accept bare literals/ratios, and reject overlapping alternatives before bounds.
- [x] Check compatible inclusive intervals, dimensions and comparable constants; reject wrong types/ranges/injection/unknown functions without guessing var() output types.
- [x] Preserve valid browser variables and deferred comparisons in structured traces, including external property/asset/name ownership after substitution.
- [x] Test actual accepted complex Rose shadow/color/anchor-ready values plus images, tracks, strings, ratios, math and numeric unit conversions.

## Validation

- Language facade positive/negative behavior suite for every type and bound outcome, nested math dimension errors, escapes and value-position injection.
- Processor tests apply representative newly accepted values through generated accessors and ownership checks.

## Evidence

- Facade suite: 229 cases passed across all 15 types, exact scale multiplication, supported dimensional algebra, compound grammars, escaped functions/units, contextual string URLs/names, bad static values and injection paths.
- Range tests passed for inclusive endpoints, absolute length/time/angle unit conversions, incompatible/inverted/unbounded intervals, token/source collisions and structured `checked`/`browser-dependent` traces. Rational arithmetic avoids floating point; cross-radian comparisons use a bounded pi interval and defer unresolved precision cases.
- `utility_tests` and real `integration_tests` passed after value changes. A packaged processor test for broader types passed: generated getters and actual fragments carry Rose shadows, tracks, ratios, strings and durations; bad duration/shadow values report both errors.
- Binding uses temporary browser substitutions for open holes, plus all finite named-token instantiations. This avoids inventing unknown URL/animation names while still rejecting bad unused named tokens. Tests verify temporary substitutions never enter descriptors, emitted CSS or metadata.
- Typed nth holes are parsed before the CSS selector lexer, then canonicalized without changing candidate identity. Tests cover comments, CSS escapes, uppercase An+B and strings containing literal `param(...)`.
- Buildifier, root Java formatter and diff whitespace checks passed. Container feature holes and anchor name/context binding remain T05/T06.
