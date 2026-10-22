---
title: Project Setup
---

React4j applications compile Java to JavaScript with J2CL. Published libraries target Java 17 and generated sources remain Java 17-compatible; run annotation processing on Java 21 or later. The processor shades its compiler dependencies.

Configure the runtime with Closure defines and run runtime tests through J2CL in a browser. JVM system-property configuration is not supported.

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

## Configure your IDE

Import the build, use Java 21+ for annotation processing, and align processor options and exports with the command-line build. See [IntelliJ IDEA setup](intellij.md). Repository contributors should use the commands in [CONTRIBUTING.md](https://github.com/react4j/react4j/blob/master/CONTRIBUTING.md).
