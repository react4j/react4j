#!/usr/bin/env bash
set -euo pipefail

root="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
mode="${1:-write}"
case "$mode" in
  write|check|watch) ;;
  *) echo "Usage: tools/java_format.sh [write|check|watch]" >&2; exit 2 ;;
esac
cd "$root"
paths=(
  tools/generation/src/main/java
  tools/site/src/main/java
  tools/site/src/test/java
  tools/browser/src/main/java
  tools/release/src/main/java
  tools/testing/src/main/java
  core/src/main/java
  core/src/test/java
  dom/src/main/java
  dom/src/test/java
  processor/src/main/java
  processor/src/test/java
  doc-examples/src/main/java
)
format_roots=()
for path in "${paths[@]}"; do
  format_roots+=("--root=$path")
done
case "$mode" in
  check) exec bazel build //:java_format_check ;;
  write) exec bazel run @rules_palantir_java_format//:java_format -- "${format_roots[@]}" ;;
  watch) exec bazel run @rules_palantir_java_format//:java_format_watch -- "${format_roots[@]}" ;;
esac
