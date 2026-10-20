"""Declared Java actions for DOM sources and pinned React assets."""

visibility("private")

def _dom_source_impl(ctx):
    ctx.actions.run(
        executable = ctx.executable._tool,
        outputs = [ctx.outputs.out],
        arguments = [ctx.outputs.out.path],
        mnemonic = "DomSource",
    )
    return [DefaultInfo(files = depset([ctx.outputs.out]))]

dom_source = rule(
    implementation = _dom_source_impl,
    attrs = {
        "out": attr.output(mandatory = True),
        "_tool": attr.label(default = Label("//tools/generation:dom_generator"), executable = True, cfg = "exec"),
    },
)

def _react_asset_impl(ctx):
    ctx.actions.run(
        executable = ctx.executable._tool,
        inputs = [ctx.file.src],
        outputs = [ctx.outputs.out],
        arguments = [ctx.file.src.path, ctx.outputs.out.path, ctx.attr.mode],
        mnemonic = "ReactAsset",
    )
    return [DefaultInfo(files = depset([ctx.outputs.out]))]

react_asset = rule(
    implementation = _react_asset_impl,
    attrs = {
        "mode": attr.string(values = ["development", "production", "copy"], mandatory = True),
        "out": attr.output(mandatory = True),
        "src": attr.label(allow_single_file = True, mandatory = True),
        "_tool": attr.label(default = Label("//tools/generation:react_asset_patcher"), executable = True, cfg = "exec"),
    },
)
