"""Browser runfiles and executable paths for Java and Closure tests."""

load("@rules_browsers//browsers:named_files_info.bzl", "NamedFilesInfo")

visibility(["//", "//tools/webtesting"])

def _browser_files_impl(ctx):
    binaries = {}
    runfiles = []
    for dep in ctx.attr.deps:
        for name, binary in dep[NamedFilesInfo].value.items():
            if name in binaries and binaries[name] != binary:
                fail("Conflicting browser file '%s': %s and %s" % (name, binaries[name].path, binary.path))
            binaries[name] = binary
        runfiles.append(dep[DefaultInfo].default_runfiles)
    return [
        DefaultInfo(runfiles = ctx.runfiles().merge_all(runfiles)),
        NamedFilesInfo(value = binaries),
        platform_common.TemplateVariableInfo({name: file.short_path for name, file in binaries.items()}),
    ]

browser_files = rule(
    implementation = _browser_files_impl,
    doc = """Combines declared browser files, support runfiles and executable-path variables.

Dependencies provide rules_browsers' NamedFilesInfo and DefaultInfo. Repeated names
must identify the same file. Each name also becomes a Make variable whose value
uses Bazel rootpath semantics; resolve it with the runtime's runfiles library.
Browser downloads, versions and capabilities remain caller-owned.
""",
    attrs = {
        "deps": attr.label_list(
            allow_empty = False,
            mandatory = True,
            providers = [NamedFilesInfo, DefaultInfo],
            doc = "Browser file providers and their complete runtime files.",
        ),
    },
)
