# T04 — Honest runtime coverage pipeline

Status: pending. Blocked by: T03. Coverage: R4; AC4.

Build the smallest JVM adapter around pinned Closure instrumentation and Chromium/ChromeDriver that records original-Java line and branch evidence. Demonstrate an end-to-end runtime target with untouched methods and partially exercised branches included in its denominator, no dead-code pruning, stable source-map attribution and failure controls. Create a source-aware runtime LCOV from disjoint browser obligations and parsed @GwtIncompatible JVM-only declarations, including mixed-platform files. Reject missing JVM-only method evidence and ambiguous attribution; prove both omitted and unexecuted JVM-only code fail the actual root gate even with complete browser coverage. Route explicit effective JVM/runtime report names per root in JSON, pass a report directory to the checker, and wire tools/check.sh; preserve no-data and per-class checks. Keep reports separate and reject missing/corrupt inputs. Verify native browser boundaries and API instrumentation limitations before expanding tests.

Evidence: pending.
