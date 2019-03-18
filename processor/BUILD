package(default_visibility = ["//visibility:private"])

java_binary(
    name = "react4j-processor",
    srcs = glob(["src/main/java/react4j/**/*.java"]),
    create_executable = 0,
    deps = [
        "@Xcom_google_auto_common//jar",
        "@Xcom_google_auto_service//jar",
        "@Xcom_google_guava//jar",
        "@Xcom_squareup_javapoet//jar",
        "@javax_annotation//jar",
    ],
)

load("@com_github_johnynek_bazel_jar_jar//:jar_jar.bzl", "jar_jar")

genrule(
    name = "jar_jar_rules",
    outs = ["rules.txt"],
    cmd = """
echo \"zap javax.**\" > $@
echo \"rule com.squareup.javapoet.** react4j.processor.vendor.javapoet.@1\" >> $@
echo \"rule com.google.** react4j.processor.vendor.google.@1\" >> $@
    """,
)

jar_jar(
    name = "processor",
    input_jar = ":react4j-processor_deploy.jar",
    rules = ":jar_jar_rules",
    visibility = ["//visibility:public"]
)
