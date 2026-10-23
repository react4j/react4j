#!/usr/bin/env bash
set -euo pipefail
root="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$root"
command="${1:---help}"
example="${2:-hello_world}"
mode="${3:-development}"
case "$example" in
  hello_world) target=//doc-examples:hello_world; output=doc-examples/hello_world ;;
  drumloop) target=//examples/drumloop:drumloop; output=examples/drumloop/drumloop ;;
  heart-rate-monitor) target=//examples/heart-rate-monitor:heart-rate-monitor; output=examples/heart-rate-monitor/heart-rate-monitor ;;
  todomvc-raw) target=//examples/todomvc/raw:raw; output=examples/todomvc/raw/raw ;;
  todomvc-arez) target=//examples/todomvc/arez:arez; output=examples/todomvc/arez/arez ;;
  todomvc-sting) target=//examples/todomvc/sting:sting; output=examples/todomvc/sting/sting ;;
  utility-css) target=//examples/utility-css; output=examples/utility-css/utility-css ;;
  webspeechdemo) target=//examples/webspeechdemo:webspeechdemo; output=examples/webspeechdemo/webspeechdemo ;;
  *) echo "Unknown example: $example" >&2; exit 2 ;;
esac
case "$mode" in
  development) options=(-c fastbuild) ;;
  production) options=(-c opt) ;;
  *) echo "Unknown mode: $mode" >&2; exit 2 ;;
esac
case "$command" in
  build|serve)
    bazel build "${options[@]}" "$target"
    directory="$(bazel info "${options[@]}" bazel-bin)/$output"
    if [[ "$command" == serve ]]; then
      exec bazel run //tools/browser:preview -- "$directory"
    fi
    echo "$directory/index.html"
    ;;
  --help|-h) echo "Usage: tools/examples.sh <build|serve> [hello_world|drumloop|heart-rate-monitor|todomvc-raw|todomvc-arez|todomvc-sting|utility-css|webspeechdemo] [development|production]" ;;
  *) echo "Unknown command: $command" >&2; exit 2 ;;
esac
