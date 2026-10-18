"""JVM browser tests with loopback access limited to test execution."""

load("//third_party/java:rules.bzl", "java_binary")

def _loopback_test_impl(ctx):
    runner = ctx.executable.runner
    launcher = ctx.actions.declare_file(ctx.label.name + ".sh")
    ctx.actions.write(
        launcher,
        "#!/usr/bin/env bash\nset -euo pipefail\nexec \"${TEST_SRCDIR}/%s/%s\" \"$@\"\n" % (ctx.workspace_name, runner.short_path),
        is_executable = True,
    )
    return [
        DefaultInfo(executable = launcher, runfiles = ctx.runfiles([runner]).merge(ctx.attr.runner[DefaultInfo].default_runfiles)),
        testing.ExecutionInfo({"requires-network": ""}),
    ]

_loopback_test = rule(
    implementation = _loopback_test_impl,
    test = True,
    attrs = {"runner": attr.label(mandatory = True, executable = True, cfg = "target")},
)

def java_browser_testng(name, test_class, size = "medium", jvm_flags = [], deps = [], visibility = None, **kwargs):
    """Run a TestNG browser test without granting its build actions network access."""
    java_binary(
        name = name + "_runner",
        testonly = True,
        main_class = "org.testng.TestNG",
        jvm_flags = ["-ea"] + jvm_flags,
        deps = ["//third_party/java:testng"] + deps,
        visibility = visibility,
        **kwargs
    )
    _loopback_test(name = name, size = size, runner = ":" + name + "_runner", args = ["-testclass", test_class, "-verbose", "2"], visibility = visibility)
