"""Declared JVM extraction action for the CSS authoring proof."""

def _styles_impl(ctx):
    sources = ctx.actions.declare_file(ctx.label.name + ".srcjar")
    css = ctx.actions.declare_file(ctx.label.name + ".css")
    ctx.actions.run(
        executable = ctx.executable._compiler,
        inputs = ctx.files.srcs + ctx.files.shared,
        outputs = [sources, css],
        arguments = [sources.path, css.path] + [f.path for f in ctx.files.srcs + ctx.files.shared],
        mnemonic = "JavaCss",
    )
    return [DefaultInfo(files = depset([sources, css])), OutputGroupInfo(sources = depset([sources]), css = depset([css]))]

styles = rule(
    implementation = _styles_impl,
    attrs = {
        "shared": attr.label_list(allow_files = [".css"]),
        "srcs": attr.label_list(allow_files = [".java"], mandatory = True),
        "_compiler": attr.label(default = Label("//css:compiler"), executable = True, cfg = "exec"),
    },
)
