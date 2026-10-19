#!/usr/bin/env bash
set -euo pipefail
root="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
cd "$root"
bazel build //tools/release:maven_artifacts //tools/release:site_javadoc //css/core //css/processor:processor_jar "$@"
first_root="$(bazel info bazel-bin)"
first="$first_root/tools/release"
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
bazel --output_base="$scratch/bazel" build //tools/release:maven_artifacts //tools/release:site_javadoc //css/core //css/processor:processor_jar "$@"
second_root="$(bazel --output_base="$scratch/bazel" info bazel-bin)"
second="$second_root/tools/release"
for module in core dom processor; do
  for suffix in .jar -sources.jar -javadoc.jar .pom; do
    cmp "$first/react4j-$module$suffix" "$second/react4j-$module$suffix"
  done
done
cmp "$first/react4j-api-javadoc.jar" "$second/react4j-api-javadoc.jar"
cmp "$first_root/css/core/src/main/java/react4j/css/libcss.jar" "$second_root/css/core/src/main/java/react4j/css/libcss.jar"
cmp "$first_root/css/processor/css-processor.jar" "$second_root/css/processor/css-processor.jar"
echo "All 15 publication/API/CSS artifacts match an independent Bazel build"
