"""Explicit CSS inputs available during Java header and J2CL compilation."""

load("@j2cl//build_defs:rules.bzl", "j2cl_import")
load("@rules_java//java/common:java_info.bzl", "JavaInfo")

visibility(["//css"])

def _inputs_impl(ctx):
    if not ctx.files.srcs:
        fail("css_sources requires at least one explicit source")
    package_path = "/" + ctx.label.package + "/"
    roots = [marker for marker in ["/src/main/java/", "/src/test/java/", "/src/main/css/", "/src/test/css/"] if marker in package_path]
    if len(roots) != 1:
        fail("css_sources must be declared beside package sources under src/main/java, src/test/java, src/main/css or src/test/css")
    prefix = package_path.split(roots[0], 1)[1]
    args = ctx.actions.args()
    args.add(ctx.outputs.jar)
    for file in ctx.files.srcs:
        if file.is_directory or file.owner.package != ctx.label.package or file.owner.repo_name != ctx.label.repo_name:
            fail("css_sources requires individually declared files in its package: " + str(file))
        relative = ("/" + file.short_path).split("/" + ctx.label.package + "/", 1)[1]
        args.add(prefix + relative)
        args.add(file)
    ctx.actions.run(
        executable = ctx.executable._tool,
        inputs = ctx.files.srcs,
        outputs = [ctx.outputs.jar],
        arguments = [args],
        mnemonic = "CssInputs",
    )
    return [DefaultInfo(files = depset([ctx.outputs.jar])), JavaInfo(output_jar = ctx.outputs.jar, compile_jar = ctx.outputs.jar)]

_inputs = rule(
    implementation = _inputs_impl,
    outputs = {"jar": "%{name}.jar"},
    attrs = {
        "srcs": attr.label_list(allow_files = [".css"], mandatory = True),
        "_tool": attr.label(default = Label("//css/processor/src/main/java/react4j/css/processor:css_inputs"), executable = True, cfg = "exec"),
    },
)

def css_sources(name, srcs, visibility = None, testonly = False):
    """Provide name for J2CL dependencies and name_java for JVM dependencies."""
    _inputs(name = name + "_java", srcs = srcs, visibility = visibility, testonly = testonly)
    j2cl_import(name = name, jar = ":" + name + "_java", visibility = visibility, testonly = testonly)
