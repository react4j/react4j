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
  css/src/main/java
  css/src/test/java
  css/example
  tools/generation/src/main/java
  tools/site/src/main/java
  tools/site/src/test/java
  tools/browser/src/main/java
  tools/consumer/src/main/java
  tools/verification/src/main/java
  tools/release/src/main/java
  tools/release/src/test/java
  tools/testing/src/main/java
  core/src/main/java
  core/src/test/java
  dom/src/main/java
  dom/src/test/java
  processor/src/main/java
  processor/src/test/java
  examples/drumloop/src/main/java
  examples/drumloop/src/test/java
  examples/heart-rate-monitor/src/main/java
  examples/todomvc/raw/src/main/java
  examples/todomvc/arez/src/main/java
  examples/todomvc/sting/src/main/java
  examples/todomvc/src/main/java
  examples/webspeechdemo/src/main/java
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
