---
title: Project Setup
---

React4j applications compile Java to JavaScript with J2CL. Published libraries target Java 17 and generated sources remain Java 17-compatible; run annotation processing on Java 21 or later. The processor shades its compiler dependencies.

Owned nullness contracts use JSpecify `@NullMarked`, `@NullUnmarked`, `@NonNull` and `@Nullable`. Mark application packages and specify nullable values explicitly. Legacy nullness annotations are ignored. Nullable AUTODETECT inputs are optional; an explicitly required nullable input needs a builder call and accepts null. Optional non-null references need a non-null default or a tree-context source.

The Bazel build pins migrated Arez, Proton and Sting revisions. Maven consumption requires a compatible published Arez release containing that migration; existing older releases do not acquire the new contents merely by choosing their version number. The snapshot below is the local build default, not a claim that migrated artifacts have been published to Central. Replace it with the migrated React4j release you intend to use.

## Configure Maven

Run Maven on Java 21+. Add the DOM library (which includes the core runtime transitively), JSpecify, and the processor. This uses the same compiler plugin configuration as the Arez reference build:

```xml
<project>
  <modelVersion>4.0.0</modelVersion>
  <groupId>com.example</groupId>
  <artifactId>application</artifactId>
  <version>1.0-SNAPSHOT</version>
  <properties>
    <react4j.version>0.0.0-SNAPSHOT</react4j.version>
  </properties>
  <dependencies>
    <dependency>
      <groupId>org.realityforge.react4j</groupId>
      <artifactId>react4j-dom</artifactId>
      <version>${react4j.version}</version>
    </dependency>
    <dependency>
      <groupId>org.jspecify</groupId>
      <artifactId>jspecify</artifactId>
      <version>1.0.0</version>
    </dependency>
  </dependencies>
  <build>
    <plugins>
      <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-compiler-plugin</artifactId>
        <version>3.12.1</version>
        <configuration>
          <release>17</release>
          <parameters>true</parameters>
          <useIncrementalCompilation>false</useIncrementalCompilation>
          <compilerArgs>
            <arg>-XDaddTypeAnnotationsToSymbol=true</arg>
          </compilerArgs>
          <annotationProcessorPaths>
            <path>
              <groupId>org.realityforge.react4j</groupId>
              <artifactId>react4j-processor</artifactId>
              <version>${react4j.version}</version>
            </path>
          </annotationProcessorPaths>
        </configuration>
      </plugin>
    </plugins>
  </build>
</project>
```

`-XDaddTypeAnnotationsToSymbol=true` preserves type-use annotations from compiled dependencies for processor inspection. Add compatible Arez/Sting processors when using those integrations. Configure Error Prone and JSpecify-mode NullAway in the application; its checker selection can differ from the repository policy.

Generated source formatting is enabled by default. The JVM running the processor needs these exports, supplied through `.mvn/jvm.config` or `MAVEN_OPTS` when Maven runs javac in-process:

```text
--add-exports=jdk.compiler/com.sun.tools.javac.api=ALL-UNNAMED
--add-exports=jdk.compiler/com.sun.tools.javac.code=ALL-UNNAMED
--add-exports=jdk.compiler/com.sun.tools.javac.file=ALL-UNNAMED
--add-exports=jdk.compiler/com.sun.tools.javac.parser=ALL-UNNAMED
--add-exports=jdk.compiler/com.sun.tools.javac.tree=ALL-UNNAMED
--add-exports=jdk.compiler/com.sun.tools.javac.util=ALL-UNNAMED
```

For forked javac, pass them with `-J`, such as `-J--add-exports=jdk.compiler/com.sun.tools.javac.parser=ALL-UNNAMED`. Alternatively, disable React4j source formatting with the processor option `-Areact4j.format_generated_source=false`.

## Configure Bazel

Use these public targets with native `j2cl_library`:

| Bazel label | Purpose |
| --- | --- |
| `@react4j//:core` | Core browser library from publication sources |
| `@react4j//:dom` | DOM browser library, exporting core |
| `@react4j//:css` | CSS and asset annotations |
| `@react4j//:processor` | React4j annotation processor plugin |
| `@react4j//:css_processor_plugin` | CSS and asset annotation processor plugin |

Bazel applies overrides and compiler patches from the application's root module. From the React4j checkout at the revision you intend to use, run `tools/export_bazel_config.sh /absolute/path/to/application`. Commit the generated `third_party/react4j/` configuration and regenerate it when updating React4j. For a local sibling checkout, the application module can use:

```starlark
bazel_dep(name = "react4j")
local_path_override(module_name = "react4j", path = "../react4j")
include("//third_party/react4j:dependencies.MODULE.bazel")
```

For a published Git revision, replace the local override with a `git_override` pinned to that immutable revision and use configuration exported from the same revision. Merge duplicate dependency declarations or overrides with those already in the application. Apply the Java/toolchain options from React4j's `.bazelrc`; dependency repositories' rc files are not inherited.

Put the React4j processor in the native J2CL target's `plugins` and DOM or core in `deps`. Add the CSS processor and annotation dependency when authoring styles or assets. Add compatible Arez/Sting processors for generated integrations. Core and DOM export the framework dependencies used by generated code.

The pinned J2CL compiler emits Java 21 intermediate bytecode for browser compilation. J2CL runs javac with the application's `plugins` and `javacopts`; a separate Java library and React4j's internal checked-library helper are unnecessary. Choose Error Prone checks in the application. For JSpecify-mode NullAway, supply its plugin and these options:

```text
-XDaddTypeAnnotationsToSymbol=true
-Xep:NullAway:ERROR
-XepOpt:NullAway:OnlyNullMarked=true
-XepOpt:NullAway:JSpecifyMode=true
-XepOpt:NullAway:AssertsEnabled=true
```

`AssertsEnabled` lets NullAway use assertions in generated React4j/Arez code to refine nullness. Enable J2CL's `experimental_enable_jspecify_support_do_not_enable_without_jspecify_static_checking_or_you_might_cause_an_outage` only with JSpecify static checking. The [external consumer check](https://github.com/react4j/react4j/blob/master/tools/check_bazel_consumer.sh) demonstrates native J2CL with application-selected checks, both processors and asset dependencies.

Load `web_assets`, `css_js_binary`, `css_bundle` and `web_bundle` from `@react4j//css:defs.bzl` for [CSS and asset integration](https://github.com/react4j/react4j/blob/master/css/README.md). Asset catalogues are ordinary J2CL `deps`. CSS linking requests the existing J2CL javac action as validation, because JavaScript-only linking can otherwise skip configured checks.

`@react4j//tools/browser:rules.bzl` offers optional `browser_application`, `browser_application_variant` and `browser_test` conveniences. They select the development/optimized runtime configuration, assemble React/ReactDOM with finalized application files and expose the build-only manifest, or run tests in pinned Chromium. Applications can instead use the lower-level CSS rules with their own packaging and browser-test infrastructure. Compression and deployment/cache policy belong downstream.

Public support targets provide the linker worker, runtime assets, mode selection and Chromium dependencies for those macros; they are not additional library APIs. Examples, JVM imports, checker policy and implementation tools are repository-only. The external consumer check maintains and verifies the complete public-target inventory. If assembling a page directly, load the patched React/ReactDOM UMD assets separately from the Closure bundle and provide runtime Closure defines before a development bundle loads.

## Configure your IDE

Import the build, use Java 21+ for annotation processing, and align processor options and exports with the command-line build. See [IntelliJ IDEA setup](intellij.md). Repository contributors should use the commands in [CONTRIBUTING.md](https://github.com/react4j/react4j/blob/master/CONTRIBUTING.md).
