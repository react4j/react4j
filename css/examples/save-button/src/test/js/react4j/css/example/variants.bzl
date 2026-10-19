"""Complete-link naming variants for the SaveButton browser regression suite."""

load("//css:defs.bzl", "css_bundle")
load("//tools/browser:rules.bzl", "browser_test")

def naming_variant(name, defs):
    """Declares a complete-link browser naming variant with its independent CSS bundles."""
    browser_test(
        name = name + "_browser_tests",
        srcs = ["browser_test.js"],
        data = [":" + name + "_" + style_name for style_name in ["styles", "alternate_styles", "component_styles"]],
        defs = defs + ["--define=react4j.css.example.variant='" + name + "-'"],
        entry_point = "react4j.css.example.BrowserTest",
        runtime_defines = {"react4j.css.example.variant": name + "-"},
        deps = [
            "//css/examples/save-button/src/main/java/react4j/css/example:browser",
            "@rules_closure//closure/testing/library:testcase",
        ],
    )
    for style_name, output, globals in [
        ("styles", "styles", ["react4j.css.example.BrowserExample$Theme", "react4j.css.example.BrowserExample$Defaults"]),
        ("alternate_styles", "alternate", ["react4j.css.example.BrowserExample$AlternateTheme", "react4j.css.example.BrowserExample$Defaults"]),
        ("component_styles", "component", []),
    ]:
        css_bundle(
            name = name + "_" + style_name,
            testonly = True,
            css = name + "-" + output + ".css",
            globals = globals,
            root = ":" + name + "_browser_tests_bin",
            shared = ["//css/examples/save-button/src/main/css:shared.css"] if globals else [],
            source_map = name + "-" + output + ".css.map",
        )
