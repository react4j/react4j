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
cp "$root/tools/release/consumer/src/main/java/isolated/Parent.java" "$root/tools/release/consumer/src/main/java/isolated/SampleView.java" "$workspace/src/main/java/isolated/"
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
load("@j2cl//build_defs:rules.bzl", "j2cl_library")
package(default_visibility = ["//:__pkg__"])
j2cl_library(
    name = "assets",
    srcs = ["AssetsExample.java"],
    experimental_enable_jspecify_support_do_not_enable_without_jspecify_static_checking_or_you_might_cause_an_outage = True,
    plugins = ["@react4j//css/processor:plugin", "@sting//third_party/java:nullaway_plugin"],
    javacopts = ["-XDaddTypeAnnotationsToSymbol=true", "-Xep:ArrayEquals:ERROR", "-Xep:NullAway:ERROR", "-XepOpt:NullAway:OnlyNullMarked=true", "-XepOpt:NullAway:JSpecifyMode=true", "-XepOpt:NullAway:AssertsEnabled=true"],
    deps = ["//src/main/assets/react4j/css/example:media", "@react4j//:core-j2cl", "@react4j//:dom-j2cl", "@react4j//css/core:core-j2cl", "@j2cl//:jsinterop-annotations-j2cl", "//:jspecify"],
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
load("@j2cl//build_defs:rules.bzl", "j2cl_import")
load("@react4j//css:defs.bzl", "css_bundle", "css_js_binary")
load("@react4j//tools/browser:rules.bzl", "browser_application")
j2cl_import(name = "jspecify", jar = "@maven//:org_jspecify_jspecify", visibility = ["//visibility:public"])
config_setting(name = "optimized", values = {"compilation_mode": "opt"})
css_js_binary(name = "bad_array", deps = ["//src/main/java/checks:array"], compilation_level = select({":optimized": "ADVANCED", "//conditions:default": "BUNDLE"}))
css_js_binary(name = "bad_null", deps = ["//src/main/java/checks:null"], compilation_level = select({":optimized": "ADVANCED", "//conditions:default": "BUNDLE"}))
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
cat > "$workspace/src/main/java/isolated/BUILD.bazel" <<'JAVA'
load("@j2cl//build_defs:rules.bzl", "j2cl_library")
j2cl_library(
    name = "views",
    srcs = ["Parent.java", "SampleView.java"],
    experimental_enable_jspecify_support_do_not_enable_without_jspecify_static_checking_or_you_might_cause_an_outage = True,
    plugins = ["@react4j//:react4j_processor_plugin", "@arez//:arez_processor_plugin", "@sting//third_party/java:nullaway_plugin"],
    javacopts = ["-XDaddTypeAnnotationsToSymbol=true", "-Xep:NullAway:ERROR", "-XepOpt:NullAway:OnlyNullMarked=true", "-XepOpt:NullAway:JSpecifyMode=true", "-XepOpt:NullAway:AssertsEnabled=true"],
    deps = ["@react4j//:core-j2cl", "@react4j//:dom-j2cl", "@arez//:core-j2cl", "//:jspecify"],
)
JAVA
mkdir -p "$workspace/src/main/java/checks"
cat > "$workspace/src/main/java/checks/BadArray.java" <<'JAVA'
package checks;

final class BadArray {
    boolean same(int[] first, int[] second) {
        return first.equals(second);
    }
}
JAVA
cat > "$workspace/src/main/java/checks/BadNull.java" <<'JAVA'
package checks;

import org.jspecify.annotations.NullMarked;

@NullMarked
final class BadNull {
    String value() {
        return null;
    }
}
JAVA
cat > "$workspace/src/main/java/checks/BUILD.bazel" <<'JAVA'
load("@j2cl//build_defs:rules.bzl", "j2cl_library")
package(default_visibility = ["//:__pkg__"])
j2cl_library(name = "array", srcs = ["BadArray.java"], javacopts = ["-Xep:ArrayEquals:ERROR"])
j2cl_library(name = "null", srcs = ["BadNull.java"], plugins = ["@sting//third_party/java:nullaway_plugin"], javacopts = ["-XDaddTypeAnnotationsToSymbol=true", "-Xep:NullAway:ERROR", "-XepOpt:NullAway:OnlyNullMarked=true", "-XepOpt:NullAway:JSpecifyMode=true", "-XepOpt:NullAway:AssertsEnabled=true"], experimental_enable_jspecify_support_do_not_enable_without_jspecify_static_checking_or_you_might_cause_an_outage = True, deps = ["//:jspecify"])
JAVA
expect_diagnostic() {
  local mode="$1" target="$2" diagnostic="$3" log="$scratch/checker.log"
  if bazel --output_base="$scratch/bazel" build -c "$mode" "$target" >"$log" 2>&1; then
    cat "$log" >&2
    echo "Expected $diagnostic when linking $target in $mode" >&2
    return 1
  fi
  if ! grep -Fq "$diagnostic" "$log"; then
    cat "$log" >&2
    return 1
  fi
}
cd "$workspace"
bazel --output_base="$scratch/bazel" build //src/main/java/isolated:views //:application //:application_manifest @react4j//:core-j2cl @react4j//:dom-j2cl @react4j//doc-examples:hello_world
bazel --output_base="$scratch/bazel" build -c opt //:application //:application_manifest @react4j//doc-examples:hello_world
for mode in fastbuild opt; do
  expect_diagnostic "$mode" //:bad_array.js '[ArrayEquals]'
  expect_diagnostic "$mode" //:bad_null.js '[NullAway]'
done
echo 'External Bazel consumer passed with native J2CL, caller-owned checks, assets and publication archives'
