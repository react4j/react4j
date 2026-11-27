"""Declared Java actions for DOM sources."""

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
