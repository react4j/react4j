# Browser build controls and Rose development delivery

## Authority

The user accepted independent React runtime and Closure optimization selection, independent map publication, and Rose-owned optional compression; on 2026-10-20 explicitly instructed “could you do that”. They require preserving iBazel iteration speed. Existing React4j PR112 and Rose draft PR6342 publication authority continues. Work in the existing React4j checkout/branch and isolated Rose checkout; no merges or deployment. The formatter chat owns its formatter edits; its committed 896e4a77c is the current React4j baseline. Rose baseline for this pass is d627958a3ff.

## Problem and outcome

Rose needs production ADVANCED, diagnostic ADVANCED with development React, and local BUNDLE, with its own defines and delivery policy. browser_application currently couples runtime/preset selection and optimization to -c opt, omits custom defines from BUNDLE runtime, always publishes maps, and makes runtime scripts inaccessible to custom packagers. Deliver independent supported application controls and switch Rose’s draft to consume complete public application outputs, retaining downstream policy and measurable development boundaries.

## Scope and constraints

- Preserve native J2CL, explicit package-owned source lists, Yoda conditions, private implementation targets, JVM-only toolchain, checked source maps and liveness.
- Do not expose four new React runtime targets; browser application output supplies matching runtimes.
- Compression and service-worker/cache policy stay in Rose. Do not broaden cache membership or executable update identity. Do not migrate all styles/icons.
- Preserve Rose client-delivery spec and ADR0040/0043: deployed diagnostics optimized, production maps withheld, local/browser/backend assembly separate.
- Shared dependency ownership is a separate verified blocker (392 duplicate modules plus one namespace). Do not suppress diagnostics, bypass target visibility or claim complete Rose migration/latency parity while that blocks real linking. Measure available baseline and post-link costs on real Rose payloads and public end-to-end fixtures; report the blocked full-application gate explicitly.
- Leave secrets and main Rose checkout untouched; do not read credential-bearing Jenkinsfile/tools/publish.sh/user.bazelrc into output.

## Requirements and acceptance

- R1 / AC1 (T01): Public browser_application supports independent compilation_level and runtime_mode, retaining convenient Bazel-mode defaults. Production/development runtime selects matching framework defaults; explicit Closure defines override them and produce identical typed BUNDLE runtime values. All three Rose combinations render and verify custom string/boolean/numeric values, duplicate override ordering and defaults.
- R2 / AC2 (T01): Map publication is independent of optimization/runtime. Withheld generated JS/CSS maps and their trailing references are absent from delivered tree/inventory; required build maps remain internal. Optional caller-owned HTML avoids a manifest/launch-page dependency cycle in Rose; HTML-free BUNDLE supplies a runtime configuration script for downstream launchers. Unsupported mode values fail clearly.
- R3 / AC3 (T02): Profile extra post-link work on real Rose BUNDLE outputs and implement only evidence-backed safe reductions. Asset bytes must not invalidate Java/J2CL/Closure actions. No-op and edit checks must distinguish action execution from cache hits. Preserve mapping/token validation and use meaningful regression tests.
- R4 / AC4 (T03): Rose consumes BrowserApplicationInfo trees/inventories, eliminating private React release archive references. Production/local adapters preserve matching relative asset/CSS URLs, diagnostic map names, JavaScript byte-derived identities and existing cache policy. Compression is optional in Rose’s production packaging; local iBazel graphs have no compression, ADVANCED, service-worker or WAR actions. Real public browser_application fixture outputs exercise downstream tools where app linking remains blocked.
- R5 / AC5 (T04): Document public controls and downstream usage in canonical user docs, classify accepted durable contracts, independently review and simplify, run proportional full gates, update both PRs with evidence/limits, and remove exact temporary plan/profiles/experiments. Do not report the blocked full Rose iteration comparison as passing.

## Decisions

| Decision | Rationale / impact | Verification |
| --- | --- | --- |
| Independent per-application runtime and compilation overrides; omitted values use existing Bazel-mode defaults | Supports all Rose combinations without new public script targets or changing ordinary examples | Runtime/rendering matrix and config analysis |
| Reuse Closure’s define parser for effective runtime configuration | Matches compiler string/boolean/numeric semantics and last override wins; no competing JavaScript grammar | Unit and browser equivalence tests |
| Optional HTML; configuration script for HTML-free BUNDLE | Rose generates passive launch pages after its combined manifest, so requiring those pages as application inputs would create a cycle | Native fixture plus Rose local/deployed adapter tests |
| One application assembly action owns root transformation/metadata | Configuration expansion and map filtering are assembly concerns; avoid adding per-root tool startups to development graph. Managed asset preparation remains per-file and independently cached | Baseline profiles and action graph comparison |
| Optional compression belongs to a downstream Rose build setting/layers | Building local uncompressed output must not even depend on compression actions | aquery of local and compressed/uncompressed production graphs |

## Validation

Start with unit/packaging tests, then real pinned-runtime browser tests for the mode/define/map matrix, source-map and asset regressions, explicit external public consumer checks and the full React4j gate. Use real output fixtures for Rose tool tests and configured action graphs; measure real Rose baseline/post-link overhead without deployment. Full Rose link/WAR/browser/iteration parity remains a pending migration gate if the known shared-library conflict persists. Record exact failures and timing methodology.

## Open decisions

No user decision required for this bounded pass. Canonical shared ecosystem dependency alignment remains outside this pass; latency parity is not assumed.
