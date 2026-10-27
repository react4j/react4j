# T03 — Consumer guidance and full validation

- Status: pending
- Blocked by: T02
- Spec coverage: R7, R8; AC3, AC4

## Delivers

Checked usage/migration guidance and canonical contracts accompany a fully validated implementation, ready for independent review and PR publication.

## Acceptance criteria

- [ ] Checked examples show nesting ready builders and returning Renderable; migration guidance explains widened custom enhancer setters and preserved explicit build().
- [ ] Unreleased changelog describes the feature and migration impact; qualified domain knowledge has a canonical owner and valid links.
- [ ] The full local/CI gate passes with pinned Bazel, including publication, site and coverage.
- [ ] Implementation scope and validation evidence are committed for independent review; closeout subsequently removes the exact temporary plan and publishes an attached ready-for-review PR after a passing review.

## Validation

- `tools/check.sh` — complete repository gate.
- `python3 /home/peter/.codex/skills/domain-modeling/scripts/validate_doc_links.py .` — domain links and artifact identities.
- Author diff review plus independent implementation review — scope, behavior, checks and canonical knowledge.
- At closeout: verify clean git state and the published PR's head/base/status, attach it to this chat, and report checks without merging.

## Evidence

- Pending.
