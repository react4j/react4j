# T02 — Public learning paths and knowledge audit

- Status: pending
- Blocked by: T01
- Spec coverage: R3,R4,R5,R6,R7; AC2,AC3,AC4,AC5

## Delivers

Consumer-first website guidance, a linked agent/rule-author learning path and precise reference, portable worked recipes, and a short Tailwind comparison. The website packages the actual teaching app. Existing canonical domain owners retain requirements and rationale; only qualifying omissions are promoted.

## Acceptance criteria

- [ ] Public navigation leads to using utilities, authoring rules, recipes/reference and the runnable app; repository entry points link the same learning paths.
- [ ] Guide teaches the app's actual vocabulary, composition and explicit global/container/popover setup without implying built-in Tailwind utilities.
- [ ] Validate/describe/expand instructions explain how to obtain a real checked provider, prepared versus checked state, exit statuses and diagnostic repair. Verify commands and positive/negative recipe cases with the real JVM compiler.
- [ ] Recover portable table/shadow/negative-margin/direct-declaration/popover examples, ordering and Java escape pitfalls; remove stale process narration and redundant description.
- [ ] Audit old design conclusions to one current owner each, promote qualifying gaps, validate domain links and remove stale inbound references.
- [ ] Build/check website, run full repository gate, review complete implementation, and record all evidence before closing the plan.

## Validation

- Real authoring CLI commands and compiled provider descriptor; exact documented successful and intentionally invalid requests.
- `tools/site.sh build` and `tools/site.sh check`.
- Domain skill `scripts/validate_doc_links.py` against repository root; inspect specification-only diff if specifications change.
- `tools/check.sh` full gate, plus existing targeted CssInputIntegrationTest run evidence.

## Evidence

Pending.
