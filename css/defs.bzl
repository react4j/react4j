"""Bazel rules for CSS-in-Java."""

load("@rules_closure//closure:defs.bzl", "ClosureJsBinaryInfo", "closure_js_binary")

WebAssetsInfo = provider("Declared logical asset paths and their byte-independent catalogue.", fields = ["files", "manifest"])

# Read by collection aspects; source bytes are inputs only to asset processing/packaging.
def _web_assets_impl(ctx):
    package_path = "/" + ctx.label.package + "/"
    roots = [marker for marker in ["/src/main/assets/", "/src/test/assets/"] if marker in package_path]
    if len(roots) != 1:
        fail("web_assets must be declared below src/main/assets or src/test/assets")
    prefix = package_path.split(roots[0], 1)[1]
    files = {}
    manifest = {}
    for file in ctx.files.srcs:
        if file.is_directory:
            fail("web_assets requires individually declared files: " + str(file))
        local_prefix = ctx.label.package + "/"
        relative = file.short_path[len(local_prefix):] if file.short_path.startswith(local_prefix) else file.basename
        logical = prefix + relative
        if logical in files:
            fail("Duplicate declared logical asset path: " + logical)
        if any([part in ["", ".", ".."] for part in logical.split("/")]) or any([char in logical for char in ["\\", ":", "?", "#", "%"]]):
            fail("Invalid logical asset path: " + logical)
        files[logical] = file
        manifest[logical] = file.path
    ctx.actions.write(ctx.outputs.manifest, json.encode({path: manifest[path] for path in sorted(manifest)}))
    return [DefaultInfo(files = depset([ctx.outputs.manifest])), WebAssetsInfo(files = files, manifest = ctx.outputs.manifest)]

web_assets = rule(
    implementation = _web_assets_impl,
    outputs = {"manifest": "%{name}.assets.json"},
    attrs = {"srcs": attr.label_list(allow_files = True, mandatory = True)},
)

_CssInfo = provider("Transitive J2CL resources and byte-independent asset catalogues.", fields = ["trees", "asset_manifests", "asset_files"])

def _collect_impl(target, ctx):
    transitive = []
    manifests = []
    files = []
    own = []
    if WebAssetsInfo in target:
        own = [target[WebAssetsInfo].manifest]
        files = [depset(target[WebAssetsInfo].files.values())]
    for attribute in ["deps", "exports", "srcs", "assets"]:
        for dep in getattr(ctx.rule.attr, attribute, []):
            if _CssInfo in dep:
                transitive.append(dep[_CssInfo].trees)
                manifests.append(dep[_CssInfo].asset_manifests)
                files.append(dep[_CssInfo].asset_files)

    # J2CL declares its per-library JavaScript directory as a tree artifact.
    trees = [file for file in target[DefaultInfo].files.to_list() if file.is_directory and file.basename.endswith(".js")]
    return [_CssInfo(trees = depset(trees, transitive = transitive), asset_manifests = depset(own, transitive = manifests), asset_files = depset(transitive = files))]

_collect = aspect(implementation = _collect_impl, attr_aspects = ["deps", "exports", "srcs", "assets"])

def _configuration_impl(ctx):
    trees = depset(transitive = [dep[_CssInfo].trees for dep in ctx.attr.deps])
    args = ctx.actions.args()
    args.use_param_file("@%s", use_always = True)
    args.set_param_file_format("multiline")
    args.add("--output", ctx.outputs.json)
    args.add("--link", ctx.label.repo_name + "//" + ctx.label.package + ":" + ctx.attr.binary_name)
    args.add("--mode", "optimized" if ctx.attr.compilation_level == "ADVANCED" else "retain")
    args.add_all(trees, before_each = "--input", expand_directories = False)
    args.add_all(ctx.attr.defs, before_each = "--define")
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
        "defs": attr.string_list(),
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
        defs = kwargs.get("defs", []),
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
