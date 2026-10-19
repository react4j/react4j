# Downstream asset policy

## Source

- Scope and execution authority: user instruction “remove caching policy and compression from react4j”, following the discussion moving compression and precache membership downstream.
- Prior authority: commit and push the existing PR112; do not merge.
- Evidence: `AssetContent`, `ApplicationPlan`, `ApplicationBundle`, `tools/web-assets/defs.bzl`, `tools/browser/rules.bzl`, the Brotli module dependency and browser/packaging tests.

## Problem

Application packaging performs compression and assigns offline policy that downstream applications should control. Unused library candidates can trigger expensive compression before linking determines retention.

## Required outcome

React4j delivers finalized uncompressed files and a complete build-only manifest. Downstream tooling can enumerate those files and apply deployment or offline policy independently.

## Scope

Remove compression actions, dependencies, metadata and preview negotiation, offline selection attributes, generated lists and their planner. Preserve asset validation, pruning, content-addressed URLs, explicit application files, source maps, per-file metadata caching and manifest outputs. Do not add downstream compression or service-worker tools. Existing development preview no-store behavior remains a server implementation detail.

## Constraints

Hard cut unshipped interfaces without compatibility machinery. Preserve Java-based browser tests, explicit colocated BUILD sources, narrow visibility, formatting, Yoda comparisons and existing coverage thresholds. Do not touch secrets or unrelated edits.

## Requirements

- R1: React4j emits uncompressed retained assets and explicit application files only, without compression actions or dependencies.
- R2: Packaging imposes no precache/on-demand membership, generated lists, reserved policy filenames or policy fields.
- R3: Default application output remains a directory, with a complete manifest output group containing logical paths, URLs, digests, sizes, MIME types, accessor/retention evidence and pruned candidates. Downstream rules can consume these outputs.
- R4: Existing pruning, deduplication, path/collision and byte-integrity checks, maps, development and optimized applications remain correct. Durable documentation describes the final contract.

## Acceptance criteria

- AC1 (R1): Focused tests/builds pass and dependency/action audits contain no Brotli implementation or compression actions in either build mode.
- AC2 (R2): No automatic offline files/fields/attributes remain; filenames formerly reserved are accepted as explicit ordinary files.
- AC3 (R3): Packaging tests match actual output files to manifest URLs and validate hashes/sizes; external consumer builds the directory and manifest output group.
- AC4 (R4): Full `tools/check.sh`, coverage thresholds and independent implementation review pass; permanent docs and PR description reflect the delivered behavior.

## Significant decisions

| ID | Decision | Rationale | Impact | User verification |
| --- | --- | --- | --- | --- |
| D1 | Remove compression entirely, including preview negotiation | Downstream owns delivery representations | No `.br` sidecars or encoding negotiation in React4j preview | Review plain output tree |
| D2 | Assemble directly from linked inventory and prepared explicit files | Offline planner has no remaining purpose | Smaller build graph; no implicit policy files | Review manifest and explicit file handling |
| D3 | Keep per-file digest metadata preparation | Content identities and build reuse still need it | Cached metadata actions remain inexpensive and independent | Check byte-change/pruning tests |

## Technical and testing decisions

The assembler validates the linked inventory and exact linked file set, then adds explicit application roots with collision checks. Identical managed URLs may share one file. The manifest remains build-only. Tests cover plain file delivery, malformed metadata, collisions, pruned candidates and exact inventory correspondence. The external consumer proves manifest accessibility. Full checks cover both link modes, coverage, browser behavior, packaging, releases and reproducibility.

## Open questions

None.

## Canonical owners

[Checked Web Assets](../../docs/specs/web-assets.md) owns plain output, manifest completeness and downstream policy ownership. [Finalize asset URLs after linking](../../docs/adr/0003-finalize-asset-urls-after-linking.md) owns the unchanged content-independent declaration boundary. [Asset inventory](../../docs/glossary/README.md#asset-inventory) defines the output account; the CSS README owns authoring and Bazel usage. No new ADR is warranted for removing unshipped deployment policy.
