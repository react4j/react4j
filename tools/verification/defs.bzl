"""Declared unoptimized J2CL module inputs for runtime coverage."""

def _j2cl_modules_impl(ctx):
    trees = [file for file in ctx.files.library if file.is_directory]
    if len(trees) != 1:
        fail("Runtime coverage requires one complete J2CL module tree")
    return [DefaultInfo(files = depset(trees), runfiles = ctx.runfiles(trees))]

j2cl_modules = rule(
    implementation = _j2cl_modules_impl,
    attrs = {"library": attr.label(mandatory = True)},
)

_JavaSourcesInfo = provider("Authored Java source files declared by a library.", fields = ["files"])

def _java_sources_impl(_target, ctx):
    return [_JavaSourcesInfo(files = [file for file in getattr(ctx.rule.files, "srcs", []) if file.is_source and file.extension == "java"])]

_java_sources = aspect(implementation = _java_sources_impl)

def _source_inventory_impl(ctx):
    files = depset([file for target in ctx.attr.libraries for file in target[_JavaSourcesInfo].files]).to_list()
    output = ctx.actions.declare_file(ctx.label.name + ".txt")
    ctx.actions.write(output, "\n".join(sorted([file.short_path for file in files])) + "\n")
    return [DefaultInfo(files = depset([output]), runfiles = ctx.runfiles([output]))]

source_inventory = rule(
    implementation = _source_inventory_impl,
    attrs = {"libraries": attr.label_list(mandatory = True, aspects = [_java_sources])},
)
