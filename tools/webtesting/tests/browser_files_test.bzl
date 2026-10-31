"""Analysis checks for the public browser_files contract."""

load("@bazel_skylib//lib:unittest.bzl", "analysistest", "asserts")
load("@rules_browsers//browsers:named_files_info.bzl", "NamedFilesInfo")
load("//:defs.bzl", "browser_files")

def _artifact_impl(ctx):
    return [
        DefaultInfo(runfiles = ctx.runfiles(files = [ctx.file.binary] + ctx.files.data)),
        NamedFilesInfo(value = {ctx.attr.key: ctx.file.binary}),
    ]

_artifact = rule(
    implementation = _artifact_impl,
    attrs = {
        "binary": attr.label(allow_single_file = True, mandatory = True),
        "data": attr.label_list(allow_files = True),
        "key": attr.string(mandatory = True),
    },
)

def _merged_test_impl(ctx):
    env = analysistest.begin(ctx)
    target = analysistest.target_under_test(env)
    expected = {"CHROME-HEADLESS-SHELL": ctx.file.browser, "CHROMEDRIVER": ctx.file.driver}
    asserts.equals(env, expected, target[NamedFilesInfo].value)
    asserts.equals(env, {key: file.short_path for key, file in expected.items()}, target[platform_common.TemplateVariableInfo].variables)
    files = target[DefaultInfo].default_runfiles.files.to_list()
    for file in [ctx.file.browser, ctx.file.driver, ctx.file.support]:
        asserts.true(env, file in files, "Missing runfile: %s" % file.path)
    return analysistest.end(env)

_merged_test = analysistest.make(
    _merged_test_impl,
    attrs = {
        "browser": attr.label(allow_single_file = True, mandatory = True),
        "driver": attr.label(allow_single_file = True, mandatory = True),
        "support": attr.label(allow_single_file = True, mandatory = True),
    },
)

def _conflict_test_impl(ctx):
    env = analysistest.begin(ctx)
    asserts.expect_failure(env, "Conflicting browser file 'CHROME-HEADLESS-SHELL'")
    return analysistest.end(env)

_conflict_test = analysistest.make(_conflict_test_impl, expect_failure = True)

def _empty_test_impl(ctx):
    env = analysistest.begin(ctx)
    asserts.expect_failure(env, "must be non empty")
    return analysistest.end(env)

_empty_test = analysistest.make(_empty_test_impl, expect_failure = True)

def browser_files_tests(name):
    _artifact(
        name = "browser",
        key = "CHROME-HEADLESS-SHELL",
        binary = "browser_files_test.bzl",
        data = ["//:LICENSE"],
        testonly = True,
        tags = ["manual"],
    )
    _artifact(
        name = "driver",
        key = "CHROMEDRIVER",
        binary = "BUILD.bazel",
        testonly = True,
        tags = ["manual"],
    )
    _artifact(
        name = "other_browser",
        key = "CHROME-HEADLESS-SHELL",
        binary = "BUILD.bazel",
        testonly = True,
        tags = ["manual"],
    )
    browser_files(
        name = "merged",
        deps = [":browser", ":driver"],
        testonly = True,
        tags = ["manual"],
    )
    browser_files(
        name = "repeated",
        deps = [":merged", ":browser"],
        testonly = True,
        tags = ["manual"],
    )
    browser_files(
        name = "conflict",
        deps = [":browser", ":other_browser"],
        testonly = True,
        tags = ["manual"],
    )
    browser_files(
        name = "empty",
        deps = [],
        testonly = True,
        tags = ["manual"],
    )
    _merged_test(
        name = name + "_merged",
        target_under_test = ":repeated",
        browser = "browser_files_test.bzl",
        driver = "BUILD.bazel",
        support = "//:LICENSE",
    )
    _conflict_test(name = name + "_conflict", target_under_test = ":conflict")
    _empty_test(name = name + "_empty", target_under_test = ":empty")
    native.test_suite(
        name = name,
        tests = [name + "_" + suffix for suffix in ["merged", "conflict", "empty"]],
        visibility = ["//:__pkg__"],
    )
