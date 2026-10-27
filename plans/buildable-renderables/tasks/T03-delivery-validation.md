# T03 — Consumer guidance and full validation

- Status: complete
- Blocked by: T02
- Spec coverage: R7, R8; AC3, AC4

## Delivers

Checked usage/migration guidance and canonical contracts accompany a fully validated implementation, ready for independent review and PR publication.

## Acceptance criteria

- [x] Checked examples show nesting ready builders and returning Renderable; migration guidance explains widened custom enhancer setters and preserved explicit build().
- [x] Unreleased changelog describes the feature and migration impact; qualified domain knowledge has a canonical owner and valid links.
- [x] The full local/CI gate passes with pinned Bazel, including publication, site and coverage.
- [x] Implementation scope and validation evidence are committed for independent review; closeout subsequently removes the exact temporary plan and publishes an attached ready-for-review PR after a passing review.

## Validation

- `tools/check.sh` — complete repository gate.
- `python3 /home/peter/.codex/skills/domain-modeling/scripts/validate_doc_links.py .` — domain links and artifact identities.
- Author diff review plus independent implementation review — scope, behavior, checks and canonical knowledge.
- At closeout: verify clean git state and the published PR's head/base/status, attach it to this chat, and report checks without merging.

## Evidence

- Checked Notice/NoticeHost examples compile, and children presets now nest ready builders. The guide, annotation Javadoc and Unreleased notes explain Renderable return types and custom enhancer migration.
- Promoted composition requirements and native-boundary ADR; existing enhancer requirement references the projected setter type. Specification-only audit and domain link validator passed.
- Simplify review centralized input conversion without changing generated output. Extra browser cases verify opaque payloads, covariant builder arrays, callback errors and eager root scheduling.
- Initial full gate found two obsolete hardcoded ReactElement line numbers in the existing map-hole regression. Source-map decoding and javap confirmed the same unmapped executable expressions at their new locations. The regression now locates those expressions in the publication source archive; it still verifies absent mappings, independent JVM obligations and exact Chromium hits. Focused runtime_coverage_tests passed.
- Full tools/check.sh then passed: 58 test targets; 17 optimized browser targets; JVM/browser coverage thresholds; format/hermeticity; source/binary/Javadoc artifacts; site build/link checks; unsigned bundle verification (12 artifacts, 60 entries).
- Coverage: core 251/257 lines, 54/59 branches; DOM 1750/1789, 14/14; processor 3478/3604, 1790/2054. No coverage policy changes.
