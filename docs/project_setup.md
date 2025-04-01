---
title: Project Setup
---

A React4j project can be setup using any build system that supports configuration of annotation
processors. The authors prefer [Apache Buildr](https://buildr.apache.org) but this is a relatively
fringe build system so the project setup will be detailed using [Apache Maven](https://maven.apache.org)
as that tool is well known within the Java ecosystem. It is expected that most React4j applications are
developed using an IDE and this is the recommended approach.

## Configure Maven

Most React4j applications make use of the `react4j-dom` library for the DOM factories and this library
needs to be added to the Maven `pom.xml` as a `compile` scope dependency. It will transitively include
the `react4j-core` library. To add this library to your Maven project, simply add the following to your
`pom.xml`:

```xml
<project>
  ...
  <dependencies>
    ...
    <dependency>
      <groupId>org.realityforge.react4j</groupId>
      <artifactId>react4j-dom</artifactId>
      <version>0.197</version>
    </dependency>
    ...
  </dependencies>
</project>
```

To enable the annotation processor used by React4j, you need add the following
snippet to configure the maven compiler plugin from within the `pom.xml`:

```xml
<project>
  ...
  <plugins>
    ...
      <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-compiler-plugin</artifactId>
        <version>3.5.1</version>
        <configuration>
          <source>1.8</source>
          <target>1.8</target>
          <useIncrementalCompilation>false</useIncrementalCompilation>
          <annotationProcessorPaths>
            <path>
              <groupId>org.realityforge.react4j</groupId>
              <artifactId>react4j-processor</artifactId>
              <version>0.197</version>
            </path>
          </annotationProcessorPaths>
        </configuration>
      </plugin>
      ...
    </plugins>
  </build>
</project>
```

## Configure your IDE

It is expected that most React4j applications are developed from within an IDE. The configuration of the IDE is
can be done by importing the `pom.xml` into the IDE but further customizations will often need to be done by
the user. In particular, there is some additional steps required when using [IntelliJ IDEA](intellij.md).

## Configure a GWT Application

As React4j applications, are GWT applications, you will also need to inherit the appropriate
GWT module in your `.gwt.xml` file. The most common approach is inherit the dom module that
transitively inherits all the required modules via:

```xml
<module>
  ...
  <inherits name='react4j.dom.Dom'/>
  ...
</module>
```

During development, it is useful to *also* inherit the `Dev` module as it enables limited invariant
checking. The invariant checking does increase the code size and decrease execution speeds significantly
so should not be used when deploying the applications. The extra safety afforded by developing with the
`Dev` module always enabled is usually worth the decreased performance in development builds. The `Dev`
module can be added via:

```xml
<module>
  ...
  <inherits name='react4j.ReactDev'/>
  ...
</module>
```
