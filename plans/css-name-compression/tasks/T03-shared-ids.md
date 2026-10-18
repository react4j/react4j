# T03 — Checked shared IDs and liveness

- Status: `pending`
- Blocked by: T02
- Spec coverage: R3, R4; AC3.

## Delivers

All three CSS definition kinds can import canonical IDs with checked aliases, and optimized links distinguish identity resolution, DOM presence and provider activation.

## Acceptance criteria

- [ ] `id` and `property` import selections are exclusive; explicit empty/malformed names, duplicate ID aliases, unused imports, ownerless cycles and invalid owners fail.
- [ ] Source/header providers, transitive re-exports, independent alias namespaces and Java-only importing getters work.
- [ ] Global ID selectors require a local getter or selected import. Migrate every affected example/fixture, including SaveButton raw `#base`; preserve ordinary external URL validation boundaries.
- [ ] Imported IDs resolve in component/global selectors and same-document fragments, including SVG fragment references inside keyframes.
- [ ] Live importing getters activate consumer/provider chains and unconditional globals. Selector/fragment-only references do not activate providers or establish presence; optimized-away getters permit pruning.
- [ ] Live actual stable ID spelling can retain matching selectors across already active owners; equal spelling does not activate another owner.
- [ ] Getters returning equal readable ID text from distinct canonical providers retain separate provider chains through real symbolization and liveness. Getter identity cannot be recovered solely from its returned literal.

## Validation

- Processor positive/negative source/binary tests and goldens for each definition kind and reference form.
- Reachability/pruner tests for provider chains, selector-only uses, dead getters and stable same-name owners.
- Real SaveButton J2CL/Closure/bundle/browser run with an imported ID accessor; CSS/reference/DOM spelling agreement.

## Evidence

Pending.
