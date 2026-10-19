# Bazel Public Facade

## Source

- User accepted the recommendation to expose nine Starlark symbols through root defs.bzl, convert three orchestration macros to symbolic macros, and restrict eight supporting targets. Their correction explicitly requires aliases, with no implementation relocation.
- Execution authority: the latest “accept” continues the branch/PR delivery work and accepted implementation recommendation. Prior authority includes local commits and updating PR112; do not merge. No human plan checkpoint requested.
- Evidence: css/defs.bzl, tools/browser/rules.bzl, public targets in their BUILD packages, MODULE.bazel patches, tools/check_bazel_consumer.sh, and example CSS bundles rooted in application_bundle/browser_tests_bin. A disposable two-repository experiment verified symbolic visibility in both modes and the upstream gen_html_<name> naming violation.

## Problem and outcome

Downstream consumers currently load implementation files and can directly depend on eight tools/support targets. Provide one stable load entry point while preserving the existing build pipelines and hiding their implementation dependencies.

## Scope and constraints

- Root defs.bzl only re-exports the same nine rule/macro/provider objects; implementations stay in their existing files and providers retain identity.
- Convert css_js_binary, browser_application and browser_test to typed symbolic macros. Keep actual compilation, packaging and WebDriver work in existing rules; preserve their configuration, outputs, source maps, validation and runtime behavior.
- Restrict the eight support targets and the two implementation .bzl modules. Keep the five existing public library/processor targets unchanged. No old-path compatibility shim; this is unreleased work.
- Preserve independently rooted CSS variants: callers currently reference <application>_bundle and <test>_bin. Export these link targets deliberately to each macro caller; hide other generated helpers and preserve requested main-target visibility.
- Apply the smallest pinned Closure test-helper naming patch needed for symbolic composition and export that patch through the existing downstream configuration tool. No upstream publication requested.
- Follow explicit source lists, Yoda equality, narrow package visibility and existing style. Leave etc/secrets untouched. No unrelated compiler/runtime/deployment changes.

## Requirements and acceptance

- R1 / AC1: Downstream BUILD and .bzl files can load all nine symbols solely from @react4j//:defs.bzl, with unchanged provider identities. Existing public libraries/plugins remain usable; direct loads of the old modules fail.
- R2 / AC2: Native downstream J2CL compilation, CSS linking/variants, asset packaging and Chromium tests work in fastbuild and opt, including caller-private ordinary dependency/file inputs and custom compiler defines. Bazel requires select() conditions to grant visibility to the symbolic implementation packages; document this constraint and verify narrowly granted conditions rather than relying on legacy public defaults. Existing generated outputs and independently rooted CSS continue to work.
- R3 / AC3: Only five public library/processor targets and root defs.bzl remain in the public inventory (generated files inherit their owners). Direct downstream dependencies on each of the eight former support targets fail. Symbolic generated helpers are private while documented main/link targets respect requested visibility.
- R4 / AC4: Existing checker enforcement, full tests, runtime coverage, site and publication/reproducibility checks pass. Public setup/CSS documentation uses the new load path; simplify and independent implementation review pass; plan removed at closeout and PR112 updated.

## Significant decisions

| Decision | Rationale | Impact and verification |
| --- | --- | --- |
| Alias facade, no relocation | User correction; one public API without changing ownership | Check direct aliases and consumer provider checks |
| Symbolic composition | Existing Closure/WebDriver rules own the actual work; definition-site privileges hide tools | Real external consumer in both modes; negative access tests |
| Export existing link children | CSS variants are separately declared by callers | Exercise application_bundle and test_bin from consumer and examples; keep other helpers hidden |
| Narrow select condition grants | A real pinned-Bazel probe showed condition keys do not receive ordinary label-argument visibility delegation | Custom conditions grant only the symbolic definition packages, without becoming public; verify both modes and document the affected packages |
| Minimal Closure naming patch | gen_html_<name> violates symbolic target names | Verify real browser tests and independent downstream patch export |

## Knowledge ownership

Public load names and usage belong in existing project setup/CSS guides and the executable consumer inventory. Symbolic composition and tool visibility are implementation/configuration. No new specification qualifies without an existing durable owner; no surprising, costly-to-reverse architectural decision warrants an ADR. Existing CSS domain contracts remain unchanged.

## Open questions

None.
