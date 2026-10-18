# T03 — Separate compilation and full validation

- Status: complete
- Blocked by: T02
- Spec coverage: R7/AC7 and cross-boundary verification of AC1–AC6

## Delivers

Separate compiled interfaces, real JVM/J2CL/browser consumers, permanent documentation and full-scope evidence for the complete interface feature.

## Acceptance criteria

- [x] Separate contract libraries exercise inherited annotations and generic signatures through real header jars, for components and resource bindings.
- [x] Shared-interface browser consumers use two independent style implementations and checked symbols in development and optimized builds.
- [x] Focused tests cover deprecation, nullness, nested type shadowing, malformed contracts and deterministic declaration ordering.
- [x] README/changelog explain the accepted interface contracts; simplify pass completes.
- [x] tools/check.sh passes without weakening coverage thresholds; all diffs/goldens are inspected and evidence recorded before commit.

## Validation

Run focused integration/browser suites first, then tools/check.sh. Follow independent implementation review and closeout; remove exactly the active temporary plan after preserving review evidence in git history.

## Evidence

tools/check.sh passed after the final code change: Java/Bazel formatting, both hermeticity modes, all 37 development test targets, all six optimized browser targets, JVM/runtime coverage with unchanged thresholds, the isolated external Bazel consumer, site generation/link checks, unsigned release validation and all 13 reproducible publication/API artifacts. The focused JVM/J2CL integration target and development/optimized save-button browser target also passed.

The separately compiled StyleContracts library carries generic signatures and CLASS-retained ID/property/animation annotations through actual header jars. Components and resource bindings inherit those methods; JVM assertions compare bindings with parsed bundled CSS and source maps. The browser renders SaveButton and Status through StateStyle<String>, using inherited symbol access and default dispatch while checking independent classes and computed styles.

Focused hardening covers source/binary conflicts, malformed inherited signatures/names, shared ancestors, overloads, helpers, factory collisions, nullness/deprecation, inherited member types and Object implementations. A direct javac experiment exposed a keyframe diamond that could hide an explicit name behind an equivalent inferred name; the regression now rejects every effective explicit name in either interface order and accepts an abstract refinement to the valid unset mapping. Generated references survive inherited owner/annotation/String type names in named and unnamed packages, in both formatting modes.

Simplify completed: reuse collected methods for factory checks, remove redundant override selection and retain public Object implementations, and keep interface examples within existing targets. Permanent contracts are in css/README.md and CHANGELOG.md. The complete initiative diff and golden outputs were inspected. No domain-promotion directories exist.

Final coverage: CSS processor root 1282/1292 lines (99.2%) and 902/936 branches (96.4%); CssMethods 84/84 lines and 54/56 branches; CssGenerator 85/85 lines and 48/48 branches.

## Review round 1 fixes

Both P2 findings were reproduced and fixed. Internal names now avoid inherited members. When a type hides the conventional generated name, its package-access factory contains a separate package-access final implementation with a private constructor; the existing lazy holder owns allocation. This preserves the authored API even in unnamed packages without reserving legal interface member names. The interface golden includes this form, and real separately compiled JVM/J2CL contracts exercise it.

Effective method groups retain all Java signatures separately from annotation declarations, prefer a non-generic subsignature and validate covariant String returns plus the erased intersection of throws. Ambiguous or unsafe return contracts still fail. Sixty collision combinations, sixty peer-name/order combinations and ten invalid combinations compile actual authored/generated Java; positive signature tests also compile a String-returning caller without throws or casts. Header-jar integration combines a generic root with a throws-bearing non-generic root.

Goldens were regenerated and inspected. The simplify pass reviewed the collision-safe construction path and effective-signature checks. tools/check.sh passed again after both fixes: all 37 development targets, six optimized browser targets, full unchanged coverage policy, isolated consumer, site/release validation and 13 reproducible artifacts. The packaged processor smoke test passes in its normal uninstrumented build; instrumented implementation tests supply coverage. Round 2 review is pending.
