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
public_targets='
//:core
//:css
//:css_processor_plugin
//:dom
//:processor
//css:defs.bzl
//css/linker/src/main/java/react4j/css/linker:worker
//tools/browser:mode
//tools/browser:production
//tools/browser:rules.bzl
//tools/generation:react_dev
//tools/generation:react_dom_dev
//tools/generation:react_dom_prod
//tools/generation:react_prod
//tools/webtesting:chromium
'
# Include exported sources and generated outputs; generated files belong to their generating API target.
(cd "$root" && bazel query 'visible(@rules_java//java:defs.bzl, //...:all-targets)' --output=xml) > "$scratch/public.xml"
sed -nE \
  -e 's/^[[:space:]]*<generated-file generating-rule="([^"]+)".*/\1/p' \
  -e 's/^[[:space:]]*<(rule|source-file) .* name="([^"]+)".*/\2/p' \
  "$scratch/public.xml" | LC_ALL=C sort -u > "$scratch/public.actual"
printf '%s\n' "$public_targets" | sed '/^$/d' | LC_ALL=C sort > "$scratch/public.expected"
diff -u "$scratch/public.expected" "$scratch/public.actual"
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
    plugins = ["@react4j//:css_processor_plugin", "@sting//third_party/java:nullaway_plugin"],
    javacopts = ["-XDaddTypeAnnotationsToSymbol=true", "-Xep:ArrayEquals:ERROR", "-Xep:NullAway:ERROR", "-XepOpt:NullAway:OnlyNullMarked=true", "-XepOpt:NullAway:JSpecifyMode=true", "-XepOpt:NullAway:AssertsEnabled=true"],
    deps = ["//src/main/assets/react4j/css/example:media", "@react4j//:dom", "@react4j//:css"],
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
cat > "$workspace/src/main/java/isolated/BrowserTest.java" <<'JAVA'
package isolated;

import jsinterop.annotations.JsMethod;
import org.jspecify.annotations.NullMarked;
import react4j.Keyed;
import react4j.dom.DOM;

@NullMarked
public final class BrowserTest {
    private BrowserTest() {}

    @JsMethod
    public static void verify() {
        Keyed keyed = () -> "consumer-key";
        if (!"consumer-key".equals(Keyed.getKey(keyed)) || null == DOM.div("consumer")) {
            throw new AssertionError("Native J2CL consumer runtime");
        }
    }
}
JAVA
mkdir -p "$workspace/src/test/js/isolated"
cat > "$workspace/src/test/js/isolated/BUILD.bazel" <<'BUILD'
load("@react4j//tools/browser:rules.bzl", "browser_test")
browser_test(name = "browser", srcs = ["browser_test.js"], deps = ["//src/main/java/isolated:views"], entry_point = "isolated.BrowserTestSuite")
BUILD
cat > "$workspace/src/test/js/isolated/browser_test.js" <<'JS'
goog.module('isolated.BrowserTestSuite');
const Test = goog.require('isolated.BrowserTest');
const testSuite = goog.require('goog.testing.testSuite');
testSuite({ testNativeConsumer() { Test.verify(); } });
JS
cat > "$workspace/src/main/java/isolated/BUILD.bazel" <<'JAVA'
load("@j2cl//build_defs:rules.bzl", "j2cl_library")
j2cl_library(
    name = "views",
    srcs = ["Parent.java", "SampleView.java", "BrowserTest.java"],
    experimental_enable_jspecify_support_do_not_enable_without_jspecify_static_checking_or_you_might_cause_an_outage = True,
    plugins = ["@react4j//:processor", "@arez//:arez_processor_plugin", "@sting//third_party/java:nullaway_plugin"],
    javacopts = ["-XDaddTypeAnnotationsToSymbol=true", "-Xep:NullAway:ERROR", "-XepOpt:NullAway:OnlyNullMarked=true", "-XepOpt:NullAway:JSpecifyMode=true", "-XepOpt:NullAway:AssertsEnabled=true"],
    deps = ["@react4j//:dom"],
    visibility = ["//src/test/js/isolated:__pkg__"],
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
expect_failure() {
  local diagnostic="$1" log="$scratch/failure.log"
  shift
  if bazel --output_base="$scratch/bazel" "$@" >"$log" 2>&1; then
    cat "$log" >&2
    echo "Expected $diagnostic from bazel $*" >&2
    return 1
  fi
  if ! grep -Fq "$diagnostic" "$log"; then
    cat "$log" >&2
    return 1
  fi
}

cd "$workspace"
bazel --output_base="$scratch/bazel" build //src/main/java/isolated:views //:application //:application_manifest @react4j//:core @react4j//:dom
bazel --output_base="$scratch/bazel" build -c opt //:application //:application_manifest
for mode in fastbuild opt; do
  bazel --output_base="$scratch/bazel" test -c "$mode" //src/test/js/isolated:browser --test_output=errors
  expect_failure '[ArrayEquals]' build -c "$mode" //:bad_array.js
  expect_failure '[NullAway]' build -c "$mode" //:bad_null.js
done
mkdir "$workspace/denied"
for target in \
  '@react4j//:_core_java' \
  '@react4j//:lib_core_j2cl.jar' \
  '@react4j//core:core' \
  '@react4j//core/src/main/java/react4j:ReactConfig.native.js' \
  '@react4j//css/core:core' \
  '@react4j//css/core:core-j2cl' \
  '@react4j//css/processor:plugin' \
  '@react4j//doc-examples:j2cl' \
  '@react4j//third_party/java:jspecify-j2cl' \
  '@react4j//tools/browser:preview'; do
  printf 'filegroup(name = "target", testonly = True, srcs = ["%s"])\n' "$target" > "$workspace/denied/BUILD.bazel"
  expect_failure 'not visible' build --nobuild //denied:target
done
for target in core-j2cl dom-j2cl react4j_processor_plugin; do
  printf 'filegroup(name = "target", srcs = ["@react4j//:%s"])\n' "$target" > "$workspace/denied/BUILD.bazel"
  expect_failure 'no such target' build --nobuild //denied:target
done
for entry in \
  'tools/browser:java.bzl java_j2cl_library' \
  'third_party/java:rules.bzl java_library' \
  'tools/generation:generation.bzl dom_source' \
  'tools/release:release.bzl release_jar' \
  'tools/site:site.bzl react4j_site' \
  'tools/verification:defs.bzl j2cl_modules' \
  'tools/webtesting:defs.bzl java_browser_testng' \
  'tools/web-assets:defs.bzl prepare_file'; do
  read -r label symbol <<< "$entry"
  printf 'load("@react4j//%s", "%s")\nfilegroup(name = "target")\n' "$label" "$symbol" > "$workspace/denied/BUILD.bazel"
  expect_failure 'not visible for loading' build --nobuild //denied:target
done
echo 'External Bazel consumer passed with native J2CL, caller-owned checks, assets and enforced public boundaries'
