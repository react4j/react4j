#!/usr/bin/env bash
set -euo pipefail
root="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$root"
bazel build //css/example:styles
first="$(bazel info bazel-bin)/css/example"
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
bazel --output_base="$scratch/bazel" build //css/example:styles
second="$(bazel --output_base="$scratch/bazel" info bazel-bin)/css/example"
for suffix in srcjar css css.br; do
  cmp "$first/styles.$suffix" "$second/styles.$suffix"
done
# Restore the ordinary output links before removing the independent checkout.
bazel build //css/example:styles
echo 'CSS, Brotli and generated Java source archive match an independent Bazel build'
