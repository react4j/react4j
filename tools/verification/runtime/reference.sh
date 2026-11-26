#!/usr/bin/env bash
set -euo pipefail

# Materialize the reference and the same DOM test sources used by the candidate.
root="$(cd "$(dirname "${BASH_SOURCE[0]}")/../../.." && pwd)"
destination="${1:?Supply a new directory for the React 16.6 reference}"
mkdir "$destination"
git -C "$root" archive 18e896de70f9e7ebae0ac6be8e9f5506a1949997 -- . ':!etc/secrets' | tar -x -C "$destination"

tests=dom/src/test/java/react4j/dom
suite=dom/src/test/js/react4j/dom/browser_test.js
cp "$root/$tests"/{RuntimeBrowserTest.java,RuntimeParityBrowserTest.java,RuntimeParityBrowserTest.native.js,EventsBrowserTest.java,EventsBrowserTest.native.js} "$destination/$tests/"
cp "$root/$suite" "$destination/$suite"
cd "$destination"
sed -i '/^browser_test(/,/^)/s|deps = \["//dom/src/test/java/react4j/dom:runtime_coverage"\]|deps = ["//dom/src/test/java/react4j/dom:runtime_coverage", "//third_party:closure_testcase"]|' "$(dirname "$suite")/BUILD.bazel"
sed -i '/"RuntimeBrowserTest.java",/a\        "RuntimeParityBrowserTest.java",\n        "RuntimeParityBrowserTest.native.js",' "$tests/BUILD.bazel"
sed -i '/"\/\/third_party:jsinterop_annotations-j2cl",/a\        "//third_party:jsinterop_base-j2cl",' "$tests/BUILD.bazel"

# Expose the existing two-argument error callback to ADVANCED Closure test code.
externs=core/src/main/java/react4j/react.externs.js
sed -i '/React.Component.prototype.componentDidCatch = function() {};/c\/** @param {Error} error @param {Object} info @return {void} */\nReact.Component.prototype.componentDidCatch = function(error, info) {};' "$externs"
printf '\n/** @type {string} */\nObject.prototype.componentStack;\n' >> "$externs"

# The UMD selects its before-input plugin when the script first loads. Run this
# capability-dependent scenario in a fresh page with TextEvent absent at startup.
sed -i '/testBeforeInputFallback() {/d' "$suite"
cat > "$(dirname "$suite")/fallback_test.js" <<'JS'
goog.module('react4j.dom.BeforeInputFallback');
const Events = goog.require('react4j.dom.EventsBrowserTest');
const testSuite = goog.require('goog.testing.testSuite');
testSuite({testBeforeInputFallback() { Events.runBeforeInputFallback(); }});
JS
cat >> "$(dirname "$suite")/BUILD.bazel" <<'BZL'

browser_test(
    name = "fallback_tests",
    srcs = ["fallback_test.js"],
    entry_point = "react4j.dom.BeforeInputFallback",
    deps = ["//dom/src/test/java/react4j/dom:runtime_coverage"],
)
BZL
sed -i '/for (int i = 1; i <= 3; i++) {/i\                if (args[4].endsWith("/fallback_tests_runtime.js")) {\n                    output.write("delete window.TextEvent;\\n".getBytes(StandardCharsets.UTF_8));\n                }' tools/browser/src/main/java/react4j/browser/BrowserAssets.java

for mode in fastbuild opt; do
  bazel test -c "$mode" --test_env=CHROME_DEVEL_SANDBOX --test_output=errors //:browser_tests //dom/src/test/js/react4j/dom:fallback_tests
done
