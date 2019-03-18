workspace(name = "org_realityforge_react4j")

load("@bazel_tools//tools/build_defs/repo:http.bzl", "http_archive")

http_archive(
    name = "com_google_j2cl",
    strip_prefix = "j2cl-master",
    url = "https://github.com/google/j2cl/archive/master.zip",
)

load("@com_google_j2cl//build_defs:repository.bzl", "load_j2cl_repo_deps")

load_j2cl_repo_deps()

load("@com_google_j2cl//build_defs:rules.bzl", "setup_j2cl_workspace")

setup_j2cl_workspace()

maven_jar(
    name = "org_realityforge_javax_annotation",
    artifact = "org.realityforge.javax.annotation:javax.annotation:1.0.0",
    sha1 = "1ebb2908a65d23131748a11158f5984ac242800b",
)

maven_jar(
    name = "org_realityforge_com_google_jsinterop_base",
    artifact = "org.realityforge.com.google.jsinterop:base-j2cl:1.0.0-b2-e6d791f",
    sha1 = "5019e4e51236b5fb35f31d5ffde52679d7dd121f",
)

maven_jar(
    name = "org_realityforge_arez_core",
    artifact = "org.realityforge.arez:arez-core:0.132",
    sha1 = "6ee17e9fa56cfac5236c3a36b9e11bc0772825fc",
)

maven_jar(
    name = "org_realityforge_arez_processor",
    artifact = "org.realityforge.arez:arez-processor:0.132",
    sha1 = "8d81984baf6273a3f8cae8cc2701cb3807921dab",
)

maven_jar(
    name = "org_realityforge_com_google_elemental2_core",
    artifact = "org.realityforge.com.google.elemental2:elemental2-core:1.0.0-b19-fb227e3",
    sha1 = "04a4b4da516d9c616488199062b23acddad668cf",
)

maven_jar(
    name = "org_realityforge_com_google_elemental2_promise",
    artifact = "org.realityforge.com.google.elemental2:elemental2-promise:1.0.0-b19-fb227e3",
    sha1 = "dafe9a270c20aef0d02bcb7fa793f4a3458a395c",
)

maven_jar(
    name = "org_realityforge_com_google_elemental2_dom",
    artifact = "org.realityforge.com.google.elemental2:elemental2-dom:1.0.0-b19-fb227e3",
    sha1 = "8661df17b5645e64f5e9ac243a0da060d595932c",
)

maven_jar(
    name = "org_realityforge_braincheck",
    artifact = "org.realityforge.braincheck:braincheck:1.13.0",
    sha1 = "8e3e425e52766cd28d1563116be190ad18195c04",
)

maven_jar(
    name = "javax_annotation",
    artifact = "com.google.code.findbugs:jsr305:3.0.2",
)

maven_jar(
    name = "Xcom_google_auto_service",
    artifact = "com.google.auto.service:auto-service:1.0-rc3",
)

maven_jar(
    name = "Xcom_google_auto_common",
    artifact = "com.google.auto:auto-common:0.10",
)

maven_jar(
    name = "Xcom_squareup_javapoet",
    artifact = "com.squareup:javapoet:1.11.1",
)

maven_jar(
    name = "Xcom_google_guava",
    artifact = "com.google.guava:guava:25.0-jre",
)

load("@bazel_tools//tools/build_defs/repo:git.bzl", "git_repository")

git_repository(
    name = "com_github_johnynek_bazel_jar_jar",
    commit = "16e48f319048e090a2fe7fd39a794312d191fc6f",
    remote = "git://github.com/johnynek/bazel_jar_jar.git",
)

load("@com_github_johnynek_bazel_jar_jar//:jar_jar.bzl", "jar_jar_repositories")

jar_jar_repositories()
