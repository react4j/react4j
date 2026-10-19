# T04 — Contract and delivery

- Status: complete
- Blocked by: T03
- Spec coverage: R5, R6; AC3, AC4

## Delivers

Permanent downstream setup/build documentation, accurate helper explanations, full validation and simplified implementation ready for independent review.

## Acceptance criteria

- [x] README/setup/CSS/contributor/release docs use the final API; unrelated changelog rows remain intact.
- [x] Durable requirements/decisions, if qualifying, are promoted to their existing owner and links validate.
- [x] Full `tools/check.sh` and existing coverage floors pass; scope/diff/simplify audits complete.
- [x] Changes committed for fresh independent review; workflow owns final push and plan deletion.

## Validation

Full gate, domain-link validation, obsolete-label and visibility audits; independent review before closeout.

## Evidence

- Full tools/check.sh passes: formatting, both hermeticity modes, 45 default suites, eight optimized browser suites, 23 JVM coverage suites, runtime coverage and every configured floor, native external application/browser/checker/visibility matrix, site build/check, unsigned release validation and independent artifact reproducibility.
- Simplify removes duplicate failure-check plumbing and redundant downstream dependencies; native views now need only public dom, and the CSS/asset example adds css/catalogue dependencies. The full gate verifies this reduced dependency graph.
- Obsolete-label audit finds no active old root callers; only explicit denial probes and internal CSS backings remain. Single CSS/asset changelog row and unrelated TodoMVC/DOM entries are preserved.
- Domain classification and canonical reader-documentation owners are recorded in TASKMAP. Domain link validation and complete site validation pass. No qualifying new domain artifact is required.

- Review correction: qualify all Java 17 guarantees against native J2CL intermediate Java 21 bytecode; site/domain-link checks pass after the documentation-only fix.
