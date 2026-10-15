# React4j Bazel migration specification

## Source

- Authority: completed grill-me interview in this conversation, explicitly confirmed by the user on 2026-10-15.
- Evidence: root AGENTS.md, docs/agents/*.md, buildfile, build.yaml, tasks/{gwt,js_asset,react_dom,site,release,api_diff}.rake; existing processor/core/DOM tests and fixtures.
- References: /Users/peter/Code/arez/arez (primary), /Users/peter/Code/sting/sting, /Users/peter/Code/realityforge/proton. Their current Bazel rules, publication archives, nullness semantics, golden testing, Java site, and check/release commands are reference implementations, not permission to edit those repositories.

## Problem

Building, testing, generating, publishing and documenting React4j currently requires Buildr/Ruby and Node/Yarn/Docusaurus. Obsolete GWT and downstream builds complicate validation. The annotation processor uses legacy nullability contracts and does not receive the strict checking now used by Arez.

## Required outcome

A contributor can use pinned Bazel toolchains to build, test and package React4j, run J2CL examples in development and optimized production, generate/check the documentation site, and prepare release artifacts without Ruby or Node. Public Bazel consumers exercise the exact assembled publication archives. Strict static checks and golden tests cover maintained and generated Java.

## Scope and constraints

- In scope: all build/test/generation/site/CI/release commands; JSpecify migration; redundant null-check removal; golden testing; distribution archive layout; consolidated contributor guidance.
- Remove GWT consumer/build support, real downstream repository builds/statistics, API-diff tests/history/viewer/report assets, Docusaurus, Buildr, Ruby, Yarn and Node project infrastructure.
- Preserve public Java/generated API contracts, artifact coordinates and relevant existing behavior except agreed GWT/nullness/archive-layout cuts and invalid input-contract diagnostics.
- Keep React and ReactDOM 16.6.0. Avoid unrelated dependency/product upgrades. Port required runtime fixes only when evidence from JVM/J2CL verification requires them.
- Do not modify/decode etc/secrets. No changes to dependency repositories. No publish, push, tag, deploy or cross-repository updates during execution. Local commits are required. No branch publication/PR is authorized; closeout reports local review readiness and this boundary.
- Implementation is sequential. Temporary intermediate migration states are permitted only with explicit task-local validation and an assigned follow-up; final state has no compatibility shims for removed tooling or annotations.

## Requirements

- R1: Pin Bazel 9.3.0 and reference Java toolchains; maintain Java 17 library/generated output and Java 21 processor execution. Build dependencies are immutable/checksummed and use the Arez dependency maintenance model.
- R2: Generate DOM factories and acquire/patch React assets through Bazel without Ruby/Node; preserve DOM APIs and React asset behavior. Prefer Java for substantive tools and Bash for orchestration; Python needs a concrete benefit.
- R3: Compile maintained core, DOM, processor and tooling Java using the complete Arez Error Prone policy, JSpecify-mode NullAway and fatal javac warnings. Adopt reference formatting in an isolated mechanical change where feasible.
- R4: Use JSpecify exclusively for owned nullness contracts. Honor declaration scopes, source/compiled inherited generic substitution, nested/enclosing types, wildcards, arrays/elements and conditional type-variable nullness. Ignore legacy javax/JetBrains annotations; error when generation needs specified nullness but it is unknown. Retain necessary javax.annotation.processing/JDK APIs.
- R5: Preserve nullable AUTODETECT input optionality and required nullable builder calls that allow null. Reject optional non-null reference inputs without a valid non-null default or tree-context source; reject nullable defaults for non-null inputs. Preserve handler/input contract matching and other input rules under effective JSpecify nullness. Preserve parametric contracts where possible and diagnose omissions/initialization that need stronger evidence.
- R6: Remove redundant Objects.requireNonNull checks on statically non-null arguments/internal state while preserving dynamic React props/context validation, meaningful invariants and checks/conversions that establish nullness from nullable data.
- R7: Preserve relevant existing runtime, package-boundary and processor coverage. Golden families compare complete generated file sets and exact formatted/unformatted output; diagnostics and classpath/subprocess regressions remain tested. Generated bodies compile under the full product checking policy. Golden updates are deliberate, bounded and rerun the affected family.
- R8: Publish reproducible binary/source/Javadoc/POM archives for core/DOM/processor, preserving shading and service registration. Binaries contain bytecode; sources contain complete authored/generated Java and required native JavaScript/assets. Public JVM imports/plugins use assembled binary jars and public J2CL libraries consume assembled source archives. Verify an isolated synthetic consumer without real downstream builds.
- R9: Replace all GWT compiles/examples with J2CL and deterministic pinned-browser validation in development and optimized production. Preserve runtime configuration switches. Retain the package-private GwtIncompatible marker only for J2CL's documented JVM-only declaration stripping; it introduces no GWT dependency. Remove GWT XML/compiler/bootstraps/super-source infrastructure and obsolete consumer documentation.
- R10: Replace Docusaurus with a Java/CommonMark site built by Bazel, with serve/build/link-check commands, aggregate Javadocs and compiled runnable examples. Preserve documentation, established URLs where practical, favicons/assets and externally owned example directories during deployment. Remove API-diff and downstream-statistics site content and references.
- R11: Provide explicit local release preparation/validation/signing/upload/tag/GitHub-release/site-deployment commands, removing downstream updates. Their validation must not execute remote mutations. Pin migrated dependency revisions for builds; honestly record unreleased dependency Maven publication prerequisites instead of claiming current released versions include migrated contents.
- R12: Provide a shared CI/local verification entrypoint covering formatting/buildifier, Ahab, JVM tests/coverage, strict goldens, archive/consumer checks, both J2CL modes, site checks and unsigned release validation. Ahab has reviewed reproducibility declarations and narrowly justified sandbox exceptions. Coverage verifies meaningful nonempty owned-module reports rather than inventing percentage thresholds.
- R13: Consolidate contributor instructions; update agent guides, user setup and changelog to the final contracts. Remove obsolete infrastructure/dependencies/docs/assets, retaining historical changelog statements. Complete reviewed, committed, auditable delivery and remove only this temporary plan tree at closeout.

## Acceptance criteria

- AC1 (R1,R2): From a clean checkout, Bazel resolves the pinned graph and builds generators/assets and owned modules without Ruby/Node or undeclared generated files; generated factory APIs and React patches are verified.
- AC2 (R3,R4,R6): Full reference compiler policy and formatting pass on owned code; no owned legacy nullability remains; focused tests demonstrate preserved dynamic validation and removed redundant checks.
- AC3 (R4,R5,R7): All golden/diagnostic families pass including source/compiled inheritance, generic/nested nullness, optional/default input rules, no-Sting classpath and formatter subprocess behavior; strict checker has a negative control proving it detects invalid generated nullness.
- AC4 (R8,R11): Archive inspection and isolated JVM/processor/J2CL consumer checks pass; reproducible archive checks and unsigned release validation pass; POMs document truthful dependency version inputs/publication prerequisites.
- AC5 (R9): All retained examples compile as J2CL; browser behavior tests pass in development and optimized production against packaged libraries; no GWT build/support resources remain except the J2CL pruning marker.
- AC6 (R10): Java site build/link checks pass with compiled examples and aggregate Javadocs; established route mapping is verified and deployment preparation preserves externally owned paths without pushing.
- AC7 (R11,R12): Shared check gate passes locally; CI invokes it; maintenance/release command help and safe dry-run/prepare behavior are verified without remote mutations. Coverage and Ahab reports pass their declared policies.
- AC8 (R13): Repository searches and final diff confirm obsolete tooling, downstream tests/statistics and API-diff infrastructure are gone; current contributor/setup/agent docs and changelog reflect the migration; all tasks and implementation review pass and plan deletion is committed.

## Significant decisions

| ID | Decision | Rationale | Impact | User verification |
| --- | --- | --- | --- | --- |
| D1 | Full sequential migration; Arez default, Sting where better suited | User confirmed full execution and primary reference | Ordered local commits and green acceptance boundaries | Review task gates and final evidence |
| D2 | Java 17 output, Java 21 processor minimum, reference tool JDK | Match upgraded Proton/checkers | Processor users need Java 21 | Review setup docs and output/runtime checks |
| D3 | JSpecify hard cut with definite generation contracts | Avoid unsound legacy inference | Old annotations ignored; ambiguous/invalid inputs can become errors | Review diagnostic goldens and migration docs |
| D4 | Preserve presence versus nullable-value semantics; reject unsound omission/defaults | Required builder call and allowed null are separate | New errors for invalid optional non-null/default combinations | Review focused processor fixtures |
| D5 | Remove GWT, downstream builds/statistics and API diffs entirely | User explicitly chose cuts | No GWT validation/distribution/support; obsolete reports disappear | Review removals and J2CL replacement |
| D6 | Publication archive boundary and split binary/source jars | Match Maven and Bazel consumption; expose packaging defects | J2CL uses source artifacts; binaries no longer bundle Java | Review archive inspections and consumer tests |
| D7 | Pin unreleased migrated dependencies; do not release them | Full migration can be tested before dependency publication | Maven-only distribution needs appropriate future dependency releases | Review release prerequisites/POM inputs |
| D8 | Keep React 16.6.0 and documented site behavior | Limit unrelated change | Existing React patch remains; Java site replaces rendering stack | Review browser and route checks |
| D9 | Keep meaningful dynamic checks; remove statically redundant checks | Static Java nullness does not validate JavaScript props/context | Runtime boundary failures remain detectable | Review runtime/processor tests |
| D10 | Java tools with thin Bash orchestration; Python only with concrete benefit | User preference | Some reference scripts may be reimplemented | Review tooling simplicity and command checks |
| D11 | Local validation/commits only; no remote mutations | Explicit scope confirmed in interview | Shipping releases and publishing branch/PR are separate actions | Review local commit history and remaining publication prerequisites |

## Technical and testing decisions

- Use explicit source lists and package-level BUILD files like the reference projects. Keep buildifier under tools/buildifier and Ahab under tools/ahab.
- Capture the existing expected-output/diagnostic families before semantic edits; migrate expected content by executing generators, not hand-editing output fixtures.
- Full checker enforcement applies to generated bodies as well as maintained product sources; diagnostic-only invalid inputs use plain javac where required.
- Keep no-Sting and formatter export regressions while changing harnesses. Assert complete generated output sets, not only named expected files.
- Package checks include service registration, shaded dependency isolation, source/native-JS completeness, Java class versions, POM dependencies and repeatability.
- Browser tests exercise meaningful React rendering/update/context behavior and configuration differences in both compilation modes. Site example assets come from those builds.
- Full gate: tools/check.sh (final implementation), including relevant Bazel build/test/coverage, tools/site.sh check, tools/release.sh validate and tools/ahab checks. Exact labels are recorded with task evidence once implemented.

## Open questions

None. New evidence materially contradicting this agreement requires reviewed replanning before implementation continues.
