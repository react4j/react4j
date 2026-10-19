# T03 — Checked shared IDs and liveness

- Status: `complete`
- Blocked by: T02
- Spec coverage: R3, R4; AC3.

## Delivers

All three CSS definition kinds can import canonical IDs with checked aliases, and optimized links distinguish identity resolution, DOM presence and provider activation.

## Acceptance criteria

- [x] `id` and `property` import selections are exclusive; explicit empty/malformed names, duplicate ID aliases, unused imports, ownerless cycles and invalid owners fail.
- [x] Source/header providers, transitive re-exports, independent alias namespaces and Java-only importing getters work.
- [x] Global ID selectors require a local getter or selected import. Migrate every affected example/fixture, including SaveButton raw `#base`; preserve ordinary external URL validation boundaries.
- [x] Imported IDs resolve in component/global selectors and same-document fragments, including SVG fragment references inside keyframes.
- [x] Live importing getters activate consumer/provider chains and unconditional globals. Selector/fragment-only references do not activate providers or establish presence; optimized-away getters permit pruning.
- [x] Live actual stable ID spelling can retain matching selectors across already active owners; equal spelling does not activate another owner.
- [x] Getters returning equal readable ID text from distinct canonical providers retain separate provider chains through real symbolization and liveness. Getter identity cannot be recovered solely from its returned literal.

## Validation

- Processor positive/negative source/binary tests and goldens for each definition kind and reference form.
- Reachability/pruner tests for provider chains, selector-only uses, dead getters and stable same-name owners.
- Real SaveButton J2CL/Closure/bundle/browser run with an imported ID accessor; CSS/reference/DOM spelling agreement.

## Realization notes

- Resolve IDs from CLASS-retained effective `@CssId` declarations and selected-import aliases, with parsed selector/fragment occurrences for local ownership. Imports may re-export an alias without a getter when CSS uses it. Preserve the canonical owner/name/kind/stability and the complete provider chain; detect cycles by owner plus alias, so a chain ending at a real local ID is valid.
- Pass resolved alias-to-name maps to the parsed ID rewriter. Keep its found-name set separate from imported getters: an importing getter can supply its value without a CSS occurrence, but an import with neither CSS usage nor a getter fails. IDs in global selector functions must also be checked; reject hash references in unsupported selector functions rather than leave an unchecked reference.
- Add per-binding required providers to metadata/link configuration (hard-cut versions). Only surviving getter bindings activate these ID chains. CSS-only selected IDs contribute catalogue dependencies for unlinked/development collection, but never value-based provider edges in optimized reachability. Existing global dependency ordering tolerates dependency cycles while enforcing acyclic unconditional inclusions.
- Keep ordinary external asset URLs and strings unchanged. Same-document fragments use the selected binding. Keyframe consumers accept selected IDs as well as properties, without expanding keyframe placement rules.
- Migrate SaveButton `#base` to a stable `Defaults.base()` getter used by the DOM. Themes and inactive globals import that ID to preserve the authored selector and existing cascade tests; keep ordinary properties scoped. Existing global-ID fixtures/tests must preserve their original failure conditions while adding checked bindings where appropriate.

## Evidence

- Added selected ID imports, canonical source/binary/transitive resolution and provider-chain metadata (resource v5, link v2). Java-only getters, empty global consumers, SVG fragments in keyframes, generic interfaces, independent namespaces, real local-owner cycles and invalid/unused imports are covered.
- Strict global hash checking migrates SaveButton defaults/themes to checked stable IDs. Strings and external asset URLs remain unchanged. All other example global definitions were inspected; they contain no unbound hashes.
- New graph and real optimized browser regressions prove equal-valued ID getters activate only their own provider chain. Already active same-name globals match; inactive same-name providers remain absent. The dead getter sorts first to expose literal-based identity bugs.
- `tools/update_goldens.sh css` passed: 526 assembled processor cases, 99.0s. Audited all 60 changed snapshots: only schema version and explicit binding-provider arrays changed.
- Final focused run passed all 12 targets: full implementation suite (524 cases), processor units, internal, linker, bundler, integration and development browser. Optimized SaveButton browser also passed with the full provider scenario. Formatter, domain links and diff whitespace checks passed.
