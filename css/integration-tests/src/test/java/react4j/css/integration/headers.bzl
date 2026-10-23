"""Expose actual Java compile jars for dependency-boundary regression tests."""

load("@rules_java//java/common:java_info.bzl", "JavaInfo")

visibility("private")

def _headers_impl(ctx):
    return [DefaultInfo(files = ctx.attr.dep[JavaInfo].compile_jars)]

headers = rule(
    implementation = _headers_impl,
    attrs = {"dep": attr.label(mandatory = True, providers = [JavaInfo])},
)
