#!/usr/bin/env bash
set -euo pipefail
root="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$root"
test "$(bazel --version)" = 'bazel 9.3.0'
bazel run //tools/buildifier:buildifier_check
tools/java_format.sh check
bazel run //tools/ahab:hermeticity.check
bazel run //tools/ahab:hermeticity.check -- --compilation-mode=opt
bazel build //core //dom //processor //doc-examples:java //:core //:dom //tools/generation:assets //tools/release:maven_artifacts //tools/release:site_javadoc
bazel test //:all_tests --test_output=errors
bazel test -c opt //:browser_tests --test_output=errors
bazel coverage -c dbg //:jvm_tests --combined_report=lcov '--instrumentation_filter=^//(processor|css/(processor|internal|bundler|linker))(:|/),-/src/test/' --test_output=errors
coverage_report="$(bazel info -c dbg output_path)/_coverage/_coverage_report.dat"
mkdir -p reports/coverage
cp -f "$coverage_report" reports/coverage/jvm.lcov
bazel run //tools/verification:runtime_coverage -- "$root/reports/coverage/runtime.lcov"
bazel run //tools/verification:coverage_check -- "$root/tools/verification/coverage.json" "$root/reports/coverage" "$root/reports/coverage/summary.txt"
tools/check_bazel_consumer.sh
tools/site.sh build
tools/site.sh check
tools/release.sh validate 0.0.0-SNAPSHOT
tools/release/check_reproducibility.sh
# Restore ordinary output links after the independent reproducibility build.
bazel build //tools/release:maven_artifacts //tools/release:site_javadoc
echo 'All React4j checks passed.'
