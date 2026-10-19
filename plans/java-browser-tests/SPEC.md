# Java browser assertions

## Source

- Authority: user requested a reasonable Java/JavaScript split for Drumloop, then explicitly extended it to all JavaScript tests added in PR 112. Continue implementation without a plan checkpoint; existing authority permits committing and updating the branch PR, without merging.
- Corrections: Java and Closure sources belong in package-matching directories with package-local explicit-source libraries; use Yoda equality, narrow Java/Bazel visibility, checked Java formatting, and no Node toolchain. Preserve every meaningful browser/coverage failure condition.
- Base: 424077f39; the separate visibility audit is committed and passed tools/check.sh. Deferred compression was researched with Bazel 9.3 directory-action probes; changing that production pipeline is outside this test migration.
- Evidence: seven added assertion/coverage drivers and four modified example browser suites contain about 1680 JavaScript lines. Existing ReactCacheTest demonstrates checked Java/J2CL assertions called by Closure. Property/event probes discover and invoke actual generated overlay methods; measurement fixtures exercise omitted, throwing and deferred Java execution.

## Problem

Browser assertions and application scenarios are authored in Closure JavaScript while the examples and APIs are Java. The current split obscures the Java-facing behavior and bypasses Java's checks.

## Required outcome

Java owns browser assertions, expected values and application behavior scenarios. JavaScript supplies the Closure runner, native browser/external-service instrumentation and interop operations that require JavaScript. Actual development/optimized applications and coverage evidence retain their existing checks.

## Scope

- In scope: core runtime driver; DOM runtime/property/event probes; CSS SaveButton browser suite and naming variants; Drumloop, Heart Rate Monitor, all TodoMVC variants and Web Speech Demo suites; verification runtime assertions; audit measurement and CSS-link JavaScript fixtures for the same boundary. Tests introduced inside modified files are included.
- Out of scope: production behavior changes, replacing the Closure runner, new third-party browser libraries, general DOM bindings for application users, changing coverage obligations/floors, and changing asset compression.

## Constraints

- No assertions or expected application values are hidden in native helper files. Native timeout/fixture-structure failures may remain at the browser boundary.
- Do not replace real APIs, React, resource loading, rendering or promises with mocks. Preserve controlled external Bluetooth/speech engines and real Web Audio observation; expected application calls belong in Java checks.
- Shared support is test-only and limited to operations exercised by these suites. Public Java members are required only for cross-package/native access; Bazel grants only actual consumer packages and formatter access. Existing generated implementations remain package-access.
- No compatibility wrappers for unshipped test APIs and no node dependency. Preserve source-map, Java line inventory and branch attribution; moving assertions must not inflate covered production obligations.

## Requirements

- R1: Every affected application/API assertion and expected value is authored in checked Java/J2CL, including setup/action sequences and async conditions. Existing case identities and failure conditions remain exercised.
- R2: Retained JavaScript contains runner/configuration wiring or necessary native reflection, browser instrumentation and interop fixtures. It does not implement the migrated assertions under a different filename.
- R3: Core/DOM native probes still exercise every currently discovered authored property/event overlay, including fluent identity, false updates, aliases, default prevention and propagation. Verification retains its skipped/throwing/deferred and mixed-platform attribution checks.
- R4: CSS and packaged examples still exercise development/ADVANCED behavior, independent naming variants, nested routes, real images/fonts/audio, resource pruning, UI interaction and external-service lifecycle behavior.
- R5: The resulting Java sources pass the full checker/formatter policy. Build libraries use explicit sources beside packages and restricted visibility. JavaScript entry modules retain their package-aligned names.
- R6: Durable authoring policy, implementation review and cleanup are completed. No migration or experiment scaffolding remains at closeout.

## Acceptance criteria

- AC1 (R1,R2,R5): Inspect the PR's JavaScript test changes and corresponding Java/native files; compiled Java owns each assertion/expectation and only justified runner/native operations remain. Every changed package builds with normal JVM checks and formatting.
- AC2 (R3): Run the real core/DOM browser coverage collector and its attribution regression suites in both configurations. Keep root/class denominators and existing coverage floors; execute all discovered overlay cases and preserve native object identity rather than comparing computed expectations to themselves.
- AC3 (R4): Run all affected example/CSS browser suites in development and optimized production, including prefixed/uncompressed CSS variants and all three TodoMVC apps. Retain scenario-specific observations and cleanup on both success and failure.
- AC4 (R5,R6): tools/check.sh, diff checks and canonical link validation pass. Simplify and independent implementation review pass; remove only this completed plan tree and publish the existing PR without merging.

## Significant decisions

| ID | Decision | Rationale | Impact | User verification |
| --- | --- | --- | --- | --- |
| D1 | Retain Closure's runner with thin calls to Java test entry points | Existing Java/J2CL and browser configuration already work in both modes | No new test framework or production dependency | Check that scenarios/assertions read as Java and JS entry points remain small |
| D2 | Add a small shared test-only native browser/async support module as needed by the migrated cases | Repeated iframe, polling, DOM and native-value handling otherwise duplicates interop machinery | Internal test API shared by concrete current callers | Check API size, typed boundaries and narrow visibility |
| D3 | Keep native overlay discovery/invocation and deliberate JS coverage fixtures where language behavior is the subject | Java reflection cannot substitute for the generated JavaScript/native boundary being tested | Some native JS remains, while Java owns expectations | Verify the original failure triggers and coverage obligations remain intact |

## Technical decisions

- Use existing java_j2cl_library checks and normal @JsMethod entry points. Keep assertions active in optimized mode by explicitly throwing assertion failures, independent of Java assert configuration.
- Use typed test-only native DOM/value and promise boundaries; avoid an application-facing browser abstraction or broad new dependency. Native helpers perform operations/observations; Java compares their results.
- Preserve actual packaged-app iframes and observer initialization timing. Chain native promises for async cases, including failure-safe teardown. Keep per-suite case identities and timeouts.
- The production code and coverage collector semantics stay unchanged. Reuse existing coverage regression fixtures and map/denominator checks.

## Testing decisions

- Each slice first runs its own browser/collector target and the optimized counterpart when applicable; shared support is demonstrated by a real migrated suite, not a synthetic placeholder test.
- Keep the original assertions as a migration checklist in repository history; audit case-by-case before deleting each JS assertion body. Add no replacement tests that merely assert source structure.
- The last slice owns the full repository gate and proof that native helper code contains no migrated expectations. Finish with simplify, domain classification/promotion, independent review and exact plan deletion.

## Open questions

None.
