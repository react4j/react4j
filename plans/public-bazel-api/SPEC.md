# Public Bazel API

## Source

- User accepts repository-only default visibility, explicit supported entry points/support tools and separately restricted Starlark loads.
- User directs public J2CL labels `//:core`, `//:dom`, React4j plugin `//:processor`, CSS annotations `//:css`, CSS plugin `//:css_processor_plugin`, removing old public names. Existing authority permits commit/push PR112, never merge.
- Repository evidence: root BUILD exposes separate JVM/J2CL names; query finds 300 public rules. Broad package defaults and unrestricted internal Starlark files expose examples/tests/tools. External consumer uses checked-library/application macros. Checked library attaches asset catalogues; application macro owns complete-tree assembly and manifest.

## Problem and outcome

Downstream applications need a small J2CL-first API with predictable names. Implementation targets and Starlark files should stay within the repository unless a demonstrated consumer requires access.

## Scope and constraints

Rename and migrate labels, narrow all owned BUILD/file/load visibility, curate necessary support exceptions, update permanent docs and external consumer checks. Preserve compilation checks, source archives, processor isolation, asset/pruning/maps/manifest behavior, tests and build caching. No aliases for retired names; no Maven coordinate, Java API or dependency-version changes. No secrets changes. Preserve unrelated changelog entries; no extra CSS feature row.

Ordinary `j2cl_library` consumers configure their own Error Prone/NullAway checks and include `web_assets` in `deps`. The checked-Java helper becomes repository-only. Existing application/browser-test macros remain optional public conveniences pending the user's preference; removing them would also change complete application assembly and manifest ownership. No new public rule or option is introduced. Core functionality remains usable through lower-level CSS rules.

## Requirements

- R1: The five requested root labels provide J2CL libraries or processor plugins, with no old public label aliases.
- R2: Ordinary J2CL consumers use their own compiler-check configuration and asset dependencies. CSS linking requests their existing J2CL javac outputs as validations, so JavaScript-only builds cannot skip configured checks. JVM-only publication/test targets remain internal; no combined-provider public adapter is needed.
- R3: Owned target/file visibility defaults to this repository. Only supported consumer targets and proven macro support dependencies remain public; examples, tests, implementation libraries and generated companion targets remain internal.
- R4: Public Starlark entry points remain loadable downstream; internal files allow only their actual repository clients. Target visibility and load visibility are independently enforced.
- R5: A separate downstream workspace exercises actual compilation, plugins, CSS and assets in development and optimized modes. Negative checks prove representative implementation targets and internal loads are inaccessible and retired labels are absent. Existing full verification remains green.
- R6: Permanent docs describe names, public boundaries, helper responsibilities and the downstream ownership of compression/cache policy accurately.

## Acceptance criteria

- AC1 (R1/R2): Root-label consumers compile J2CL and run both processors; internal JVM/publication tests remain green. Native J2CL consumers carry transitive asset catalogues and reject configured checker failures even when only JavaScript is linked. Retired root `core-j2cl`, `dom-j2cl` and `react4j_processor_plugin` labels have no callers or definitions; CSS package targets may remain as repository-only backing targets. No public generated Wasm/Kotlin companions appear.
- AC2 (R3/R4): Expanded target visibility audit, exported-file checks and Starlark load audit agree with the curated public set. External negative probes fail specifically for visibility or absent labels, never incidental errors.
- AC3 (R5): External consumer passes both modes using the new labels, native J2CL compilation with caller-owned check selection and the retained optional application macro. Full `tools/check.sh`, coverage floors, source-map checks and reproducibility pass.
- AC4 (R6): Documentation links and CLI examples resolve; simplify and independent review pass before publication and plan cleanup.

## Significant decisions

| ID | Decision | Rationale | Impact | User verification |
| --- | --- | --- | --- | --- |
| D1 | Public libraries are J2CL aliases; JVM publication/test imports remain internal | Experiments prove J2CL runs configurable Error Prone and NullAway itself | No separate public JVM labels or provider-combining adapter | Native J2CL external consumer and internal JVM suites |
| D2 | Private backing J2CL targets with explicit public wrappers | J2CL macros otherwise expose generated Wasm/Kotlin companions alongside public targets | Five deliberate root entry points | Inspect public-target audit |
| D3 | Hide the checked-Java helper; retain ordinary J2CL asset/validation integration | User challenged fixed checker policy; probes confirm apps can own checks, with explicit validation needed for JS-only builds | Asset catalogues work as ordinary dependencies; no mandatory duplicate Java compilation | Real asset/liveness behavior and negative checker probes |
| D4 | Keep application/browser-test macros optional in this pass | User has asked whether they are needed, without directing removal; application macro also supplies the complete manifest | Explicit supporting tools stay public; lower-level CSS primitives remain available | Pending optional preference may narrow these conveniences |

## Technical decisions

Native J2CL already runs java_common.compile with caller plugins/javacopts. Planning probes rejected ArrayEquals and NullAway failures when the library itself was built, but both bad programs linked successfully when only JS was requested. Forwarding J2CL's own runtime jar as an OutputGroupInfo validation made the linked build reject the configured checker failure without a separate Java compilation. A metadata-only Closure provider is accepted as a native J2CL dependency; use the official provider-construction helper for `web_assets` and collect it through existing dependency aspects. Remove the superseded asset attachment from the repository helper.

Private J2CL backing targets plus public aliases prevent incidental public Wasm/Kotlin companions. Internal JVM imports get distinct names and preserve publication/test semantics. Keep narrower existing friend visibility. Internal Starlark files restrict actual loader packages, supported files remain public. Generator-owned dependency visibility changes must come from its configuration when required. Remove all planning probes before validation.

## Testing decisions

Extend the existing external consumer instead of adding a new verification framework. Exercise native J2CL @View/@Css/@Assets processing, caller-selected Error Prone/NullAway checks and rejection of those failures from JS-only links. Cover new root API, real @View/@Css/@Assets output and manifest access. Retain only meaningful boundary checks in existing verification tooling. Full gate covers local callers, browser variants, coverage, publication archives, site and reproducibility.

## Open questions

None required for this bounded pass. The asynchronous optional preference about application/browser-test conveniences remains pending; preserving those wrappers is the working assumption. The user's checker question and verified evidence supersede the earlier recommendation to retain the checked-library helper publicly.
