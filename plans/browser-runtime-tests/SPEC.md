# Browser Runtime Tests

## Source

- Accepted scope and execution authority: the user said “Do that” after the recommendation to migrate remaining runtime tests to Java authored J2CL/Chromium tests, remove the JVM configuration branch and `GwtIncompatible`, remove separate compilations for browser-only tests, and simplify mixed-runtime coverage.
- Existing authority includes local commits and updating PR112; merging is not authorized. No human plan checkpoint was requested.
- Evidence: runtime tests under `core` and `dom`; configuration in `ReactConfig` and `react4j.js`; compiler policy in `third_party/java/rules.bzl`; browser helpers; runtime coverage collection in `tools/verification`.

## Problem and required outcome

Runtime tests should exercise the browser implementation used by React4j applications. JVM configuration fallbacks, mock context providers and mixed-platform coverage currently support a runtime that applications do not use. Migrate that verification without weakening compiler checks, configuration coverage or coverage accounting.

## Scope and constraints

- Migrate core/DOM runtime tests and the browser-only Java test libraries/support used by examples and CSS browser tests. Keep assertions in Java and Closure harnesses thin.
- Preserve browser configuration semantics and optimization through Closure defines, including default, explicit true/false and invalid values; preserve development/optimized execution.
- Keep public Java/Bazel APIs, Java 17 publication archives, Javadoc and JVM processor/build-tool tests. Retain bytecode dependency checks as JVM build verification.
- Keep publication class outputs as the runtime line denominator, but collect the inventory without executing runtime classes. All runtime execution evidence must come from Chromium.
- Remove obsolete mixed-platform coverage logic, fixtures and product testing hooks when their last caller disappears. Keep missing sources, modules, method inventory and counters as errors; never shrink the denominator to what happened to execute.
- Limit companion removal to browser test/support targets. Existing application/source-publication and deliberate JVM fixture compilation are outside that mechanical conversion.
- Follow explicit package source lists, package-aligned files, narrow visibility, Yoda comparisons and existing formatting. Do not touch `etc/secrets`.

## Requirements and acceptance

- R1 / AC1: All former core/DOM runtime assertions are covered by browser scenarios, including context defaults/qualifiers, invalid registrations/lookups, keyed/unkeyed values, class-name composition, and configuration defaults/overrides. Run development and optimized tests. JVM test suites contain only architecture checks for these modules.
- R2 / AC2: Runtime code has no `GwtIncompatible` or JVM-specific configuration implementation. Browser configuration retains compile-time folding and exact boolean string semantics. No reflection, classloader or mock context setup is needed for runtime tests.
- R3 / AC3: Browser-only test/support libraries compile once through J2CL under the repository's full Error Prone/NullAway policy. Existing compiler policy is shared rather than copied. Verify real checker failures and ordinary compilation; formatting continues to include these sources.
- R4 / AC4: Browser coverage uses an independently complete inventory derived from class archives and browser-only hit counters. Remove JVM-only branches/annotations and synthetic fixtures; preserve tests for unexecuted/throwing/deferred paths and missing/invalid evidence. Configured 95% line / 85% branch floors still pass without reductions.
- R5 / AC5: Contributor guidance and aggregate gates describe and run the resulting split. Full local gate, independent implementation review and simplify pass pass; remove the temporary plan at closeout and update PR112.

## Significant decisions

| Decision | Rationale | Impact and verification |
| --- | --- | --- |
| Keep Java assertions and use the pinned browser | Tests run the shipped implementation with existing tooling | Review tests for actual context rendering and active assertions in optimized builds |
| Test Closure defines in separate builds/pages | JVM system-property mutation does not prove browser constant configuration | Verify default profiles and explicit valid/invalid overrides in both modes |
| Derive inventory from publication classes with no execution | Complete executable line inventory remains independent of browser maps and hit counts | Verify missing methods/modules fail and uncovered browser paths stay uncovered |
| Keep publication and JVM tool checks | Runtime migration does not authorize changing distribution compatibility | Verify release artifacts and dependency checks in the full gate |

## Knowledge ownership

No new domain contract or architecture decision is introduced: compiler settings and coverage thresholds remain code/configuration; contributor operation belongs in CONTRIBUTING.md; delivery evidence belongs in this temporary tree. Existing CSS specifications and ADRs remain unchanged.

## Open questions

None.
