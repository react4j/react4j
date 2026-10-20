"""Link configuration inputs for the JVM asset bundler tests."""

load("@rules_closure//closure:defs.bzl", "ClosureJsBinaryInfo")

visibility("private")

def _link_config_impl(ctx):
    return [DefaultInfo(files = depset([ctx.attr.root[ClosureJsBinaryInfo].compiler_config]))]

link_config = rule(
    implementation = _link_config_impl,
    attrs = {"root": attr.label(mandatory = True, providers = [ClosureJsBinaryInfo])},
)
