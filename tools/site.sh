#!/usr/bin/env bash
set -euo pipefail
root="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$root"
action="${1:---help}"
case "$action" in
  build|check|serve|prepare-deploy)
    if [[ "$action" == prepare-deploy && $# -ne 2 ]]; then
      echo "Usage: tools/site.sh prepare-deploy <local-site-checkout>" >&2; exit 2
    fi
    bazel build -c opt //website:site
    directory="$(bazel info -c opt bazel-bin)/website/site"
    case "$action" in
      build) echo "$directory" ;;
      check|serve) exec bazel run //tools/site:tool -- "$action" "$directory" ;;
      prepare-deploy) exec bazel run //tools/site:tool -- prepare-deploy "$directory" "$2" ;;
    esac
    ;;
  --help|-h) echo "Usage: tools/site.sh <build|check|serve|prepare-deploy> [local-site-checkout]" ;;
  *) echo "Unknown command: $action" >&2; exit 2 ;;
esac
