"""Build the pinned CSS parser with the CSS empty-fallback grammar repair."""

load("@rules_java//java/common:java_common.bzl", "java_common")
load("@rules_java//java/common:java_info.bzl", "JavaInfo")

visibility("private")

def _parser_impl(ctx):
    sources = ctx.actions.declare_file(ctx.label.name + ".srcjar")
    resources = ctx.actions.declare_file(ctx.label.name + ".resources.jar")
    ctx.actions.run(
        executable = ctx.executable._patch,
        inputs = [ctx.file._sources, ctx.file._binary],
        outputs = [sources, resources],
        arguments = [ctx.file._sources.path, sources.path, ctx.file._binary.path, resources.path],
        mnemonic = "CssParserSources",
    )
    upstream = ctx.attr._upstream[JavaInfo]
    own = upstream.runtime_output_jars
    dependencies = [
        JavaInfo(output_jar = jar, compile_jar = jar)
        for jar in upstream.transitive_runtime_jars.to_list()
        if jar not in own
    ]
    output = ctx.actions.declare_file(ctx.label.name + ".jar")
    info = java_common.compile(
        ctx,
        source_jars = [sources],
        runtime_deps = [JavaInfo(output_jar = resources, compile_jar = resources)],
        output = output,
        deps = dependencies,
        java_toolchain = ctx.toolchains["@bazel_tools//tools/jdk:toolchain_type"].java,
        # These are the pinned dependency's sources, not owned application code.
        javac_opts = ["--release", "17", "-XepDisableAllChecks", "-Xlint:none"],
        strict_deps = "OFF",
    )
    return [DefaultInfo(files = depset([output])), info]

css_parser = rule(
    implementation = _parser_impl,
    attrs = {
        "_binary": attr.label(default = Label("@ph_css//file"), allow_single_file = True),
        "_patch": attr.label(
            default = Label("//tools/generation/src/main/java/react4j/generation:css_parser_sources"),
            executable = True,
            cfg = "exec",
        ),
        "_sources": attr.label(default = Label("@ph_css__sources//file"), allow_single_file = True),
        "_upstream": attr.label(default = Label("//third_party/java:ph_css"), providers = [JavaInfo]),
    },
    fragments = ["java"],
    toolchains = ["@bazel_tools//tools/jdk:toolchain_type"],
)
