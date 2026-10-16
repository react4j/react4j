"""Declared extraction action for the CSS authoring proof."""

def _styles_impl(ctx):
    sources = ctx.actions.declare_file(ctx.label.name + ".srcjar")
    css = ctx.actions.declare_file(ctx.label.name + ".css")
    compressed = ctx.actions.declare_file(ctx.label.name + ".css.br")
    ctx.actions.run(
        executable = ctx.executable._compiler,
        tools = [ctx.attr._css_tool[DefaultInfo].files_to_run],
        inputs = ctx.files.srcs + ctx.files.shared,
        outputs = [sources, css, compressed],
        arguments = [ctx.executable._css_tool.path, sources.path, css.path] + [f.path for f in ctx.files.srcs + ctx.files.shared],
        env = {"BAZEL_BINDIR": ctx.bin_dir.path},
        mnemonic = "JavaCss",
    )
    return [DefaultInfo(files = depset([sources, css, compressed])), OutputGroupInfo(sources = depset([sources]), css = depset([css]), brotli = depset([compressed]))]

styles = rule(
    implementation = _styles_impl,
    attrs = {
        "shared": attr.label_list(allow_files = [".css"]),
        "srcs": attr.label_list(allow_files = [".java"], mandatory = True),
        "_compiler": attr.label(default = Label("//css:compiler"), executable = True, cfg = "exec"),
        "_css_tool": attr.label(default = Label("//css:tool"), executable = True, cfg = "exec"),
    },
)
