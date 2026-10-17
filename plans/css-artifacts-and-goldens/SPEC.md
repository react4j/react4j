# CSS artifacts and golden tests

## Source

- Accepted scope: user requests the Arez persist layout, JVM-only processing, isolated shaded dependencies, golden tests, and refinement of draft PR #112 rather than merging the spike.
- Execution authority: latest “proceed and do that” accepts the proposed immediate sequence: structure, standalone processor, golden harness. No human plan checkpoint or merge authority.
- Evidence: css/BUILD.bazel and rules.bzl, current CssProcessor/Compile/CssCompiler, tools/release/release.bzl and JarBuilder, tools/testing StrictGeneratedCode, existing React4j golden fixtures.

## Problem and outcome

The CSS prototype mixes its client annotation, compiler implementation, tests and integration fixtures, and its processor needs unshaded parser jars. Deliver a separately consumable JVM processor and durable regression coverage without changing its public CSS contract.

## Scope and constraints

Keep org.realityforge.css.Css and generated class/resource names and semantics stable. Use css/core, css/processor, css/examples/save-button and css/integration-tests; implementation lives in org.realityforge.css.processor. Use JVM tooling only. Keep #112 draft. No new CSS features, final Maven coordinates, publication, application migration, or merge. No compatibility aliases for unshipped target names. Existing parser restrictions remain explicit limits, not newly accepted production requirements.

## Requirements and acceptance criteria

- R1 / AC1: the requested subproject boundaries build and run existing behavior; Java/CSS generated before and after the move compare byte-for-byte, relevant unit/integration/browser checks pass, obsolete source roots and labels are gone.
- R2 / AC2: a single assembled processor jar supports javac service discovery with only the core and public JSpecify annotation jars on the application classpath; vendor packages are relocated, vendor classes cannot be imported by applications, no core annotation class must be loaded by the processor, and the real extractor/example consumes this artifact.
- R3 / AC3: checked-in fixtures compare complete generated Java and CSS resource sets and exact contents, cover valid naming/selectors/at-rules and invalid Java/CSS diagnostics including source locations, run generated sources through strict checks, and include a negative control proving the check can fail. Provide a repeatable intentional update workflow.
- R4 / AC4: docs, build/format/gate ownership follow the new layout; full tools/check.sh and fresh output/artifact reproducibility checks pass; draft PR is updated and pushed with evidence and remaining production roadmap. Remove temporary planning/scratch files at closeout.

## Significant decisions

| ID | Decision | Rationale | Impact | User verification |
| --- | --- | --- | --- | --- |
| D1 | Keep provisional css identity and public annotation API | Avoid mixing naming/product decisions into boundary work | Public source and generated names remain stable; implementation package and Bazel labels change | Review module layout and README |
| D2 | Reuse deterministic release jar builder with an explicit bundled runtime closure | Parser dependencies are private implementation details; reuse existing relocation machinery | Processor vendor namespace owns ph-css/commons and SLF4J; public JSpecify source references remain public; final Maven publication deferred | Review jar audit and isolated javac test |
| D3 | Fixtures test packaged processor and all generated text, with existing strict checker where practical | Raw implementation tests alone cannot prove distribution or generated code quality | Keep parser unit tests; add real artifact and golden tests without extra processor framework | Review fixtures and update command |

## Technical and testing decisions

Move files and target ownership first, validating preserved output. Resolve Css annotations by compiler model name instead of loading the annotation class. Assemble only required implementation runtime inputs, relocate vendor code/resources/services, preserve third-party legal notices. Public application dependencies remain separate from processor execution dependencies. Test discovered processor with an isolated processor path. Golden comparisons must reject missing and extra outputs. Reuse strict repository checking configuration, exercise a failing control, and keep fixture updates explicit. Validate with focused Bazel tests, Chromium dev/optimized checks, the full shared gate and independent clean-build byte comparison. Record durable conclusions in css/README.md and relevant existing agent guides. No open questions require user direction.
