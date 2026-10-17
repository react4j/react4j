"""Browser mode defaults and example applications and Chromium tests."""

load("@rules_closure//closure:defs.bzl", "closure_js_binary", "closure_js_library")
load("@rules_closure//closure/testing:webdriver_test.bzl", "webdriver_test")

_PRODUCTION_DEFINES = {
    "arez.check_api_invariants": "false",
    "arez.check_expensive_invariants": "false",
    "arez.check_invariants": "false",
    "arez.collections_properties_unmodifiable": "false",
    "arez.enable_names": "false",
    "arez.enable_native_components": "false",
    "arez.enable_observer_error_handlers": "true",
    "arez.enable_property_introspection": "false",
    "arez.enable_references": "false",
    "arez.enable_registries": "false",
    "arez.enable_spies": "false",
    "arez.enable_task_interceptor": "true",
    "arez.enable_verify": "false",
    "arez.enable_zones": "false",
    "arez.enforce_transaction_type": "false",
    "arez.environment": "production",
    "arez.logger": "console",
    "arez.purge_tasks_when_runaway_detected": "true",
    "braincheck.check_api_invariants": "false",
    "braincheck.check_invariants": "false",
    "braincheck.environment": "production",
    "braincheck.verbose_error_messages": "false",
    "jre.checks.checkLevel": "NORMAL",
    "jre.classMetadata": "STRIPPED",
    "react4j.check_invariants": "false",
    "react4j.enable_view_names": "false",
    "react4j.environment": "production",
    "react4j.minimize_input_keys": "true",
    "react4j.store_debug_data_as_state": "false",
    "react4j.validate_input_values": "false",
    "zemeckis.enable_names": "false",
    "zemeckis.enable_uncaught_error_handlers": "false",
    "zemeckis.environment": "production",
    "zemeckis.log_worker_interactions": "false",
    "zemeckis.logger": "console",
    "zemeckis.purge_tasks_when_runaway_detected": "true",
    "zemeckis.use_message_channel_to_schedule_tasks": "true",
    "zemeckis.use_test_scheduler": "false",
    "zemeckis.use_worker_to_schedule_delayed_tasks": "true",
}
_DEVELOPMENT_DEFINES = {
    "arez.check_api_invariants": "true",
    "arez.check_expensive_invariants": "false",
    "arez.check_invariants": "false",
    "arez.collections_properties_unmodifiable": "true",
    "arez.enable_names": "true",
    "arez.enable_native_components": "false",
    "arez.enable_observer_error_handlers": "true",
    "arez.enable_property_introspection": "false",
    "arez.enable_references": "false",
    "arez.enable_registries": "false",
    "arez.enable_spies": "false",
    "arez.enable_task_interceptor": "true",
    "arez.enable_verify": "true",
    "arez.enable_zones": "false",
    "arez.enforce_transaction_type": "true",
    "arez.environment": "development",
    "arez.logger": "console",
    "arez.purge_tasks_when_runaway_detected": "true",
    "braincheck.check_api_invariants": "true",
    "braincheck.check_invariants": "true",
    "braincheck.environment": "development",
    "braincheck.verbose_error_messages": "true",
    "jre.checks.checkLevel": "NORMAL",
    "jre.classMetadata": "SIMPLE",
    "react4j.check_invariants": "true",
    "react4j.enable_view_names": "true",
    "react4j.environment": "development",
    "react4j.minimize_input_keys": "false",
    "react4j.store_debug_data_as_state": "true",
    "react4j.validate_input_values": "true",
    "zemeckis.enable_names": "true",
    "zemeckis.enable_uncaught_error_handlers": "true",
    "zemeckis.environment": "development",
    "zemeckis.log_worker_interactions": "false",
    "zemeckis.logger": "console",
    "zemeckis.purge_tasks_when_runaway_detected": "true",
    "zemeckis.use_message_channel_to_schedule_tasks": "true",
    "zemeckis.use_test_scheduler": "false",
    "zemeckis.use_worker_to_schedule_delayed_tasks": "true",
}

BrowserApplicationInfo = provider("Packaged browser files and their relative application paths.", fields = ["assets"])

_PRODUCTION = "//tools/browser:production"

def _defines(test = False):
    production = dict(_PRODUCTION_DEFINES)
    development = dict(_DEVELOPMENT_DEFINES)
    if test:
        production["react4j.examples.environment"] = "production"
        development["react4j.examples.environment"] = "development"
    return select({_PRODUCTION: production, "//conditions:default": development})

def _compiler_defines(test = False):
    # The jsunit harness needs goog.DEBUG while application flags retain their mode.
    production = ["--define=goog.DEBUG=" + ("true" if test else "false")]
    development = ["--define=goog.DEBUG=true"]
    if test:
        production.append("--define=react4j.examples.environment=production")
        development.append("--define=react4j.examples.environment=development")
    return select({
        _PRODUCTION: production + ["--define=%s=%s" % item for item in _PRODUCTION_DEFINES.items()],
        "//conditions:default": development + ["--define=%s=%s" % item for item in _DEVELOPMENT_DEFINES.items()],
    })

