#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
VERSION="0.26"
SHA256="217987b5d60162bf6afe78a838b64818abef511852e39b094824e0b01a9bbd8c"
URL="https://repo.maven.apache.org/maven2/org/realityforge/bazel/depgen/bazel-depgen/${VERSION}/bazel-depgen-${VERSION}-all.jar"
OUTPUT_BASE="$(cd "${ROOT}" && bazel info output_base)"
TOOLS_DIR="${OUTPUT_BASE}/.depgen-tools"
CACHE_DIR="${OUTPUT_BASE}/.depgen-cache"
JAR="${TOOLS_DIR}/bazel-depgen-${VERSION}-all.jar"

mkdir -p "${TOOLS_DIR}" "${CACHE_DIR}"

candidate="${JAR}"
if [[ ! -f "${candidate}" ]]; then
  candidate="$(mktemp "${JAR}.XXXXXX")"
  trap 'rm -f "${candidate}"' EXIT
  curl -fsSL -o "${candidate}" "${URL}"
fi

printf '%s  %s\n' "${SHA256}" "${candidate}" | shasum -a 256 --check
if [[ "${candidate}" != "${JAR}" ]]; then
  mv "${candidate}" "${JAR}"
fi

cd "${ROOT}"
java -jar "${JAR}" \
  --directory "${ROOT}" \
  --config-file third_party/java/dependencies.yml \
  --cache-directory "${CACHE_DIR}" \
  generate
bazel run //third_party/java:update_depgen_generated_outputs
bazel run //tools/buildifier:buildifier -- MODULE.bazel third_party/java/BUILD.bazel
