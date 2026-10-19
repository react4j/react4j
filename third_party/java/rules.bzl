load("@rules_java//java:defs.bzl", _java_binary = "java_binary", _java_library = "java_library", _java_plugin = "java_plugin", _java_test = "java_test")

# Java 17 products use the same strict checker policy as the pinned Sting module.
_JAVACOPTS = [
    "--release",
    "17",
    "-Werror",
    "-Xlint:all,-processing,-serial,-this-escape",
    "-Xmaxerrs",
    "10000",
    "-Xmaxwarns",
    "10000",
]
_ERROR_PRONE_JAVACOPTS = [
    "-Xep:AlmostJavadoc:ERROR",
    "-Xep:AlreadyChecked:ERROR",
    "-Xep:AmbiguousMethodReference:ERROR",
    "-Xep:AnnotateFormatMethod:ERROR",
    "-Xep:ArrayAsKeyOfSetOrMap:ERROR",
    "-Xep:AssertEqualsArgumentOrderChecker:ERROR",
    "-Xep:AssertThrowsMultipleStatements:ERROR",
    "-Xep:AttemptedNegativeZero:ERROR",
    "-Xep:BadComparable:ERROR",
    "-Xep:BadImport:ERROR",
    "-Xep:BadInstanceof:ERROR",
    "-Xep:BareDotMetacharacter:ERROR",
    "-Xep:BigDecimalEquals:ERROR",
    "-Xep:BigDecimalLiteralDouble:ERROR",
    "-Xep:BoxedPrimitiveConstructor:ERROR",
    "-Xep:ClassCanBeStatic:ERROR",
    "-Xep:ClassName:ERROR",
    "-Xep:DefaultLocale:ERROR",
    "-Xep:DeprecatedVariable:ERROR",
    "-Xep:DuplicateBranches:ERROR",
    "-Xep:EmptyBlockTag:ERROR",
    "-Xep:EmptyCatch:ERROR",
    "-Xep:EmptyIf:ERROR",
    "-Xep:EmptyTopLevelDeclaration:ERROR",
    "-Xep:EqualsBrokenForNull:ERROR",
    "-Xep:EqualsMissingNullable:ERROR",
    "-Xep:FieldCanBeLocal:ERROR",
    "-Xep:FieldCanBeStatic:ERROR",
    "-Xep:Finalize:ERROR",
    "-Xep:ForEachIterable:ERROR",
    "-Xep:InconsistentHashCode:ERROR",
    "-Xep:LongLiteralLowerCaseSuffix:ERROR",
    "-Xep:MissingBraces:ERROR",
    "-Xep:MissingDefault:ERROR",
    "-Xep:MissingRuntimeRetention:ERROR",
    "-Xep:MixedArrayDimensions:ERROR",
    "-Xep:MultiVariableDeclaration:ERROR",
    "-Xep:MultipleTopLevelClasses:ERROR",
    "-Xep:NonOverridingEquals:ERROR",
    "-Xep:NotJavadoc:ERROR",
    "-Xep:NullOptional:ERROR",
    "-Xep:NullablePrimitive:ERROR",
    "-Xep:NullablePrimitiveArray:ERROR",
    "-Xep:NullableTypeParameter:ERROR",
    "-Xep:NullableWildcard:ERROR",
    "-Xep:PackageLocation:ERROR",
    "-Xep:ParameterMissingNullable:ERROR",
    "-Xep:ParameterName:ERROR",
    "-Xep:PublicApiNamedStreamShouldReturnStream:ERROR",
    "-Xep:RedundantOverride:ERROR",
    "-Xep:RedundantThrows:ERROR",
    "-Xep:RemoveUnusedImports:ERROR",
    "-Xep:ReturnAtTheEndOfVoidFunction:ERROR",
    "-Xep:ReturnFromVoid:ERROR",
    "-Xep:ReturnMissingNullable:ERROR",
    "-Xep:ReturnsNullCollection:ERROR",
    "-Xep:SelfAlwaysReturnsThis:ERROR",
    "-Xep:SunApi:ERROR",
    "-Xep:SystemExitOutsideMain:ERROR",
    "-Xep:ToStringReturnsNull:ERROR",
    "-Xep:UnnecessarilyVisible:ERROR",
    "-Xep:UnnecessaryAnonymousClass:ERROR",
    "-Xep:UnnecessaryBoxedAssignment:ERROR",
    "-Xep:UnnecessaryMethodReference:ERROR",
    "-Xep:UnnecessaryOptionalGet:ERROR",
    "-Xep:UnsynchronizedOverridesSynchronized:ERROR",
    "-Xep:UnusedLabel:ERROR",
    "-Xep:UnusedTypeParameter:ERROR",
    "-Xep:UnusedVariable:ERROR",
    "-Xep:UseCorrectAssertInTests:ERROR",
    "-Xep:UsingJsr305CheckReturnValue:ERROR",
    "-Xep:VoidMissingNullable:ERROR",
    "-Xep:FieldCanBeFinal:ERROR",
    "-Xep:FieldMissingNullable:ERROR",
    "-Xep:PrivateConstructorForUtilityClass:ERROR",
    "-Xep:UnnecessaryDefaultInEnumSwitch:ERROR",
    "-Xep:UnnecessarilyFullyQualified:ERROR",
    "-Xep:Varifier:ERROR",
]

_NULLAWAY_JAVACOPTS = [
    "-XDaddTypeAnnotationsToSymbol=true",
    "-Xep:NullAway:ERROR",
    "-Xep:RequireExplicitNullMarking:ERROR",
    "-Xep:JSpecifyUnrecognizedAnnotationLocation:ERROR",
    "-XepOpt:NullAway:OnlyNullMarked=true",
    "-XepOpt:NullAway:JSpecifyMode=true",
    "-XepOpt:NullAway:AssertsEnabled=true",
]

FORMATTER_JVM_FLAGS = [
    "--add-exports=jdk.compiler/com.sun.tools.javac.api=ALL-UNNAMED",
    "--add-exports=jdk.compiler/com.sun.tools.javac.code=ALL-UNNAMED",
    "--add-exports=jdk.compiler/com.sun.tools.javac.file=ALL-UNNAMED",
    "--add-exports=jdk.compiler/com.sun.tools.javac.parser=ALL-UNNAMED",
    "--add-exports=jdk.compiler/com.sun.tools.javac.tree=ALL-UNNAMED",
    "--add-exports=jdk.compiler/com.sun.tools.javac.util=ALL-UNNAMED",
]

_JSPECIFY = Label("//third_party/java:jspecify")
_NULLAWAY = Label("//third_party/java:nullaway_plugin")

def _compile_policy(srcs, javacopts, deps, plugins):
    options = _JAVACOPTS + _ERROR_PRONE_JAVACOPTS
    if srcs:
        options += _NULLAWAY_JAVACOPTS
        if _JSPECIFY not in [value if type(value) == "Label" else native.package_relative_label(value) for value in deps]:
            deps = [_JSPECIFY] + deps
        if _NULLAWAY not in [value if type(value) == "Label" else native.package_relative_label(value) for value in plugins]:
            plugins = [_NULLAWAY] + plugins
    return (options + javacopts, deps, plugins)

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
    ctx.actions.write(output, "\n".join(_JAVACOPTS + _ERROR_PRONE_JAVACOPTS + _NULLAWAY_JAVACOPTS) + "\n")
    return [DefaultInfo(files = depset([output]))]

strict_checker_options = rule(implementation = _checker_options_impl)
