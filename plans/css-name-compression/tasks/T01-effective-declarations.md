# T01 — Effective accessor declarations

- Status: `complete`
- Blocked by: None
- Spec coverage: R1, R9; AC1, processor-timeout portion of AC7.

## Delivers

CSS interfaces and overrides use the same metadata-selection convention as main React4j while retaining independent Java signature checks. The assembled processor suite has an evidence-based runtime disposition suitable for the expanded tests.

## Acceptance criteria

- [x] Normal Proton-selected declarations own symbol annotations; untouched inheritance, override defaults, changed kind/name, sibling selection and helpers behave as accepted.
- [x] Source and separately compiled cases agree; concrete/default annotations fail, unannotated overrides/helpers and re-abstraction follow their effective declaration.
- [x] Generic substitution, covariant return/throws intersection, Object/default precedence, public generation, shadowing and singleton/facade regressions pass.
- [x] Sticky-metadata fixtures/goldens and permanent README text are updated together.
- [x] Inspect available CI test output and measure the target/class runtime before correcting size, partitioning or implementation. Record evidence; preserve assembled and implementation-path coverage.

## Validation

- CSS processor source/binary suites and generated goldens, including strict generated-code checks.
- Target/class timing and representative assembled-suite run; record environment limitations if Linux timing cannot be reproduced locally.
- Java formatting and affected BUILD syntax checks.

## Evidence

- Red/green: the new override test failed against sticky annotation merging, then passed with Proton-selected metadata. Source/binary interface suite: 24 methods passed, covering changed kinds/names, erased metadata, helpers, re-abstraction, sibling order and retained Java correctness.
- Full task validation: `bazel test //css/processor/src/test/java/react4j/css/processor:{processor_tests,implementation_tests,unit_tests} --test_output=errors`: 8 targets passed; assembled 505/505 cases (88.8s), implementation 503/503 (27.0s). Strict generated-code and JVM value checks include actual binary dependency classpaths, fixing an exposed harness gap.
- `tools/update_goldens.sh css` regenerated six authored-source map contents after an explicit accessor annotation; its generation run passed 505 cases. Its final verification initially overlapped a test edit; the subsequent eight-target run verified the final sources and goldens. Removed obsolete conflict-only fixture and BUILD references.
- Timeout disposition: prior CI hit 300s after 430 processor-load SLF4J initializations; successful local baseline completed 568 with 508 cases in 84.3s. This is a heavyweight assembled-processor compilation suite, now classified `large` like main processor goldens. No assertion failure was suppressed; Linux completion remains to be checked on delivery.
- Buildifier, Java format check, domain link validation and `git diff --check` passed. Review of implementation/test/golden diff completed.
