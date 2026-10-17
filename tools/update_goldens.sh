#!/usr/bin/env bash
set -euo pipefail
if [[ $# -ne 1 ]]; then
  echo "Usage: tools/update_goldens.sh <generated|jspecify|regressions|css>" >&2
  exit 2
fi
case "$1" in
  generated) family=generated_goldens ;;
  jspecify) family=jspecify_goldens ;;
  regressions) family=regressions ;;
  css) family=processor_tests ;;
  *) echo "Unknown golden family: $1" >&2; exit 2 ;;
esac
root="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$root"
package=processor/src/test/java/react4j/processor
fixture=processor/src/test/fixtures
trees=(expected expectedFormatted)
if [[ "$1" == css ]]; then
  package=css/processor/src/test/java/react4j/css/processor
  fixture=css/processor/src/test/fixtures
fi
target="//$package:$family"
bazel build "$target"
binary="$(bazel info bazel-bin)/$package/$family"
scratch="$(mktemp -d "${TMPDIR:-/tmp}/react4j-goldens.XXXXXX")"
trap 'rm -rf "$scratch"' EXIT
cp -RL "$binary.runfiles/_main/$fixture" "$scratch/fixtures"
mkdir "$scratch/compilations" "$scratch/baseline"
for tree in "${trees[@]}"; do
  cp -R "$scratch/fixtures/$tree" "$scratch/baseline/$tree"
done
bazel run "$target" -- \
  "--wrapper_script_flag=--jvm_flag=-Djava.io.tmpdir=$scratch/compilations" \
  "--wrapper_script_flag=--jvm_flag=-Dreact4j.fixture_dir=$scratch/fixtures" \
  "--wrapper_script_flag=--jvm_flag=-Dreact4j.output_fixture_data=true" -d "$scratch/test-output"
bazel run //tools/testing/src/main/java/react4j/testing:golden_updater -- "$scratch/fixtures" "$scratch/baseline" "$root/$fixture" "${trees[@]}"
bazel test "$target" --test_output=errors
paths=()
for tree in "${trees[@]}"; do
  paths+=("$fixture/$tree")
done
git diff --stat -- "${paths[@]}"
