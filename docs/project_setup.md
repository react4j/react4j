---
title: Project Setup
---

React4j applications compile Java to JavaScript with J2CL. The runtime and generated classes target Java 17; run annotation processing on Java 21 or later. The processor shades its compiler dependencies.

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

`-XDaddTypeAnnotationsToSymbol=true` preserves type-use annotations from compiled dependencies for processor inspection. Add compatible Arez/Sting processors when using those integrations. Keep full Error Prone and JSpecify-mode NullAway checking enabled in the application; the repository's compiler policy and generated-code checks provide the reference configuration.

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

Public targets consume the assembled publication archives:

| Bazel label | Artifact |
| --- | --- |
| `@react4j//:core` | `react4j-core` binary/source jar |
| `@react4j//:dom` | `react4j-dom` binary/source jar |
| `@react4j//:processor` | Shaded `react4j-processor` jar |
| `@react4j//:react4j_processor_plugin` | Assembled annotation processor |
| `@react4j//:core-j2cl` | Core publication sources for J2CL |
| `@react4j//:dom-j2cl` | DOM publication sources for J2CL |

Bazel applies overrides and compiler patches from the application's root module. From the React4j checkout at the revision you intend to use, run `tools/export_bazel_config.sh /absolute/path/to/application`. Commit the generated `third_party/react4j/` configuration and regenerate it when updating React4j. For a local sibling checkout, the application module can use:

```starlark
bazel_dep(name = "react4j")
local_path_override(module_name = "react4j", path = "../react4j")
include("//third_party/react4j:dependencies.MODULE.bazel")
```

For a published Git revision, replace the local override with a `git_override` pinned to that immutable revision and use configuration exported from the same revision. Merge duplicate dependency declarations or overrides with those already in the application. Apply the Java/toolchain options from React4j's `.bazelrc`; dependency repositories' rc files are not inherited.

Use `@react4j//:react4j_processor_plugin` in Java targets' `plugins` and the public core/DOM targets in `deps`. Compile checked application source archives with J2CL and use the public J2CL dependencies. Load the patched React/ReactDOM UMD assets separately from the Closure bundle, as the runnable `doc-examples` build does. Development bundles require the runtime Closure define map before the bundle loads; optimized production folds the explicit definitions.

## Configure your IDE

Import the build, use Java 21+ for annotation processing, and align processor options and exports with the command-line build. See [IntelliJ IDEA setup](intellij.md). Repository contributors should use the commands in [CONTRIBUTING.md](https://github.com/react4j/react4j/blob/master/CONTRIBUTING.md).
