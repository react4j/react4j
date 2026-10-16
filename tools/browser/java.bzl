"""Checked Java packages compiled for browser applications."""

load("@j2cl//build_defs:rules.bzl", "j2cl_library")
load("@rules_java//java/common:java_info.bzl", "JavaInfo")
load("//third_party/java:rules.bzl", "java_library")

def _checked_sources_impl(ctx):
    java_info = ctx.attr.java[JavaInfo]
    return [
        DefaultInfo(files = depset(java_info.source_jars)),
        # Building a source-only consumer must also run the JVM/static checks.
        OutputGroupInfo(_validation = depset(java_info.runtime_output_jars)),
    ]

_checked_sources = rule(
    implementation = _checked_sources_impl,
    attrs = {"java": attr.label(mandatory = True, providers = [JavaInfo])},
)

def java_j2cl_library(name, srcs, deps = [], java_deps = [], plugins = [], native_srcs = [], testonly = False):
    java_library(
        name = name + "_java",
        testonly = testonly,
        srcs = srcs,
        deps = java_deps,
        plugins = plugins,
    )
    _checked_sources(
        name = name + "_sources",
        testonly = testonly,
        java = ":" + name + "_java",
        visibility = ["//visibility:private"],
    )
    j2cl_library(
        name = name,
        testonly = testonly,
        srcs = [":" + name + "_sources"] + native_srcs,
        experimental_enable_jspecify_support_do_not_enable_without_jspecify_static_checking_or_you_might_cause_an_outage = True,
        deps = deps,
    )
