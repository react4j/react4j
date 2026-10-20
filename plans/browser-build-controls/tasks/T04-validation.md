# Validation and documentation

- Status: complete
- Blocked by: T03
- Spec coverage: R5 / AC5

## Delivers

Update durable user-facing documentation/contracts and changelog, classify domain knowledge, run strongest proportionate gates and prepare both PR descriptions with actual performance evidence and remaining blockers. Inspect/simplify the whole intended diff and prepare cleanup inventory.

## Acceptance

- [x] Delivered behavior satisfies assigned spec criteria.
- [x] Meaningful verification passes and limitations are explicit.

## Validation

Full tools/check.sh; Rose relevant tests/graphs; documentation links; external consumer; clean scoped diff; reviewed publication text and timing evidence.

## Evidence

Full tools/check.sh passed: formatting, Buildifier, Ahab in both modes, JVM/browser suites in both modes, coverage floors, isolated native-J2CL consumer with copied examples and enforced public visibility, site build/check, publication validation and all 15 independent-build reproducibility comparisons. Bundler root: 980/1004 lines (97.6%), 542/593 branches (91.4%). Main suite: 51 tests; optimized browser suite includes the eight runtime/optimization/map cases. The new matrix serves binary resources and checks applied CSS, decoded images, actual primitive types and a framework-default override.

Root and Rose documentation link validators passed. The specification-only audit leaves accepted independent settings, typed defines, map publication/internal mapping, caller-owned HTML, optional downstream compression and local build separation in canonical specs; CLI names and current file layout stay in user documentation/code. Existing ADR0043 reflects optional compression. No glossary/use-case/deferred artifact is needed. Simplify removed obsolete map-input acceptance, unused archive extraction paths and a custom launcher in favor of native java_binary/Java runfiles; private helpers and peer conventions checked.

Rose seven tool targets passed; actual public fixture passed with compression enabled and disabled. Manual fixture server, configured production/local graphs and the unchanged real Acal failure are recorded in T03. Scoped Java formatting/Buildifier and both diffs passed. Draft publication text is in this tree for closeout; final immutable remote pin/module-lock resolution follows review/publication ordering. Whole Rose link/WAR/browser/iteration parity remains blocked by the 393 dependency ownership errors, as explicitly bounded by SPEC.
