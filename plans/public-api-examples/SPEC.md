# Downstream examples

## Source and authority

The user asks that `examples/` use only the supported public Bazel API and authorizes fixing every exception. The accepted API is five dependency targets (`core`, `dom`, `css`, `processor`, `css_processor`) and root `defs.bzl`. Existing authority permits committing and pushing PR112; merging remains excluded. No human plan checkpoint was requested.

Repository evidence: example Java libraries use private paired JVM/J2CL compilation; browser tests and third-party aliases are private; application builds load implementation Starlark directly. `tools/check_bazel_consumer.sh` already creates an isolated downstream workspace and enforces the public inventory.

## Outcome and scope

- R1: All example build definitions can run downstream using only public React4j entry points, external ecosystem targets and example-owned support.
- R2: Preserve application behavior, annotation processing, static checking, formatting and Java-authored browser tests. Remove unused companion JVM builds.
- R3: Continuously prove this boundary by building and running the actual copied examples outside the repository in development and optimized modes.
- Exclude new public APIs, runtime changes, compression/caching policy and unrelated documentation rewrites.

## Decisions

| Decision | Rationale and impact | User verification |
| --- | --- | --- |
| Native `j2cl_library` declarations; explicit compiler options and plugin selection from example-owned settings. Repository wrappers reuse those settings. | Examples demonstrate the downstream API while retaining one authoritative strict compiler policy; no companion JVM compilation. | Native rules and checker settings are visible in each Java BUILD. |
| Move shared browser test support into `examples/testing`, preserving its Java package and implementation; migrate all repository consumers. | The examples can be copied without private tools, without adding a public testing API or duplicate helper code. | Tests use example-owned support. |
| Use public `@react4j//` labels even in the repository, native JsInterop targets and example-owned JSpecify/Sting imports. Remove dependency aliases already exported by public core/DOM. | Real downstream references remain intact when copied. Native import targets avoid recompiling shared library JavaScript. | Example dependency lists expose their actual ownership. |
| Extend the existing external consumer check with the complete examples tree. | Real visibility checks catch private dependencies that repository-local builds permit, without maintaining a synthetic example inventory. | Four browser suites and all six applications run outside the module. |

## Acceptance and verification

- AC1 (R1, T01): Source audit finds no private React4j labels/loads in examples; public inventory unchanged.
- AC2 (R2, T01): All six applications and four browser suites pass development and optimized builds/tests; formatting and full compiler checks remain applied. Root formatter follows native targets and relocated helpers.
- AC3 (R3, T02): External consumer copies examples unchanged, builds every application variant and runs all four browser suites in both modes with default visibility checking enabled.
- AC4 (R1–R3, T02): `tools/check.sh`, simplify and independent implementation review pass. Permanent docs describe only the resulting conventions; temporary plan removed at closeout.

No unresolved questions.
