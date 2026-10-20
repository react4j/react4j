# Task map

- Spec: [SPEC.md](../SPEC.md)
- Status: implementation-review
- Frontier: None (implementation complete)
- Planning reviewer: `/root/examples_public_planning` — Findings: none (1/3 rounds; final confirmation unused)
- Plan checkpoint: automatic — user implementation request, existing delivery authority, passing independent planning review
- Implementation reviewer: `/root/examples_public_review` (1/5 rounds; final confirmation unused). Round 1: P2 formatting scans omitted native J2CL sources. Fixed: explicit authored-source filegroups, including three affected native browser-test peers. 105 formatter scans include all 163 example Java sources. Negative control rejects unformatted application, browser-test and helper Java; restored check passes. Round 2 pending. Final full-gate rerun pending.

| Task | Status | Dependencies |
| --- | --- | --- |
| [T01: Downstream build definitions](T01-examples.md) | complete | None |
| [T02: Isolated verification and closeout](T02-verification.md) | complete | T01 |

Full-scope gate: `tools/check.sh` passed, including default/optimized tests, unchanged coverage gates, isolated copied-example validation, site checks, unsigned release validation and independent reproducibility. Focused copied consumer check also passed. All twelve application variants build; four copied suites pass both modes. Simplify and documentation-link validation passed.

Promoted knowledge: build/compiler settings and test-support layout belong to code and [CONTRIBUTING.md](../../../CONTRIBUTING.md). Existing public-consumer contract remains in [Project Setup](../../../docs/project_setup.md). No new runtime contract or hard-to-reverse architecture choice qualifies for a specification or ADR; no deferred questions introduced.
