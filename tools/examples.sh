#!/usr/bin/env bash
set -euo pipefail
root="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$root"
command="${1:---help}"
mode="${2:-development}"
case "$mode" in
  development) options=(-c fastbuild) ;;
  production) options=(-c opt) ;;
  *) echo "Unknown mode: $mode" >&2; exit 2 ;;
esac
case "$command" in
  build|serve)
    bazel build "${options[@]}" //doc-examples:hello_world
    directory="$(bazel info "${options[@]}" bazel-bin)/doc-examples/hello_world"
    if [[ "$command" == serve ]]; then
      exec bazel run //tools/browser:preview -- "$directory"
    fi
    echo "$directory/index.html"
    ;;
  --help|-h) echo "Usage: tools/examples.sh <build|serve> [development|production]" ;;
  *) echo "Unknown command: $command" >&2; exit 2 ;;
esac
