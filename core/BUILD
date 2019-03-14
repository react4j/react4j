package(default_visibility = ["//visibility:public"])

load("@com_google_j2cl//build_defs:rules.bzl", "j2cl_application", "j2cl_import", "j2cl_library")

java_plugin(
    name = "arez_processor",
    processor_class = "arez.processor.ArezProcessor",
    deps = ["@org_realityforge_arez_processor//jar"],
)

java_library(
    name = "react4j-core",
    srcs = glob(["src/main/java/react4j/**/*.java"]),
    plugins = [
        "arez_processor",
    ],
    deps = [
        "@com_google_jsinterop_annotations//jar",
        "@org_realityforge_arez_core//jar",
        "@org_realityforge_braincheck//jar",
        "@org_realityforge_com_google_elemental2_core//jar",
        "@org_realityforge_com_google_elemental2_dom//jar",
        "@org_realityforge_com_google_elemental2_promise//jar",
        "@org_realityforge_com_google_jsinterop_base//jar",
        "@org_realityforge_javax_annotation//jar",
    ],
)

j2cl_import(
    name = "com_google_jsinterop_annotations-j2cl",
    jar = "@com_google_jsinterop_annotations//jar",
)

j2cl_import(
    name = "gwt-javaemul-internal-annotations-j2cl",
    jar = "@org_gwtproject_gwt//user:gwt-javaemul-internal-annotations",
)

j2cl_library(
    name = "react4j-core-j2cl",
    srcs = glob(["src/main/java/react4j/*.js"]) + glob(["src/main/java/react4j/**/*.java"]) + [
        "@com_google_jsinterop_annotations//jar",
        "@org_realityforge_arez_core//jar",
        "@org_realityforge_braincheck//jar",
        "@org_realityforge_com_google_jsinterop_base//jar",
        "@org_realityforge_com_google_elemental2_core//jar",
        "@org_realityforge_com_google_elemental2_dom//jar",
        "@org_realityforge_com_google_elemental2_promise//jar",
        "@org_realityforge_javax_annotation//jar",
    ],
    deps = [
        ":com_google_jsinterop_annotations-j2cl",
        ":gwt-javaemul-internal-annotations-j2cl",
    ],
    plugins = [
        "arez_processor",
    ],
)
