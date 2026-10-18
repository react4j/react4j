# T01 — Effective accessor declarations

- Status: `pending`
- Blocked by: None
- Spec coverage: R1, R9; AC1, processor-timeout portion of AC7.

## Delivers

CSS interfaces and overrides use the same metadata-selection convention as main React4j while retaining independent Java signature checks. The assembled processor suite has an evidence-based runtime disposition suitable for the expanded tests.

## Acceptance criteria

- [ ] Normal Proton-selected declarations own symbol annotations; untouched inheritance, override defaults, changed kind/name, sibling selection and helpers behave as accepted.
- [ ] Source and separately compiled cases agree; concrete/default annotations fail, unannotated overrides/helpers and re-abstraction follow their effective declaration.
- [ ] Generic substitution, covariant return/throws intersection, Object/default precedence, public generation, shadowing and singleton/facade regressions pass.
- [ ] Sticky-metadata fixtures/goldens and permanent README text are updated together.
- [ ] Inspect available CI test output and measure the target/class runtime before correcting size, partitioning or implementation. Record evidence; preserve assembled and implementation-path coverage.

## Validation

- CSS processor source/binary suites and generated goldens, including strict generated-code checks.
- Target/class timing and representative assembled-suite run; record environment limitations if Linux timing cannot be reproduced locally.
- Java formatting and affected BUILD syntax checks.

## Evidence

Pending.
