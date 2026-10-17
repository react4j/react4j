"""Declared JVM action for static CSS extraction."""

load("@rules_java//java/common:java_info.bzl", "JavaInfo")

def _styles_impl(ctx):
    sources = ctx.actions.declare_file(ctx.label.name + ".srcjar")
    css = ctx.actions.declare_file(ctx.label.name + ".css")
    classpath = depset(transitive = [dep[JavaInfo].transitive_compile_time_jars for dep in [ctx.attr._annotations] + ctx.attr.deps])
    args = ctx.actions.args()
    args.use_param_file("@%s", use_always = True)
    args.set_param_file_format("multiline")
    args.add(sources)
    args.add(css)
    args.add_all(classpath, before_each = "--classpath")
    args.add_all(ctx.files.srcs, before_each = "--source")
    args.add_all(ctx.files.shared, before_each = "--shared")
    ctx.actions.run(
        executable = ctx.executable._compiler,
        inputs = depset(ctx.files.srcs + ctx.files.shared, transitive = [classpath]),
        outputs = [sources, css],
        arguments = [args],
        mnemonic = "JavaCss",
    )
    return [DefaultInfo(files = depset([sources, css])), OutputGroupInfo(sources = depset([sources]), css = depset([css]))]

styles = rule(
    implementation = _styles_impl,
    attrs = {
        "deps": attr.label_list(providers = [JavaInfo]),
        "shared": attr.label_list(allow_files = [".css"]),
        "srcs": attr.label_list(allow_files = [".java"], mandatory = True),
        "_annotations": attr.label(default = Label("//css/core"), providers = [JavaInfo]),
        "_compiler": attr.label(default = Label("//css/processor:compiler"), executable = True, cfg = "exec"),
    },
)
