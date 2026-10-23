"""Check that CSS output roots request transitive J2CL compiler validations."""

load("@rules_java//java/common:java_info.bzl", "JavaInfo")

visibility("private")

def _validation_test_impl(ctx):
    outputs = ctx.attr.root[OutputGroupInfo]
    validations = getattr(outputs, "_validation", depset()).to_list()
    missing = [jar.short_path for library in ctx.attr.libraries for jar in library[JavaInfo].runtime_output_jars if jar not in validations]
    return [AnalysisTestResultInfo(
        success = not missing,
        message = "Missing compiler validations: " + ", ".join(missing),
    )]

validation_test = rule(
    implementation = _validation_test_impl,
    attrs = {
        "libraries": attr.label_list(mandatory = True, providers = [JavaInfo]),
        "root": attr.label(mandatory = True),
    },
    test = True,
    analysis_test = True,
)
