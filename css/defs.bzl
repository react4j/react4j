"""Bazel rules for CSS-in-Java."""

load("@j2cl//build_defs/internal_do_not_use:provider.bzl", "J2clInfo")
load("@rules_closure//closure:defs.bzl", "CLOSURE_JS_TOOLCHAIN_ATTRS", "ClosureJsBinaryInfo", "closure_js_binary", "create_closure_js_library")
load("//tools/web-assets:defs.bzl", "ASSET_TOOL_ATTRIBUTES", "prepare_file")

WebAssetsInfo = provider("Declared logical asset paths and their byte-independent catalogue.", fields = ["files", "manifest", "prepared"])

# Read by collection aspects; source bytes are inputs only to asset processing/packaging.
def _web_assets_impl(ctx):
    package_path = "/" + ctx.label.package + "/"
    roots = [marker for marker in ["/src/main/assets/", "/src/test/assets/"] if marker in package_path]
    if len(roots) != 1:
        fail("web_assets must be declared below src/main/assets or src/test/assets")
    prefix = package_path.split(roots[0], 1)[1]
    files = {}
    manifest = {}
    prepared = []
    for file in ctx.files.srcs:
        if file.is_directory:
            fail("web_assets requires individually declared files: " + str(file))
        own_package = file.owner.package == ctx.label.package and file.owner.repo_name == ctx.label.repo_name
        marker = "/" + ctx.label.package + "/"
        relative = ("/" + file.short_path).split(marker, 1)[1] if own_package else file.basename
        logical = prefix + relative
        if logical in files:
            fail("Duplicate declared logical asset path: " + logical)
        if any([part in ["", ".", ".."] for part in logical.split("/")]) or any([char in logical for char in ["\\", ":", "?", "#", "%"]]):
            fail("Invalid logical asset path: " + logical)
        files[logical] = file
        manifest[logical] = file.path
        metadata = prepare_file(ctx, file, logical, ctx.label.name + "_prepared/" + relative)
        prepared.append(metadata)
    ctx.actions.write(ctx.outputs.manifest, json.encode({path: manifest[path] for path in sorted(manifest)}))
    return [DefaultInfo(files = depset([ctx.outputs.manifest])), WebAssetsInfo(files = files, manifest = ctx.outputs.manifest, prepared = prepared), create_closure_js_library(ctx)]

web_assets = rule(
    implementation = _web_assets_impl,
    outputs = {"manifest": "%{name}.assets.json"},
    attrs = dict(CLOSURE_JS_TOOLCHAIN_ATTRS, **dict(ASSET_TOOL_ATTRIBUTES, srcs = attr.label_list(allow_files = True, mandatory = True))),
)

_CssInfo = provider("Transitive J2CL resources and byte-independent asset catalogues.", fields = ["trees", "asset_manifests", "asset_files", "prepared"])

def _collect_impl(target, ctx):
    transitive = []
    manifests = []
    files = []
    own = []
    prepared = []
    if WebAssetsInfo in target:
        own = [target[WebAssetsInfo].manifest]
        files = [depset(target[WebAssetsInfo].files.values())]
        prepared = [depset(target[WebAssetsInfo].prepared)]
    for attribute in ["deps", "exports", "srcs"]:
        for dep in getattr(ctx.rule.attr, attribute, []):
            if _CssInfo in dep:
                transitive.append(dep[_CssInfo].trees)
                manifests.append(dep[_CssInfo].asset_manifests)
                files.append(dep[_CssInfo].asset_files)
                prepared.append(dep[_CssInfo].prepared)

    # J2CL declares its per-library JavaScript directory as a tree artifact.
    trees = [file for file in target[DefaultInfo].files.to_list() if file.is_directory and file.basename.endswith(".js")]

    # JS linking can otherwise skip J2CL's full javac action and its configured checks.
    validations = target[J2clInfo]._private_.java_info.runtime_output_jars if J2clInfo in target else []
    return [_CssInfo(trees = depset(trees, transitive = transitive), asset_manifests = depset(own, transitive = manifests), asset_files = depset(transitive = files), prepared = depset(transitive = prepared)), OutputGroupInfo(_validation = depset(validations))]

_collect = aspect(implementation = _collect_impl, attr_aspects = ["deps", "exports", "srcs"])

