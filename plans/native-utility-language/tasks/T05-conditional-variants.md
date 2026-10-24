# T05 — Apply container and starting-style variants

- Status: `complete`
- Blocked by: `T03`, `T04`
- Spec coverage: `R5`, `R8`, `AC5`

## Delivers

Checked utilities respond to size, custom-property style and scroll-state containers, and supply starting styles, with correct independent name/liveness/origin behavior.

## Acceptance criteria

- [x] Admit static checked wrappers/boolean conditions and typed dimension/ratio threshold holes, including named tokens and bracket values; reject forbidden name/operator/property/whole-condition holes.
- [x] Bind owned names from all templates and explicitly external containers/properties first used downstream. A query does not instantiate/retain a setup class.
- [x] Cover stuck, snapped, scrollable and scrolled grammar; preserve each nested query, mixed wrapper order and selector specificity.
- [x] Emit conditionless starting-style with explicit transition utilities and normal cascade, with useful malformed-condition/slot diagnostics.
- [x] Map and compress names inside nested conditions, including a valid named query whose setup getter is absent.

## Validation

- Language/processor/internal/linker/bundler tests for conditional grammar, order, ownership, source locations and absent setup pruning.
- Browser tests using actual generated classes for container size/style changes, a scroll-state scenario and an entering opacity transition, development and optimized.

## Evidence

- `bazel test //css/utility-language:tests //css/internal/src/test/java/react4j/css/internal:tests --test_output=errors`: passed (274 facade cases; shared parser regressions include ascending/mixed-group less-than conditions).
- `bazel test //css/processor/src/test/java/react4j/css/processor:utility_tests //css/integration-tests/src/test/java/react4j/css/integration:integration_tests --test_output=errors`: passed. Real separate/header/J2CL linking verifies nested conditions, compressed container/property identities, starting-style retention and unrequested setup absence.
- Linker and bundler JVM suites passed. Java formatting, Buildifier and `git diff --check` passed.
- `bazel test [ -c opt ] //css/examples/save-button/src/test/js/react4j/css/example:browser_tests --test_output=errors`: development and optimized passed, including live size/style changes, scrollable-state layout updates and a paused entering transition at its midpoint.
- Test infrastructure correction: verified prior pinned Chromium was 123 (scroll-state predates support); pin Chrome for Testing/driver 145.0.7632.117 for all four supported platforms with SHA-256 verified downloaded archives. macOS ARM browser execution passed; other platforms' archive hashes/paths checked, execution remains CI-owned.
- Parser repair: retain `<` tokens in conditional headers and propagate callback failures as runtime exceptions rather than ClassCastException. Ordinary malformed declaration regression still fails.
