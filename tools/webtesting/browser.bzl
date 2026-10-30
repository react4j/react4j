"""Pinned browser runfiles and paths for Java and Closure tests."""

load("@rules_browsers//browsers:named_files_info.bzl", "NamedFilesInfo")

visibility(["//tools/webtesting"])

def _browser_files_impl(ctx):
    binaries = {}
    runfiles = []
    for dep in ctx.attr.deps:
        binaries.update(dep[NamedFilesInfo].value)
        runfiles.append(dep[DefaultInfo].default_runfiles)
    return [
        DefaultInfo(runfiles = ctx.runfiles().merge_all(runfiles)),
        NamedFilesInfo(value = binaries),
        platform_common.TemplateVariableInfo({name: file.short_path for name, file in binaries.items()}),
    ]

browser_files = rule(
    implementation = _browser_files_impl,
    attrs = {"deps": attr.label_list(mandatory = True, providers = [NamedFilesInfo, DefaultInfo])},
)