def _configuration_impl(ctx):
    trees = depset(transitive = [dep[_CssInfo].trees for dep in ctx.attr.deps])
    manifests = depset(transitive = [dep[_CssInfo].asset_manifests for dep in ctx.attr.deps])
    args = ctx.actions.args()
    args.use_param_file("@%s", use_always = True)
    args.set_param_file_format("multiline")
    args.add("--output", ctx.outputs.json)
    args.add("--link", ctx.label.repo_name + "//" + ctx.label.package + ":" + ctx.attr.binary_name)
    args.add("--mode", "optimized" if ctx.attr.compilation_level == "ADVANCED" else "retain")
    args.add_all(trees, before_each = "--input", expand_directories = False)
    args.add_all(ctx.attr.defs, before_each = "--define")
    args.add_all(manifests, before_each = "--asset-catalogue")
    ctx.actions.run(
        executable = ctx.executable._catalogue,
        inputs = depset(transitive = [trees, manifests]),
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

_CssBundleInfo = provider("Independently pruned CSS and map from a particular JavaScript link.", fields = ["css", "source_map", "root"])

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
        _CssBundleInfo(css = ctx.outputs.css, source_map = ctx.outputs.source_map, root = ctx.attr.root[ClosureJsBinaryInfo].bin.owner if ClosureJsBinaryInfo in ctx.attr.root else ctx.attr.root.label),
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

WebBundleInfo = provider("Finalized JavaScript, maps and retained assets with a build inventory.", fields = ["directory", "manifest", "javascript", "source_map", "styles"])

def _web_bundle_impl(ctx):
    binary = ctx.attr.root[ClosureJsBinaryInfo]
    if not binary.compiler_config or not binary.compiler_report:
        fail("web_bundle requires a css_js_binary root")
    resources = ctx.attr.root[_CssInfo]
    directory = ctx.actions.declare_directory(ctx.label.name)
    args = ctx.actions.args()
    args.use_param_file("@%s", use_always = True)
    args.set_param_file_format("multiline")
    args.add("--directory", directory.path)
    args.add("--manifest", ctx.outputs.manifest)
    args.add("--output-js", ctx.outputs.javascript)
    args.add("--output-map", ctx.outputs.source_map)
    args.add("--javascript", binary.bin)
    args.add("--map", binary.map)
    args.add("--link-config", binary.compiler_config)
    args.add("--liveness", binary.compiler_report)
    label = binary.bin.owner
    args.add("--link", label.repo_name + "//" + label.package + ":" + label.name)
    args.add_all(resources.trees, before_each = "--input", expand_directories = False)
    args.add_all(resources.asset_manifests, before_each = "--asset-catalogue")
    args.add_all(resources.prepared, before_each = "--prepared")
    styles = {}
    style_inputs = []
    for target, name in ctx.attr.styles.items():
        style = target[_CssBundleInfo]
        if style.root != binary.bin.owner:
            fail("Every CSS variant must share the web_bundle JavaScript root")
        if not name.endswith(".css") or name in styles.values():
            fail("CSS variant deployment names must be distinct .css paths")
        output = ctx.actions.declare_file(ctx.label.name + "_styles/" + name)
        source_map = ctx.actions.declare_file(ctx.label.name + "_styles/" + name + ".map")
        styles[output] = name
        styles[source_map] = name + ".map"
        style_inputs.extend([style.css, style.source_map])
        args.add("--style", json.encode({"input": style.css.path, "map": style.source_map.path, "name": name, "output": output.path, "outputMap": source_map.path}))
    ctx.actions.run(
        executable = ctx.executable._tool,
        inputs = depset([binary.bin, binary.map, binary.compiler_config, binary.compiler_report] + style_inputs, transitive = [resources.trees, resources.asset_manifests, resources.asset_files, resources.prepared]),
        outputs = [directory, ctx.outputs.manifest, ctx.outputs.javascript, ctx.outputs.source_map] + styles.keys(),
        arguments = [args],
        mnemonic = "WebBundle",
    )
    return [DefaultInfo(files = depset([directory])), WebBundleInfo(directory = directory, manifest = ctx.outputs.manifest, javascript = ctx.outputs.javascript, source_map = ctx.outputs.source_map, styles = styles), OutputGroupInfo(manifest = depset([ctx.outputs.manifest]), javascript = depset([ctx.outputs.javascript]), source_map = depset([ctx.outputs.source_map]))]

web_bundle = rule(
    implementation = _web_bundle_impl,
    outputs = {"javascript": "%{name}.js", "manifest": "%{name}.manifest.json", "source_map": "%{name}.js.map"},
    attrs = {
        "root": attr.label(mandatory = True, providers = [ClosureJsBinaryInfo], aspects = [_collect]),
        "styles": attr.label_keyed_string_dict(providers = [_CssBundleInfo], doc = "CSS variant to application-relative .css deployment path."),
        "_tool": attr.label(default = Label("//css/bundler/src/main/java/react4j/css/bundler:assets_tool"), executable = True, cfg = "exec"),
    },
)
