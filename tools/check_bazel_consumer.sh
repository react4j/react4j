#!/usr/bin/env bash
set -euo pipefail
root="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
scratch="$(mktemp -d "${TMPDIR:-/tmp}/react4j-bazel-consumer.XXXXXX")"
workspace="$scratch/application"
mkdir "$workspace"
cleanup() {
  (cd "$workspace" && bazel --output_base="$scratch/bazel" shutdown >/dev/null 2>&1) || true
  find "$scratch" -type d -exec chmod u+w {} +
  find "$scratch" -depth -delete
}
trap cleanup EXIT
"$root/tools/export_bazel_config.sh" "$workspace"
cp "$root/.bazelrc" "$root/.bazelversion" "$workspace/"
mkdir -p "$workspace/src/main/java/isolated"
cp "$root/tools/release/consumer/src/main/java/isolated/Parent.java" "$root/tools/release/consumer/src/main/java/isolated/SampleView.java" "$root/tools/release/consumer/src/main/java/isolated/Consumer.java" "$workspace/src/main/java/isolated/"
module_path="${root//\\/\\\\}"
module_path="${module_path//\"/\\\"}"
cat > "$workspace/MODULE.bazel" <<MODULE
module(name = "react4j_distribution_consumer")
bazel_dep(name = "react4j")
local_path_override(module_name = "react4j", path = "$module_path")
include("//third_party/react4j:dependencies.MODULE.bazel")
MODULE
cat > "$workspace/BUILD.bazel" <<'BUILD'
load("@rules_java//java:defs.bzl", "java_test")
java_test(
    name = "consumer",
    srcs = ["src/main/java/isolated/Parent.java", "src/main/java/isolated/SampleView.java", "src/main/java/isolated/Consumer.java"],
    javacopts = ["-XDaddTypeAnnotationsToSymbol=true", "-Werror"],
    plugins = ["@react4j//:react4j_processor_plugin", "@arez//:arez_processor_plugin", "@sting//:sting_processor_plugin"],
    deps = ["@react4j//:core", "@react4j//:dom", "@sting//:core"],
    main_class = "isolated.Consumer",
    use_testrunner = False,
    jvm_flags = ["-Dreact4j.environment=development", "-Dbraincheck.environment=development"],
)
BUILD
cd "$workspace"
bazel --output_base="$scratch/bazel" test //:consumer --test_output=errors
bazel --output_base="$scratch/bazel" build @react4j//:core-j2cl @react4j//:dom-j2cl @react4j//doc-examples:hello_world
bazel --output_base="$scratch/bazel" build -c opt @react4j//doc-examples:hello_world
echo 'External Bazel consumer passed with exported root configuration and publication archives'
