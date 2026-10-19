"""Complete-link naming variants for the SaveButton browser regression suite."""

load("//css:defs.bzl", "css_bundle")
load("//css/examples/save-button:assets.bzl", "asset_example")
load("//tools/browser:rules.bzl", "browser_test")

visibility("private")

def naming_variant(name, defs):
    """Declares complete-link CSS and packaged-asset naming variants.

    Args:
        name: Target prefix and browser variant name.
        defs: Closure naming definitions shared by each link.
    """
    asset_example(name = name + "_assets", defs = defs)
    browser_test(
        name = name + "_browser_tests",
        srcs = ["browser_test.js"],
        data = [":" + name + "_" + style_name for style_name in ["styles", "alternate_styles", "component_styles"]] + [":" + name + "_assets", ":" + name + "_assets_manifest"],
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
