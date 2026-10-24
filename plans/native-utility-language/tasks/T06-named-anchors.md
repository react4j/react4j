# T06 — Deliver checked named anchors and scoped popovers

- Status: `complete`
- Blocked by: `T05`
- Spec coverage: `R4` anchor contexts, `R9`, `AC6`

## Delivers

The language expresses reusable scoped popover relationships with checked named anchors while default-anchor inputs remain usable.

## Acceptance criteria

- [x] Add a distinct shared anchor symbol kind and full-spelling externalAnchors declaration, with static ownership collected across all provider templates.
- [x] Bind/rewrite anchor-name, position-anchor, named anchor-scope, anchor()/anchor-size() and nested fallback/math occurrences; retain keyword/default semantics.
- [x] Reject dynamic/unknown names and static carriers/hole positions that conceal names; check supported functions in valid declaration contexts, including literal templates.
- [x] Preserve identities, raw-name reservations, compression, stability/maps and reports. Name references do not force setup class liveness or imply DOM existence.
- [x] Implement a real example with scope/trigger/panel classes, built-in flips, size limits and scrolling; repeated rendered instances use their own trigger in development and production.
- [x] Keep component IDs/open/close/focus responsibilities explicit; leave Rose and custom named position-try resources untouched.

## Validation

- Shared syntax/token/name/renamer/resource plus processor/linker/bundler tests for owned/external/default/dynamic cases and all anchor occurrence forms.
- Browser geometry tests for repeated scopes and edge/oversized-content examples through linked accessors. Record limits; do not claim all Rose placement bugs fixed.

## Evidence

- `bazel test //css/utility-language:tests //css/internal/src/test/java/react4j/css/internal:tests //css/processor/src/test/java/react4j/css/processor:utility_tests //css/integration-tests/src/test/java/react4j/css/integration:integration_tests //css/linker/src/test/java/react4j/css/linker:tests //css/bundler/src/test/java/react4j/css/bundler:tests --test_output=errors`: all six targets passed (301 facade cases). Tests cover separate namespaces, escaped spans, raw reservations/collisions, every bound occurrence, absent setup, native dynamic-name/context errors, finite CSS preservation and linked source origins.
- Packaged/shaded processor anchor test passed via `processor_tests -methods NativeUtilityProcessorTest.anchorsOwnTheirNamespaceAndExternalHooksCanFirstAppearDownstream`.
- `bazel test [ -c opt ] //css/examples/save-button/src/test/js/react4j/css/example:browser_tests --test_output=errors`: development and optimized passed. Actual top-layer popovers inside repeated CSS scopes align with their own triggers and default anchor width; a bottom-edge case flips; explicit maximum dimensions and overflow handle the oversized fixture despite a clipped application ancestor. IDs, opening/closing and focus restoration are explicit test/application actions.
- Browser evidence is Chrome for Testing 145 on macOS ARM. This verifies the fixtures, not all browser/layout combinations or Rose's existing popover failures. No Rose source changed.
- Java formatting, Buildifier and whitespace checks passed. Anchor declarations and context checks remain native-language-only; existing finite anchor CSS retains its previous raw binding behavior.
