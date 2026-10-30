"""JVM browser tests with declared Chrome and ChromeDriver inputs."""

load("//third_party/java:rules.bzl", "java_testng")

visibility(["//tools/browser/src/test/java/react4j/browser", "//tools/verification/src/test/java/react4j/verification"])

def java_browser_testng(name, test_class, size = "medium", jvm_flags = [], deps = [], data = [], visibility = None, **kwargs):
    java_testng(
        name = name,
        test_class = test_class,
        size = size,
        jvm_flags = [
            "-Dreact4j.browser.binary='$(CHROME-HEADLESS-SHELL)'",
            "-Dreact4j.browser.driver='$(CHROMEDRIVER)'",
        ] + jvm_flags,
        toolchains = ["//tools/webtesting:chromium_files"],
        data = ["//tools/webtesting:chromium_files"] + data,
        deps = deps,
        visibility = visibility,
        **kwargs
    )
