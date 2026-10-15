#!/usr/bin/env bash
set -euo pipefail
if [[ $# -ne 1 ]]; then
  echo "Usage: tools/update_goldens.sh <smoke|generated|regressions>" >&2
  exit 2
fi
case "$1" in
  smoke) family=smoke_goldens ;;
  generated) family=generated_goldens ;;
  regressions) family=regressions ;;
  *) echo "Unknown golden family: $1" >&2; exit 2 ;;
esac
root="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$root"
package=processor/src/test/java/react4j/processor
target="//$package:$family"
bazel build "$target"
binary="$(bazel info bazel-bin)/$package/$family"
fixture=processor/src/test/fixtures
scratch="$(mktemp -d "${TMPDIR:-/tmp}/react4j-goldens.XXXXXX")"
trap 'rm -rf "$scratch"' EXIT
cp -RL "$binary.runfiles/_main/$fixture" "$scratch/fixtures"
bazel run "$target" -- "--wrapper_script_flag=--jvm_flag=-Dreact4j.fixture_dir=$scratch/fixtures"   "--wrapper_script_flag=--jvm_flag=-Dreact4j.output_fixture_data=true" -d "$scratch/test-output"
bazel run //tools/testing/src/main/java/react4j/testing:golden_updater -- "$scratch/fixtures" "$root/$fixture"
bazel test "$target" --test_output=errors
git diff --stat -- "$fixture/expected" "$fixture/expectedFormatted"
