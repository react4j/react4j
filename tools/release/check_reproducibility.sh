#!/usr/bin/env bash
set -euo pipefail
root="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
cd "$root"
bazel build //tools/release:maven_artifacts //tools/release:site_javadoc "$@"
first="$(bazel info bazel-bin)/tools/release"
scratch="$(mktemp -d "${TMPDIR:-/tmp}/react4j-reproducibility.XXXXXX")"
cleanup() {
  bazel --output_base="$scratch/bazel" shutdown >/dev/null 2>&1
  if [[ "$scratch" != "${TMPDIR:-/tmp}/react4j-reproducibility."* ]]; then
    echo "Unexpected reproducibility temporary directory: $scratch" >&2
    return 1
  fi
  find "$scratch" -type d -exec chmod u+w {} +
  find "$scratch" -depth -delete
}
trap cleanup EXIT
bazel --output_base="$scratch/bazel" build //tools/release:maven_artifacts //tools/release:site_javadoc "$@"
second="$(bazel --output_base="$scratch/bazel" info bazel-bin)/tools/release"
for module in core dom processor; do
  for suffix in .jar -sources.jar -javadoc.jar .pom; do
    cmp "$first/react4j-$module$suffix" "$second/react4j-$module$suffix"
  done
done
cmp "$first/react4j-api-javadoc.jar" "$second/react4j-api-javadoc.jar"
echo "All 13 publication/API artifacts match an independent Bazel build"
