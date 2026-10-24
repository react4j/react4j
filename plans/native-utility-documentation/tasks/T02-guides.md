# T02 — Public learning paths and knowledge audit

- Status: complete
- Blocked by: T01
- Spec coverage: R3,R4,R5,R6,R7; AC2,AC3,AC4,AC5

## Delivers

Consumer-first website guidance, a linked agent/rule-author learning path and precise reference, portable worked recipes, and a short Tailwind comparison. The website packages the actual teaching app. Existing canonical domain owners retain requirements and rationale; only qualifying omissions are promoted.

## Acceptance criteria

- [x] Public navigation leads to using utilities, authoring rules, recipes/reference and the runnable app; repository entry points link the same learning paths.
- [x] Guide teaches the app's actual vocabulary, composition and explicit global/container/popover setup without implying built-in Tailwind utilities.
- [x] Validate/describe/expand instructions explain how to obtain a real checked provider, prepared versus checked state, exit statuses and diagnostic repair. Verify commands and positive/negative recipe cases with the real JVM compiler.
- [x] Recover portable table/shadow/negative-margin/direct-declaration/popover examples, ordering and Java escape pitfalls; remove stale process narration and redundant description.
- [x] Audit old design conclusions to one current owner each, promote qualifying gaps, validate domain links and remove stale inbound references.
- [x] Build/check website and full repository gate pass; commit evidence and promoted artifacts for independent review before closeout.

## Validation

- Real authoring CLI commands and compiled provider descriptor; exact documented successful and intentionally invalid requests.
- `tools/site.sh build` and `tools/site.sh check`.
- Domain skill `scripts/validate_doc_links.py` against repository root; inspect specification-only diff if specifications change.
- `tools/check.sh` full gate, plus existing targeted CssInputIntegrationTest run evidence.

## Evidence

- Public pages: `docs/native-utilities.md`, `docs/utility-rules.md`, `docs/utility-recipes.md`; navigation and optimized teaching app registered in website build. The module README now owns implementation boundaries and links the public learning paths.
- `verify_guides.py` passes: exact documented build/validate/describe/expand commands; three-error batch; file packaging plus real provider and consumer javac compilation; 16 recipe expansions and 2 negative cases; all 5 author-reference CSS sections/19 expansions; actual Java escape snippets; empty/duplicate rejection; separate case-sensitive namespaces.
- Domain knowledge audit and specification-only diff audit complete. Native requirements 25/26 capture missing accepted invariants; bindings clarify distinct lists. Existing ADRs/glossary/deferred records retain their ownership. Domain link validator passes.
- `tools/site.sh build` / `tools/site.sh check`: 27,157 local file/fragment links, zero broken. Rendered guide has one title, visible CSS navigation and a working link to the packaged optimized app; inspected in browser.
- `tools/check.sh`: exit 0, “All React4j checks passed.” Includes formatter/buildifier, both hermeticity modes, compilation/release archives, JVM/browser tests, optimized browser tests, enforced JVM/runtime coverage, site build/check and unsigned release validation. Terminal evidence (trailing whitespace removed): `../full-gate.log`.
- Added one exact existing-policy loopback exception for the new browser TestRunner after the first hermeticity check identified the omitted registration. Baseline remains zero violations; filesystem sandboxing remains enabled.
- No compiler behavior or public API changed. Independent implementation review and plan deletion remain post-task workflow gates in TASKMAP.

Preview instructions name `127.0.0.1:3000`, the actual bound/verified endpoint; this machine has a distinct IPv6 localhost service. No shared preview configuration changed.
