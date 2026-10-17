"""Bazel rules for CSS-in-Java."""

_CssInfo = provider("Transitive J2CL output trees containing processor CSS resources.", fields = ["trees"])

def _collect_impl(target, ctx):
    transitive = []
    for attribute in ["deps", "exports"]:
        for dep in getattr(ctx.rule.attr, attribute, []):
            if _CssInfo in dep:
                transitive.append(dep[_CssInfo].trees)

    # J2CL declares its per-library JavaScript directory as a tree artifact.
    trees = [file for file in target[DefaultInfo].files.to_list() if file.is_directory and file.basename.endswith(".js")]
    return [_CssInfo(trees = depset(trees, transitive = transitive))]

_collect = aspect(implementation = _collect_impl, attr_aspects = ["deps", "exports"])

def _bundle_impl(ctx):
    trees = ctx.attr.root[_CssInfo].trees
    args = ctx.actions.args()
    args.use_param_file("@%s", use_always = True)
    args.set_param_file_format("multiline")
    args.add("--css", ctx.outputs.css)
    args.add("--map", ctx.outputs.source_map)
    args.add_all(trees, before_each = "--input", expand_directories = False)
    args.add_all(ctx.files.shared, before_each = "--shared")
    args.add_all(ctx.attr.globals, before_each = "--global")
    ctx.actions.run(
        executable = ctx.executable._bundler,
        inputs = depset(ctx.files.shared, transitive = [trees]),
        outputs = [ctx.outputs.css, ctx.outputs.source_map],
        arguments = [args],
        mnemonic = "CssBundle",
    )
    return [
        DefaultInfo(files = depset([ctx.outputs.css, ctx.outputs.source_map])),
        OutputGroupInfo(css = depset([ctx.outputs.css]), source_map = depset([ctx.outputs.source_map])),
    ]

css_bundle = rule(
    implementation = _bundle_impl,
    attrs = {
        "css": attr.output(mandatory = True),
        "globals": attr.string_list(doc = "Binary Java names of @GlobalCss roots, visited in order after their dependencies."),
        "root": attr.label(mandatory = True, aspects = [_collect]),
        "shared": attr.label_list(allow_files = [".css"]),
        "source_map": attr.output(mandatory = True),
        "_bundler": attr.label(default = Label("//css/bundler"), executable = True, cfg = "exec"),
    },
)
