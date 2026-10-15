# T12 — Explicit release operations

- Status: `complete`
- Blocked by: `T11`
- Spec coverage: `R8`, `R11`, `AC4`, `AC7`

## Delivers

Provide release preparation, validation, signing/upload/tag/GitHub-release commands and site deploy integration using Bash/Java where useful. Remove downstream orchestration and record unreleased Maven dependency prerequisites.

## Acceptance criteria

- [x] Unsigned bundle validates, help and dry-run/prepare paths work without credentials or remote writes, dependency versions are explicit/truthful, publication commands remain separate from validation.

## Validation

- tools/release.sh help; prepare/validate unsigned bundle; command parsing/dry-run tests and POM prerequisite checks; no signing/upload/tag/push execution — proves the delivered behavior. Record actual labels, commands and outcomes below; do not mark complete with skipped required checks.

## Evidence

- `tools/release.sh --help`, all nine operations with `--dry-run`, Bash syntax and invalid-argument/snapshot-prerequisite rejection checks pass. Dry runs print the same command arguments used by execution; no command stubs or placeholder signatures were used.
- `tools/release.sh prepare 0.231` and `tools/release.sh validate 0.231`: pass using truthful default Arez `0.0.0-SNAPSHOT`. Unsigned bundle contains the real 12 publication artifacts and 48 checksums (MD5/SHA-1/SHA-256/SHA-512). Validation also passed archive inspections and the isolated packaged consumer. Temporary validation directories removed; the one-off prepare bundle is removed during cleanup.
- `bazel test //tools/release:tests //:java_format_check`: all three distribution tests pass. Permanent bundle tests use real Bazel publication files and prove repeatability, checksum corruption/missing/extra-entry rejection, dependency-version consistency, snapshot publication rejection and release-note preparation/no-write-on-rejection. Note preparation is tested in a temporary project fixture; T13 installs its single Maven version property in current setup docs.
- Explicit clean-checkout/tag commands cover local annotated tag, tag push, signed bundle, Central upload, GitHub release and site deployment. Signing first executes unsigned validation; signature verification invokes real GPG. Publication modes reject snapshot React4j/Arez versions and require the exact requested tag rather than arbitrary git-describe selection. No signing, tagging, upload, GitHub release, deploy commit or push executed.
- Central API request shape verified against Sonatype's official Publisher API documentation. Upload uses AUTOMATIC publication and prints the accepted deployment ID; docs distinguish acceptance from final publication and require monitoring. Token comes only from the environment; encrypted repository secrets remain untouched.
- Buildifier, Java format and release-tool help pass. The release guide documents the pinned-source versus future Maven publication prerequisite and each separate operation.
