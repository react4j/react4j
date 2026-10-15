# T12 — Explicit release operations

- Status: `pending`
- Blocked by: `T11`
- Spec coverage: `R8`, `R11`, `AC4`, `AC7`

## Delivers

Provide release preparation, validation, signing/upload/tag/GitHub-release commands and site deploy integration using Bash/Java where useful. Remove downstream orchestration and record unreleased Maven dependency prerequisites.

## Acceptance criteria

- [ ] Unsigned bundle validates, help and dry-run/prepare paths work without credentials or remote writes, dependency versions are explicit/truthful, publication commands remain separate from validation.

## Validation

- tools/release.sh help; prepare/validate unsigned bundle; command parsing/dry-run tests and POM prerequisite checks; no signing/upload/tag/push execution — proves the delivered behavior. Record actual labels, commands and outcomes below; do not mark complete with skipped required checks.

## Evidence

- pending
