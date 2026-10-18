"""Bazel rules for CSS-in-Java."""

load("@rules_closure//closure:defs.bzl", "ClosureJsBinaryInfo", "closure_js_binary")

_CssInfo = provider("Transitive J2CL output trees containing processor CSS resources.", fields = ["trees"])

def _collect_impl(target, ctx):
    transitive = []
    for attribute in ["deps", "exports"]:
        for dep in getattr(ctx.rule.attr, attribute, []):
            if _CssInfo in dep:
                transitive.append(dep[_CssInfo].trees)

    # J2CL declares its per-library JavaScript directory as a tree artifact.
    trees = [file for file in target[DefaultInfo].files.to_list() if file.is_directory and file.basename.endswith(".js")]
    return [_CssInfo(trees = depset(trees, transitive = transitive))]

_collect = aspect(implementation = _collect_impl, attr_aspects = ["deps", "exports"])

def _configuration_impl(ctx):
    trees = depset(transitive = [dep[_CssInfo].trees for dep in ctx.attr.deps])
    args = ctx.actions.args()
    args.use_param_file("@%s", use_always = True)
    args.set_param_file_format("multiline")
    args.add("--output", ctx.outputs.json)
    args.add("--link", ctx.label.repo_name + "//" + ctx.label.package + ":" + ctx.attr.binary_name)
    args.add("--mode", "optimized" if ctx.attr.compilation_level == "ADVANCED" else "retain")
    args.add_all(trees, before_each = "--input", expand_directories = False)
    ctx.actions.run(
        executable = ctx.executable._catalogue,
        inputs = trees,
        outputs = [ctx.outputs.json],
        arguments = [args],
        mnemonic = "CssCatalogue",
    )
    return [DefaultInfo(files = depset([ctx.outputs.json]))]

_configuration = rule(
    implementation = _configuration_impl,
    outputs = {"json": "%{name}.json"},
    attrs = {
        "binary_name": attr.string(mandatory = True),
        "compilation_level": attr.string(mandatory = True),
        "deps": attr.label_list(aspects = [_collect]),
        "_catalogue": attr.label(default = Label("//css/linker/src/main/java/react4j/css/linker:catalogue"), executable = True, cfg = "exec"),
    },
)

def css_js_binary(name, deps, compilation_level = "ADVANCED", **kwargs):
    """Complete Closure link producing evidence for independently rooted CSS bundles."""
    _configuration(
        name = name + "_css_catalogue",
        binary_name = name,
        compilation_level = compilation_level,
        deps = deps,
        testonly = kwargs.get("testonly", False),
    )
    closure_js_binary(
        name = name,
        deps = deps,
        compilation_level = compilation_level,
        compiler = Label("//css/linker/src/main/java/react4j/css/linker:worker"),
        compiler_config = name + "_css_catalogue.json",
        compiler_report = name + ".css-live.json",
        **kwargs
    )

def _bundle_impl(ctx):
    trees = ctx.attr.root[_CssInfo].trees
    evidence = []
    args = ctx.actions.args()
    args.use_param_file("@%s", use_always = True)
    args.set_param_file_format("multiline")
    args.add("--css", ctx.outputs.css)
    args.add("--map", ctx.outputs.source_map)
    args.add_all(trees, before_each = "--input", expand_directories = False)
    args.add_all(ctx.files.shared, before_each = "--shared")
    args.add_all(ctx.attr.globals, before_each = "--global")
    if ClosureJsBinaryInfo in ctx.attr.root:
        binary = ctx.attr.root[ClosureJsBinaryInfo]
        if not binary.compiler_config or not binary.compiler_report:
            fail("CSS bundles rooted in a Closure binary require css_js_binary so liveness evidence is produced; use a library root for an unlinked library")
        evidence = [binary.compiler_config, binary.compiler_report, binary.bin]
        args.add("--link-config", binary.compiler_config)
        args.add("--liveness", binary.compiler_report)
        args.add("--javascript", binary.bin)
        label = binary.bin.owner
        args.add("--link", label.repo_name + "//" + label.package + ":" + label.name)
    ctx.actions.run(
        executable = ctx.executable._bundler,
        inputs = depset(ctx.files.shared + evidence, transitive = [trees]),
        outputs = [ctx.outputs.css, ctx.outputs.source_map],
        arguments = [args],
        mnemonic = "CssBundle",
    )
    return [
        DefaultInfo(files = depset([ctx.outputs.css, ctx.outputs.source_map])),
        OutputGroupInfo(css = depset([ctx.outputs.css]), source_map = depset([ctx.outputs.source_map])),
    ]

css_bundle = rule(
    implementation = _bundle_impl,
    attrs = {
        "css": attr.output(mandatory = True),
        "globals": attr.string_list(doc = "Binary Java names of @GlobalCss roots, visited in order after their dependencies."),
        "root": attr.label(mandatory = True, aspects = [_collect]),
        "shared": attr.label_list(allow_files = [".css"]),
        "source_map": attr.output(mandatory = True),
        "_bundler": attr.label(default = Label("//css/bundler"), executable = True, cfg = "exec"),
    },
)
