# T01: Downstream build definitions

- Status: complete
- Blocked by: None
- Coverage: R1, R2, AC1, AC2

Replace private example loads/dependencies with native J2CL and the public facade. Put compiler configuration, JSpecify/Sting imports and shared browser support under examples. Reuse the compiler options from repository wrappers, migrate helper consumers and formatter wiring, remove companion JVM targets, and update affected permanent conventions.

Validation: inspect every example BUILD; native targets build; all four example browser suites pass in fastbuild and opt; Java and Bazel format checks pass. No new public labels.

Evidence:
- Four example browser suites pass in fastbuild and opt (all six application links).
- Twelve application variants, root Java format check and strict checker options build successfully.
- Bazel formatter check, source audit and documentation-link validation pass. All original compiler option lists compare equal to their new owner.
- Browser Java/JS helpers moved byte-for-byte; public API unchanged (isolated inventory check follows in T02).

Review fix: the formatter aspect ignores native J2CL providers. Each of the 36 example libraries and three affected browser-test peers now shares one explicit source filegroup with its formatter target. `aquery` proves 105 scans cover all 163 example Java files and the peer sources. A temporary negative control makes application, browser-test and helper Java unformatted; the real root check rejects all three, then passes after exact restoration. No Java source changes or new public targets.
