# Publication archives

`bazel build //tools/release:maven_artifacts` produces binary, source, Javadoc and POM files for `react4j-core`, `react4j-dom` and `react4j-processor`. `//tools/release:site_javadoc` builds the aggregate API archive.

Public `//:core` and `//:dom` provide J2CL libraries from the assembled source archives; `//:processor` wraps the shaded processor jar as a plugin. JVM jar imports are repository-only. Libraries and generated consumer classes target Java 17; annotation processing requires Java 21. Processor dependencies are shaded into its jar.

Binary jars contain bytecode and metadata. Source jars contain all authored and generated Java, native JavaScript, and the pinned React/ReactDOM assets. J2CL consumers use a byte-preserving projection of these source archives that excludes only the public UMD asset directories from Closure compilation. Browser pages load the UMD scripts separately. Source archives depend on checked JVM class outputs, so building the publication sources for J2CL also checks React4j's unstripped Java under the repository policy. Downstream application sources use native J2CL and their own checker selection.

Run `bazel test //tools/release:archive_checks //tools/release/consumer/src/main/java/isolated:packaged_consumer` to inspect the artifacts and exercise an isolated Java 21 compiler/runtime. `tools/check_bazel_consumer.sh` verifies the public J2CL targets separately. `tools/release/check_reproducibility.sh` compares the publication archives and POMs against an independent Bazel build and removes its temporary build data.

## Dependency publication prerequisite

The build uses the migrated Arez, Proton and Sting revisions pinned in `MODULE.bazel`. Publishing React4j requires an Arez release on Maven Central that contains the pinned migrated contents.

Both version inputs default to `0.0.0-SNAPSHOT` for local work. Set `--//tools/release:version=<react4j-version>` and `--//tools/release:arez_version=<published-arez-version>` when preparing release artifacts. The second input selects the POM dependency version; it does not change the pinned source dependency. Verify that the selected Arez release contains the pinned migration before uploading anything.

Proton is embedded in the shaded processor jar, so consumers do not need a separate Proton Maven dependency. Consumers using Sting integration must also use a compatible migrated Sting release.

## Release commands

Use `tools/release.sh --help`. Each operation is separate; append `--dry-run` to print its commands without executing them. `prepare <version> [arez-version]` creates `dist/react4j-<version>.zip`; `validate` checks the archives and isolated consumer, builds an unsigned bundle in a temporary directory, verifies it, and removes the temporary data. These paths need no publishing credentials. Unsigned local work may use snapshot versions.

For a release, choose a numeric React4j version and an actual published Arez version containing the pinned migration. The version input is a declaration of that prerequisite, not proof that an arbitrary existing Arez release contains these changes. Review that dependency release before publication.

1. Run `tools/release.sh prepare-notes <version>` to move Unreleased notes into a dated release and update the single `react4j.version` property in project setup. Review the diff, run `tools/check.sh`, and commit it.
2. Run `tools/release.sh tag <version> <arez-version>` to create an annotated local tag on the clean checkout. `push-tag` explicitly publishes that tag.
3. Run `tools/release.sh sign <version> <arez-version>` from that exact tag. This validates the real artifacts first, then signs with the configured GPG agent/key (`GPG_USER` optionally chooses a key). The signed bundle includes detached signatures and MD5, SHA-1, SHA-256 and SHA-512 checksums. Verification invokes GPG; a signature envelope alone is insufficient.
4. Run `tools/release.sh upload <version> <arez-version>` to upload and automatically publish the signed bundle after Central validation. Set `MAVEN_CENTRAL_TOKEN` to the base64 Portal user token. The Java client follows the [Central Portal Publisher API](https://central.sonatype.org/publish/publish-portal-api/) and prints the deployment ID; monitor its final state in the Portal. Upload success means accepted for validation, not confirmed publication.
5. Run `tools/release.sh github-release <version> <arez-version>` after the tag is published. It uses the prepared changelog notes and `gh release create --verify-tag`.
6. Run `tools/release.sh deploy-site <version> <arez-version>` to clone the site repository, prepare its checked contents while preserving external application directories, commit, and push. `tools/site.sh prepare-deploy <local-checkout>` provides the reviewable local preparation step.

Tag creation requires a clean checkout. Signing, tag publication, upload, GitHub releases and site deployment require a clean checkout at the matching tag; snapshot product/dependency versions are rejected. None of these operations runs downstream repository builds or updates. The shared check gate only executes local unsigned validation.
