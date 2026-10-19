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
asset_java="$workspace/src/main/java/react4j/css/example"
asset_files="$workspace/src/main/assets/react4j/css/example"
mkdir -p "$asset_java" "$asset_files/card" "$workspace/public"
cp "$root/css/examples/save-button/src/main/java/react4j/css/example/AssetsExample.java" "$root/css/examples/save-button/src/main/java/react4j/css/example/AssetsExample.entry.js" "$asset_java/"
for file in icon.svg background.svg unused.svg react4j.png; do
  cp "$root/css/examples/save-button/src/main/assets/react4j/css/example/card/$file" "$asset_files/card/"
done
cp "$asset_files/card/icon.svg" "$asset_files/card/prepared.svg"
cp "$root/examples/drumloop/src/main/assets/react4j/drumloop/views/Righteous-Regular.ttf" "$asset_files/"
cp "$root/css/examples/save-button/public/index.html" "$workspace/public/"
cat > "$asset_files/BUILD.bazel" <<'ASSETS'
load("@react4j//css:defs.bzl", "web_assets")
web_assets(
    name = "media",
    srcs = ["card/icon.svg", "card/background.svg", "card/unused.svg", "card/prepared.svg", "card/react4j.png", "Righteous-Regular.ttf"],
    visibility = ["//src/main/java/react4j/css/example:__pkg__"],
)
ASSETS
cat > "$asset_java/BUILD.bazel" <<'JAVA'
load("@rules_closure//closure:defs.bzl", "closure_js_library")
load("@react4j//tools/browser:java.bzl", "java_j2cl_library")
package(default_visibility = ["//:__pkg__"])
java_j2cl_library(
    name = "assets",
    srcs = ["AssetsExample.java"],
    assets = ["//src/main/assets/react4j/css/example:media"],
    java_deps = ["@react4j//css/core", "@react4j//:core", "@react4j//:dom"],
    plugins = ["@react4j//css/processor:plugin"],
    deps = ["@react4j//:core-j2cl", "@react4j//:dom-j2cl", "@react4j//css/core:core-j2cl", "@react4j//third_party/java:jsinterop_annotations-j2cl", "@react4j//third_party/java:jspecify-j2cl"],
)
closure_js_library(name = "entry", srcs = ["AssetsExample.entry.js"], deps = [":assets"])
JAVA
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
load("@react4j//css:defs.bzl", "css_bundle")
load("@react4j//tools/browser:rules.bzl", "browser_application")
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
css_bundle(name = "styles", root = ":application_bundle", css = "application.css", source_map = "application.css.map")
browser_application(
    name = "application",
    entry_point = "react4j.css.example.AssetsEntry",
    deps = ["//src/main/java/react4j/css/example:entry"],
    html = "public/index.html",
    styles = {":styles": "css/assets.css"},
)
filegroup(name = "application_manifest", srcs = [":application"], output_group = "manifest")
BUILD
cd "$workspace"
bazel --output_base="$scratch/bazel" test //:consumer --test_output=errors
bazel --output_base="$scratch/bazel" build //:application //:application_manifest @react4j//:core-j2cl @react4j//:dom-j2cl @react4j//doc-examples:hello_world
bazel --output_base="$scratch/bazel" build -c opt //:application //:application_manifest @react4j//doc-examples:hello_world
echo 'External Bazel consumer passed with exported root configuration and publication archives'
