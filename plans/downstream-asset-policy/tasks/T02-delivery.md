# T02 — Downstream contract and delivery

- Status: complete
- Blocked by: T01
- Spec coverage: R3, R4; AC3, AC4

## Delivers

Permanent downstream-facing documentation, external-consumer manifest access, complete verification, simplification and reviewed delivery on PR112.

## Acceptance criteria

- [x] Existing specs/ADR/README/changelog and the prepared PR description describe plain outputs and downstream policy ownership.
- [x] External consumer builds the directory and manifest output group.
- [x] Full gate and existing coverage floors pass; simplify is complete and changes are ready for fresh independent implementation review.
- [x] Intentional changes are committed for review; the task map owns final review, publication to PR112 and plan deletion at closeout, without merging.

## Validation

`tools/check.sh`, repository boundary/diff audit, independent review and remote push verification.

## Evidence

`tools/check.sh` passed in full: 45 default tests, eight optimized browser suites, 23 JVM coverage suites, both runtime configurations, all existing coverage floors, external consumer directory and manifest builds in both modes, site, unsigned release validation and all 15 independent reproducibility artifacts. Buildifier, Java formatter, shell syntax, domain-link validation and diff checks passed. Simplify removed the compression-sized preview fixture and clarified the explicit-file test name. No compatibility path or new public controls remain. PR description prepared for publication after review.
