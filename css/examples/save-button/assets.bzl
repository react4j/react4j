"""Complete application variants for the checked-asset example."""

load("//css:defs.bzl", "css_bundle")
load("//tools/browser:rules.bzl", "browser_application")

visibility(["//css/examples/save-button/src/test/js/react4j/css/example"])

def asset_example(name, defs = []):
    css_bundle(name = name + "_styles", root = ":" + name + "_bundle", css = name + ".css", source_map = name + ".css.map")
    browser_application(
        name = name,
        deps = [Label("//css/examples/save-button/src/main/java/react4j/css/example:assets_entry")],
        entry_point = "react4j.css.example.AssetsEntry",
        html = Label("//css/examples/save-button:public/index.html"),
        styles = {":" + name + "_styles": "css/assets.css"},
        assets = {
            Label("//css/examples/save-button:public/nested.html"): "nested/page.html",
            Label("//examples/drumloop:public/fonts/OFL.txt"): "fonts/OFL.txt",
        },
        defs = defs,
    )
    native.filegroup(name = name + "_manifest", srcs = [":" + name], output_group = "manifest")
