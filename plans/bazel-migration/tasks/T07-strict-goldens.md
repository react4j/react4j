# T07 — Complete strict golden verification

- Status: `complete`
- Blocked by: `T06`
- Spec coverage: `R3`, `R4`, `R5`, `R6`, `R7`, `AC2`, `AC3`

## Delivers

Complete all golden family migrations and enforce exact emitted file sets and full checker compilation of generated bodies in both formatting modes.

## Acceptance criteria

- [x] Entire processor suite passes under Bazel, golden updater is bounded and reruns its family, diagnostic coverage is retained, strict checker negative control fails for the intended nullness reason.

## Validation

- bazel test all processor targets; run strict-checker negative control and golden update no-change checks; compare fixture inventory against T05 — proves the delivered behavior. Record actual labels, commands and outcomes below; do not mark complete with skipped required checks.

## Evidence

- `bazel test //processor/... --test_output=errors`: all nine processor targets pass. The original generated family has 234 passing cases, each with complete own output sets and full checker compilation in both formatting modes. The diagnostic family also checks generated bodies for successful warning/suppression fixtures.
- `bazel test //processor:all_tests //core:tests //dom:tests //:java_format_check --test_output=errors`: all nine selected test targets and formatting pass after the final changes. `bazel run //tools/buildifier:buildifier_check` and `git diff --check` pass.
- `tools/update_goldens.sh generated` and `tools/update_goldens.sh jspecify`: final repetitions pass and report `Updated 0 golden files`; the original no-Sting/formatter family also passes regeneration and a zero-change repetition. Every update reruns its own family against maintained files. Expected content was generated, never hand-edited.
- `checker_negative_control` inserts a null return into a valid marked generated class and requires the specific NullAway returning-null error. Both the control and unchanged positive smoke pass.
- Generated-code defects exposed by enforcement were fixed at their source: nullable native props/view narrowing, factory initialization, typed JS-map construction, unused private state/parameters, parametric constructor extraction, context class literals and nullable render children. Dynamic key checks first widen the value to Object so arrays/final types are legal Java. Unmarked injected values require fully specified stored reference types; framework parameters retain explicit non-nullness in unmarked constructors.
- Builder/getter signatures retain concrete collection APIs with a narrow NonApiType exception for ArrayList/HashSet. A composed fixture proves concrete collections, deprecated inputs/defaults and nullable context publication work together; deprecation belongs on generated methods, and suppression entries are combined once.
- GoldenUpdater now compares against an immutable snapshot: it copies only changed candidates, preflights every path, and refuses concurrent maintained edits before copying anything. Focused temporary-directory controls pass for bounded copy, unchanged-file preservation, concurrent-edit rejection and undeclared-path rejection, with no partial writes. Update compilations use a separate temporary directory, avoiding the collision observed with overlapping local Bazel runs. Bash syntax validation passes.
- Inventory audit: all 38 original TestNG declarations remain; all original invalid-input and expected paths remain; exactly four obsolete missing-annotation-warning/suppression input paths were removed and explicitly replaced by effective-scope/unknown-contract regressions. Current Java counts are 356 input, 163 invalid, and 490 expected per mode. See processor-coverage.md for ownership/dispositions; the baseline method capture is corrected to exclude helper calls.
