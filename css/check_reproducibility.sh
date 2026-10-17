#!/usr/bin/env bash
set -euo pipefail
root="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$root"
bazel build //css/examples/save-button:styles //css/processor:processor_jar
first="$(bazel info bazel-bin)"
scratch="$(mktemp -d "${TMPDIR:-/tmp}/react4j-css-repro.XXXXXX")"
cleanup() {
  bazel --output_base="$scratch/bazel" shutdown >/dev/null 2>&1
  if [[ "$scratch" != "${TMPDIR:-/tmp}/react4j-css-repro."* ]]; then
    echo "Unexpected reproducibility temporary directory: $scratch" >&2
    return 1
  fi
  find "$scratch" -type d -exec chmod u+w {} +
  find "$scratch" -depth -delete
}
trap cleanup EXIT
bazel --output_base="$scratch/bazel" build //css/examples/save-button:styles //css/processor:processor_jar
second="$(bazel --output_base="$scratch/bazel" info bazel-bin)"
for suffix in srcjar css; do
  cmp "$first/css/examples/save-button/styles.$suffix" "$second/css/examples/save-button/styles.$suffix"
done
cmp "$first/css/processor/css-processor.jar" "$second/css/processor/css-processor.jar"
# Restore the ordinary output links before removing the independent checkout.
bazel build //css/examples/save-button:styles //css/processor:processor_jar
echo 'Shaded processor, CSS and generated Java source archive match an independent Bazel build'
