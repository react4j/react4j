load("@j2cl//build_defs:rules.bzl", _j2cl_library = "j2cl_library")
load("@rules_java//java:defs.bzl", _java_binary = "java_binary", _java_library = "java_library", _java_plugin = "java_plugin", _java_test = "java_test")
load("//examples:checks.bzl", "BASE_JAVACOPTS", "JAVACOPTS")

visibility(["//..."])

FORMATTER_JVM_FLAGS = [
    "--add-exports=jdk.compiler/com.sun.tools.javac.api=ALL-UNNAMED",
    "--add-exports=jdk.compiler/com.sun.tools.javac.code=ALL-UNNAMED",
    "--add-exports=jdk.compiler/com.sun.tools.javac.file=ALL-UNNAMED",
    "--add-exports=jdk.compiler/com.sun.tools.javac.parser=ALL-UNNAMED",
    "--add-exports=jdk.compiler/com.sun.tools.javac.tree=ALL-UNNAMED",
    "--add-exports=jdk.compiler/com.sun.tools.javac.util=ALL-UNNAMED",
]

_JSPECIFY = Label("//third_party/java:jspecify")
_JSPECIFY_J2CL = Label("//third_party/java:jspecify-j2cl")
_NULLAWAY = Label("//third_party/java:nullaway_plugin")

def _compile_policy(srcs, javacopts, deps, plugins, j2cl = False):
    options = ([] if j2cl else ["--release", "17"]) + (JAVACOPTS if srcs else BASE_JAVACOPTS)
    if srcs:
        jspecify = _JSPECIFY_J2CL if j2cl else _JSPECIFY
        if jspecify not in [value if type(value) == "Label" else native.package_relative_label(value) for value in deps]:
            deps = [jspecify] + deps
        if _NULLAWAY not in [value if type(value) == "Label" else native.package_relative_label(value) for value in plugins]:
            plugins = [_NULLAWAY] + plugins
    return (options + javacopts, deps, plugins)

def j2cl_library(name, srcs = [], javacopts = [], deps = [], plugins = [], **kwargs):
    options, deps, plugins = _compile_policy(srcs, javacopts, deps, plugins, j2cl = True)
    _j2cl_library(
        name = name,
        srcs = srcs,
        javacopts = options,
        deps = deps,
        plugins = plugins,
        experimental_enable_jspecify_support_do_not_enable_without_jspecify_static_checking_or_you_might_cause_an_outage = True,
        **kwargs
    )

def java_library(name, srcs = [], javacopts = [], deps = [], plugins = [], **kwargs):
    options, deps, plugins = _compile_policy(srcs, javacopts, deps, plugins)
    _java_library(name = name, srcs = srcs, javacopts = options, deps = deps, plugins = plugins, **kwargs)

def java_plugin(name, srcs = [], javacopts = [], deps = [], plugins = [], **kwargs):
    options, deps, plugins = _compile_policy(srcs, javacopts, deps, plugins)
    _java_plugin(name = name, srcs = srcs, javacopts = options, deps = deps, plugins = plugins, **kwargs)

def java_binary(name, srcs = [], javacopts = [], deps = [], plugins = [], **kwargs):
    options, deps, plugins = _compile_policy(srcs, javacopts, deps, plugins)
    _java_binary(name = name, srcs = srcs, javacopts = options, deps = deps, plugins = plugins, exclude_build_data = True, **kwargs)

def java_test(name, srcs = [], javacopts = [], deps = [], plugins = [], jvm_flags = [], **kwargs):
    options, deps, plugins = _compile_policy(srcs, javacopts, deps, plugins)
    _java_test(name = name, srcs = srcs, javacopts = options, deps = deps, plugins = plugins, exclude_build_data = True, jvm_flags = ["-ea"] + jvm_flags, **kwargs)

def java_testng(name, srcs, test_classes = [], test_class = None, test_methods = [], deps = [], runtime_deps = [], jvm_flags = [], javacopts = [], **kwargs):
    if test_class and test_classes:
        fail("Specify only one of test_class or test_classes")
    if test_class:
        test_classes = [test_class]
    if not test_classes:
        fail("Specify test_class or test_classes")
    java_test(
        name = name,
        srcs = srcs,
        use_testrunner = False,
        main_class = "org.testng.TestNG",
        args = ["-testclass", ",".join(test_classes), "-verbose", "2"] + (["-methods", ",".join(test_methods)] if test_methods else []),
        deps = [Label("//third_party/java:testng")] + deps if srcs else [],
        runtime_deps = runtime_deps + ([Label("//third_party/java:testng")] + deps if not srcs else []),
        javacopts = javacopts,
        jvm_flags = jvm_flags,
        **kwargs
    )

def _checker_options_impl(ctx):
    output = ctx.actions.declare_file(ctx.label.name + ".txt")
    ctx.actions.write(output, "\n".join(["--release", "17"] + JAVACOPTS) + "\n")
    return [DefaultInfo(files = depset([output]))]

strict_checker_options = rule(implementation = _checker_options_impl)

# Depgen emits its configuration export without an explicit visibility.
def repository_files(srcs):
    native.exports_files(srcs, visibility = ["//:__subpackages__"])
