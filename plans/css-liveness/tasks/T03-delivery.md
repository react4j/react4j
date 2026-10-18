# T03 — Verify distribution boundaries and close delivery

- Status: `complete`
- Blocked by: T02
- Spec coverage: R1–R6; AC4, AC6, AC7 and full-scope regression of AC1–AC5

## Delivers

All supported build entrypoints use the correct retention boundary; downstream docs and canonical domain artifacts match delivered behavior, with full verification and a reviewable pushed PR.

## Acceptance criteria

- [x] Separately compiled providers/interfaces and explicitly exported optimized library links work; unlinked library bundles retain complete intended resources.
- [x] Unknown/missing/bad/version/stale report/catalogue and recognized uninstrumented binary fail meaningfully; actual action dependency enforces report production. No variant roots leak into link evidence.
- [x] Bind reports to the actual linked JavaScript bytes as well as the catalogue and target identity. A prior report from the same target with unchanged CSS but changed JavaScript must fail. Use a bounded generic factory completion hook after the stock runner writes output; keep final symbol collection in the compiler. Verify alias roots against the output artifact's owning link.
- [x] Catalogue arguments use a parameter file, and generated-source resolution uses an index rather than scanning every style for every JavaScript input. Verify these bounded scaling details with the existing tests.
- [x] README/changelog explain downstream opt behavior and closed-world limits; examples demonstrate it; no temporary tooling or redundant docs remain.
- [x] Domain artifacts re-audited against implementation and local links validated; configured coverage and full gate pass.
- [x] Simplify and the full gate pass, with implementation and evidence committed for independent review.

## Review and closeout

After this task's implementation is complete, the workflow requires independent implementation review, removal of the exact active plan, a closeout commit, push to PR #112 and a check-status report. No merge. These gates follow task completion; TASKMAP owns their status and review budget.

## Validation

- Focused complete-link/library/separate compilation/provenance tests.
- `tools/java_format.sh check`, buildifier check, domain link validator; `tools/check.sh` strongest full scope.
- Independent implementation review against committed plan and implementation; check published PR status after push.

## Evidence

- Final `tools/check.sh` completed with `All React4j checks passed` after all code changes: formatting, development/optimized hermeticity, publication builds, all 38 test targets, 6 optimized browser targets, JVM/browser coverage, disposable external consumers in both modes, site checks, unsigned release validation and independent matching of all 13 publication/API artifacts.
- CSS JVM coverage: processor 1293/1304 lines and 901/936 branches; internal 552/561 and 312/330; linker 97/97 and 53/58; bundler 402/409 and 235/250. All configured thresholds pass without exemptions.
- Reports now bind actual JS bytes, catalogue and output-owner identity. Unit CLI tests reject JavaScript-only stale reports, changed catalogues, foreign links, missing evidence and duplicate options. Real optimized export-function and alias-root integration passed.
- Three disposable real-build negative controls rejected: raw binary CSS root (`require css_js_binary`), missing factory, and incomplete config/report pair. Probe package removed immediately; no permanent probe harness retained.
- Source identity regression first failed with an incorrectly live default-package binding from an unrelated same-named package file. Exact J2CL package-relative indexing fixed it; linker and integration suites passed.
- Literal-tab regression first corrupted a selector in the pruner; the real Java-origin query also failed (expected column 42, got 45). Parser tab size 1 and character-based Java source columns fix both. Added real parsed-CSS and exact origin assertions; focused suites and the final full gate passed.
- Simplify pass removed duplicate activation validation and report serialization, and replaced source scans with direct indexed lookup. New executables registered as deterministic programs in the existing hermeticity policy; no baseline violations or new exceptions.
- README/changelog updated for downstream users. Domain specification-only replacement audit and glossary first-use links checked; domain link validator passes. Qualified knowledge is committed with this task, ready for independent review.
