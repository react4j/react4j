load("@rules_java//java/common:java_info.bzl", "JavaInfo")

def _site_impl(ctx):
    out = ctx.actions.declare_directory(ctx.label.name)

    # Source jars include the processor output as well as the compiled examples.
    sources = [f for f in ctx.attr.examples[JavaInfo].transitive_source_jars.to_list() if f.short_path.startswith("doc-examples/")]
    args = ctx.actions.args()
    args.add("build")
    args.add("--output", out.path)
    args.add("--layout", ctx.file.layout)
    args.add("--home", ctx.file.home)
    args.add("--document", ctx.file.document)
    args.add("--navigation", ctx.file.navigation)
    args.add("--api", ctx.file.api)
    args.add_all(ctx.files.docs, before_each = "--doc")
    args.add_all(sources, before_each = "--source-jar")
    for target, destination in ctx.attr.assets.items():
        files = target[DefaultInfo].files.to_list()
        if len(files) != 1:
            fail("Each site asset must contain exactly one file: %s" % target.label)
        args.add("--asset", "%s=%s" % (files[0].path, destination))

    for file in ctx.files.browser_example:
        args.add("--asset", "%s=examples/hello_world/%s" % (file.path, file.basename))

    ctx.actions.run(
        executable = ctx.executable._builder,
        arguments = [args],
        inputs = sources + ctx.files.docs + ctx.files.assets + ctx.files.browser_example + [ctx.file.layout, ctx.file.home, ctx.file.document, ctx.file.navigation, ctx.file.api],
        outputs = [out],
        mnemonic = "React4jSite",
        progress_message = "Rendering and checking React4j documentation",
    )
    return [DefaultInfo(files = depset([out]))]

react4j_site = rule(
    implementation = _site_impl,
    attrs = {
        "api": attr.label(mandatory = True, allow_single_file = [".jar"]),
        "assets": attr.label_keyed_string_dict(allow_files = True),
        "browser_example": attr.label(mandatory = True),
        "docs": attr.label_list(allow_files = [".md"]),
        "document": attr.label(mandatory = True, allow_single_file = [".html"]),
        "examples": attr.label(mandatory = True, providers = [JavaInfo]),
        "home": attr.label(mandatory = True, allow_single_file = [".html"]),
        "layout": attr.label(mandatory = True, allow_single_file = [".html"]),
        "navigation": attr.label(mandatory = True, allow_single_file = True),
        "_builder": attr.label(default = Label("//tools/site:tool"), executable = True, cfg = "exec"),
    },
)
