# T03 — Verify distribution boundaries and close delivery

- Status: `pending`
- Blocked by: T02
- Spec coverage: R1–R6; AC4, AC6, AC7 and full-scope regression of AC1–AC5

## Delivers

All supported build entrypoints use the correct retention boundary; downstream docs and canonical domain artifacts match delivered behavior, with full verification and a reviewable pushed PR.

## Acceptance criteria

- [ ] Separately compiled providers/interfaces and explicitly exported optimized library links work; unlinked library bundles retain complete intended resources.
- [ ] Unknown/missing/bad/version/stale report/catalogue and recognized uninstrumented binary fail meaningfully; actual action dependency enforces report production. No variant roots leak into link evidence.
- [ ] Bind reports to the actual linked JavaScript bytes as well as the catalogue and target identity. A prior report from the same target with unchanged CSS but changed JavaScript must fail. Use a bounded generic factory completion hook after the stock runner writes output; keep final symbol collection in the compiler. Verify alias roots against the output artifact's owning link.
- [ ] Catalogue arguments use a parameter file, and generated-source resolution uses an index rather than scanning every style for every JavaScript input. Verify these bounded scaling details with the existing tests.
- [ ] README/changelog explain downstream opt behavior and closed-world limits; examples demonstrate it; no temporary tooling or redundant docs remain.
- [ ] Domain artifacts re-audited against implementation and local links validated; configured coverage and full gate pass.
- [ ] Simplify pass and independent implementation review pass within budget; full evidence committed before review. After pass remove exact active plan, closeout commit, push PR #112 and report checks. No merge.

## Validation

- Focused complete-link/library/separate compilation/provenance tests.
- `tools/java_format.sh check`, buildifier check, domain link validator; `tools/check.sh` strongest full scope.
- Independent implementation review against committed plan and implementation; check published PR status after push.

## Evidence

- Pending.
