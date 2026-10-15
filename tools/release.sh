#!/usr/bin/env bash
set -euo pipefail
root="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$root"
usage() {
  echo 'Usage: tools/release.sh <prepare|validate|sign|upload|tag|push-tag|github-release|deploy-site> <version> [arez-version] [--dry-run]'
  echo '       tools/release.sh prepare-notes <version> [--dry-run]'
  echo 'prepare/validate allow snapshots; all publication steps require numeric versions.'
}
if [[ $# -eq 0 || "$1" == --help || "$1" == -h ]]; then usage; exit 0; fi
action="$1"
shift
dry_run=false
arguments=()
for argument in "$@"; do
  if [[ "$argument" == --dry-run ]]; then dry_run=true; else arguments+=("$argument"); fi
done
# Bash 3 treats an empty array as unset with nounset.
if [[ ${#arguments[@]} -lt 1 || ${#arguments[@]} -gt 2 ]]; then usage >&2; exit 2; fi
version="${arguments[0]}"
arez_version="${arguments[1]:-0.0.0-SNAPSHOT}"
if [[ ! "$version" =~ ^[0-9]+(\.[0-9]+){1,2}(-SNAPSHOT)?$ || ! "$arez_version" =~ ^[0-9]+(\.[0-9]+){1,2}(-SNAPSHOT)?$ ]]; then
  echo 'Invalid version' >&2; exit 2
fi
case "$action" in
  prepare|validate) ;;
  prepare-notes) if [[ "$version" == *-SNAPSHOT || ${#arguments[@]} -ne 1 ]]; then usage >&2; exit 2; fi ;;
  sign|upload|tag|push-tag|github-release|deploy-site)
    if [[ "$version" == *-SNAPSHOT || "$arez_version" == *-SNAPSHOT ]]; then
      echo 'Publication requires numeric React4j and migrated published Arez versions.' >&2; exit 2
    fi ;;
  *) usage >&2; exit 2 ;;
esac
run() {
  if "$dry_run"; then printf '%q ' "$@"; printf '\n'; else "$@"; fi
}
require_clean() {
  if ! "$dry_run" && [[ -n "$(git status --porcelain)" ]]; then
    echo 'Release operations require a clean checkout.' >&2; exit 1
  fi
}
require_tag() {
  require_clean
  if ! "$dry_run" && [[ "$(git rev-parse "refs/tags/v$version^{commit}" 2>/dev/null || true)" != "$(git rev-parse HEAD)" ]]; then
    echo "Run from the v$version tag." >&2; exit 1
  fi
}
build_artifacts() {
  run bazel build //tools/release:maven_artifacts "--//tools/release:version=$version" "--//tools/release:arez_version=$arez_version"
}
outputs="$root/bazel-bin/tools/release"
bundle="$root/dist/react4j-$version.zip"
case "$action" in
  prepare-notes)
    run bazel run //tools/release:bundle_tool -- prepare-notes "$root" "$version" "$(date +%F)"
    ;;
  prepare|validate|sign)
    mode=unsigned
    if [[ "$action" == sign ]]; then
      require_tag
      run tools/release.sh validate "$version" "$arez_version"
      mode=signed
    fi
    build_artifacts
    if ! "$dry_run"; then outputs="$(bazel info bazel-bin)/tools/release"; fi
    scratch=""
    if [[ "$action" == validate ]]; then
      if "$dry_run"; then scratch=/tmp/react4j-release-validation; else scratch="$(mktemp -d "${TMPDIR:-/tmp}/react4j-release-validation.XXXXXX")"; fi
      bundle="$scratch/bundle.zip"
      if ! "$dry_run"; then trap 'find "$scratch" -depth -delete' EXIT; fi
      run bazel test //tools/release:archive_checks //tools/release/consumer/src/main/java/isolated:packaged_consumer "--//tools/release:version=$version" "--//tools/release:arez_version=$arez_version" --test_output=errors
    fi
    run bazel run //tools/release:bundle_tool -- pack "$outputs" "$version" "$arez_version" "$bundle" "$mode"
    run bazel run //tools/release:bundle_tool -- verify "$bundle" "$version" "$arez_version" "$mode"
    ;;
  upload)
    require_tag
    run bazel run //tools/release:bundle_tool -- upload "$bundle" "$version" "$arez_version"
    ;;
  tag)
    require_clean
    run bazel run //tools/release:bundle_tool -- notes "$root/CHANGELOG.md" "$version" "$root/dist/release-notes-$version.md"
    run git tag -a "v$version" -m "Release React4j $version"
    ;;
  push-tag)
    require_tag
    run git push origin "refs/tags/v$version"
    ;;
  github-release)
    require_tag
    run bazel run //tools/release:bundle_tool -- notes "$root/CHANGELOG.md" "$version" "$root/dist/release-notes-$version.md"
    run gh release create "v$version" --repo react4j/react4j --verify-tag --title "React4j $version" --notes-file "$root/dist/release-notes-$version.md"
    ;;
  deploy-site)
    require_tag
    run tools/site.sh build
    if "$dry_run"; then
      scratch=/tmp/react4j-site-release
    else
      scratch="$(mktemp -d "${TMPDIR:-/tmp}/react4j-site-release.XXXXXX")"
      trap 'find "$scratch" -type d -exec chmod u+w {} +; find "$scratch" -depth -delete' EXIT
    fi
    run git clone --depth 1 https://github.com/react4j/react4j.github.io.git "$scratch/checkout"
    run tools/site.sh prepare-deploy "$scratch/checkout"
    run git -C "$scratch/checkout" add -A
    if "$dry_run" || ! git -C "$scratch/checkout" diff --cached --quiet; then
      run git -C "$scratch/checkout" commit -m "Publish React4j $version website"
      run git -C "$scratch/checkout" push origin HEAD:master
    fi
    ;;
esac
