# T01: Downstream build definitions

- Status: pending
- Blocked by: None
- Coverage: R1, R2, AC1, AC2

Replace private example loads/dependencies with native J2CL and the public facade. Put compiler configuration, JSpecify/Sting imports and shared browser support under examples. Reuse the compiler options from repository wrappers, migrate helper consumers and formatter wiring, remove companion JVM targets, and update affected permanent conventions.

Validation: inspect every example BUILD; native targets build; all four example browser suites pass in fastbuild and opt; Java and Bazel format checks pass. No new public labels.

Evidence: pending.
