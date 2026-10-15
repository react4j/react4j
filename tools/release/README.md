# Publication archives

`bazel build //tools/release:maven_artifacts` produces binary, source, Javadoc and POM files for `react4j-core`, `react4j-dom` and `react4j-processor`. `//tools/release:site_javadoc` builds the aggregate API archive.

The public targets `//:core`, `//:dom`, `//:processor` and `//:react4j_processor_plugin` consume these assembled binary jars. Libraries and generated consumer classes target Java 17; annotation processing requires Java 21. Processor dependencies are shaded into its jar.

Binary jars contain bytecode and metadata. Source jars contain all authored and generated Java, native JavaScript, and the pinned React/ReactDOM assets. J2CL consumers use a byte-preserving projection of these source archives that excludes only the public UMD asset directories from Closure compilation. Browser pages load the UMD scripts separately. Source archives depend on checked JVM class outputs, so building a J2CL consumer also enforces the owned Java checker policy.

Run `bazel test //tools/release:archive_checks //tools/release/consumer/src/main/java/isolated:packaged_consumer` to inspect the artifacts and exercise both the public Bazel targets and an isolated Java 21 compiler/runtime. `tools/release/check_reproducibility.sh` compares all 13 archives/POMs against an independent Bazel build and removes its temporary build data.

## Dependency publication prerequisite

The build uses the migrated Arez, Proton and Sting revisions pinned in `MODULE.bazel`. Publishing React4j requires an Arez release on Maven Central that contains the pinned migrated contents.

Both version inputs default to `0.0.0-SNAPSHOT` for local work. Set `--//tools/release:version=<react4j-version>` and `--//tools/release:arez_version=<published-arez-version>` when preparing release artifacts. The second input selects the POM dependency version; it does not change the pinned source dependency. Verify that the selected Arez release contains the pinned migration before uploading anything.

Proton is embedded in the shaded processor jar, so consumers do not need a separate Proton Maven dependency. Consumers using Sting integration must also use a compatible migrated Sting release.