def _preview_impl(ctx):
    html = ctx.actions.declare_file(ctx.label.name + "/index.html")
    js = ctx.actions.declare_file(ctx.label.name + "/app.js")
    react = ctx.actions.declare_file(ctx.label.name + "/react.js")
    dom = ctx.actions.declare_file(ctx.label.name + "/react-dom.js")
    ctx.actions.expand_template(template = ctx.file.html, output = html, substitutions = {"@@DEFINES@@": json.encode(ctx.attr.defines)})
    for output, source in [(js, ctx.file.js), (react, ctx.file.react), (dom, ctx.file.dom)]:
        ctx.actions.run(
            executable = ctx.executable._assets,
            inputs = [source],
            outputs = [output],
            arguments = ["copy", source.path, output.path],
            mnemonic = "BrowserPreviewAsset",
        )
    assets = {html: "index.html", js: "app.js", react: "react.js", dom: "react-dom.js"}
    for target, destination in ctx.attr.assets.items():
        if destination.startswith("/") or ".." in destination.split("/") or destination in assets.values():
            fail("Invalid or duplicate browser asset destination: " + destination)
        sources = target[DefaultInfo].files.to_list()
        if len(sources) != 1:
            fail("Each browser asset must contain exactly one file: %s" % target.label)
        output = ctx.actions.declare_file(ctx.label.name + "/" + destination)
        ctx.actions.run(
            executable = ctx.executable._assets,
            inputs = sources,
            outputs = [output],
            arguments = ["copy", sources[0].path, output.path],
            mnemonic = "BrowserPreviewAsset",
        )
        assets[output] = destination
    files = assets.keys()
    return [DefaultInfo(files = depset(files), runfiles = ctx.runfiles(files)), BrowserApplicationInfo(assets = assets)]

_preview = rule(implementation = _preview_impl, attrs = {
    "assets": attr.label_keyed_string_dict(allow_files = True),
    "defines": attr.string_dict(),
    "dom": attr.label(allow_single_file = True, mandatory = True),
    "html": attr.label(allow_single_file = True, mandatory = True),
    "js": attr.label(allow_single_file = True, mandatory = True),
    "react": attr.label(allow_single_file = True, mandatory = True),
    "_assets": attr.label(default = Label("//tools/browser:assets"), executable = True, cfg = "exec"),
})

def _asset(name):
    return select({_PRODUCTION: "//tools/generation:" + name + "_prod", "//conditions:default": "//tools/generation:" + name + "_dev"})

def browser_application(name, entry_point, deps, html, assets = {}):
    closure_js_binary(
        name = name + "_bundle",
        deps = deps,
        entry_points = [entry_point],
        compilation_level = select({_PRODUCTION: "ADVANCED", "//conditions:default": "BUNDLE"}),
        defs = _compiler_defines(),
    )
    _preview(name = name, html = html, js = name + "_bundle.js", defines = _defines(), react = _asset("react"), dom = _asset("react_dom"), assets = assets)

def _mode_transition_impl(_settings, attr):
    return {"//command_line_option:compilation_mode": attr.mode}

_mode_transition = transition(
    implementation = _mode_transition_impl,
    inputs = [],
    outputs = ["//command_line_option:compilation_mode"],
)

def _variant_impl(ctx):
    application = ctx.attr.application[0]
    return [application[DefaultInfo], application[BrowserApplicationInfo]]

browser_application_variant = rule(
    implementation = _variant_impl,
    attrs = {
        "application": attr.label(mandatory = True, cfg = _mode_transition, providers = [BrowserApplicationInfo]),
        "mode": attr.string(mandatory = True, values = ["fastbuild", "opt"]),
        "_allowlist_function_transition": attr.label(default = "@bazel_tools//tools/allowlists/function_transition_allowlist"),
    },
)

def _runtime_impl(ctx):
    output = ctx.outputs.js

    # UMD assets are runtime scripts; concatenating after Closure compilation
    # preserves their published bytes and keeps their legacy JSDoc out of Closure.
    ctx.actions.run(
        executable = ctx.executable._assets,
        inputs = [ctx.file.react, ctx.file.dom, ctx.file.js],
        outputs = [output],
        arguments = ["bundle", ctx.file.react.path, ctx.file.dom.path, ctx.file.js.path, output.path],
        mnemonic = "BrowserRuntimeBundle",
    )
    return [DefaultInfo(files = depset([output]))]

_runtime = rule(implementation = _runtime_impl, outputs = {"js": "%{name}.js"}, attrs = {"dom": attr.label(allow_single_file = True), "js": attr.label(allow_single_file = True), "react": attr.label(allow_single_file = True), "_assets": attr.label(default = Label("//tools/browser:assets"), executable = True, cfg = "exec")})

def browser_test(name, srcs, deps, entry_point, data = []):
    closure_js_library(name = name + "_lib", testonly = True, srcs = srcs, deps = deps + ["//tools/browser:mode", "@rules_closure//closure/testing/library:asserts", "@rules_closure//closure/testing/library:testsuite"])
    closure_js_binary(
        name = name + "_bin",
        testonly = True,
        deps = [":" + name + "_lib"],
        entry_points = [entry_point],
        compilation_level = select({_PRODUCTION: "ADVANCED", "//conditions:default": "BUNDLE"}),
        defs = _compiler_defines(test = True),
    )
    _runtime(name = name + "_runtime", testonly = True, js = name + "_bin.js", react = _asset("react"), dom = _asset("react_dom"))
    webdriver_test(name = name, test_file_js = name + "_runtime.js", data = data, browsers = ["//tools/webtesting:chromium"], runtime_defines = _defines(test = True))
