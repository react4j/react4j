#!/usr/bin/env bash
set -euo pipefail
if [[ $# -ne 1 || "$1" == --help ]]; then
  echo 'Usage: tools/export_bazel_config.sh <application-directory>'
  [[ $# -eq 1 && "$1" == --help ]] && exit 0 || exit 2
fi
application="$(cd "$1" && pwd)"
root="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$root"
exec bazel run //tools/consumer:config -- "$root" "$application"
