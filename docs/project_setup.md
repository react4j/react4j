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
      <version>0.154</version>
    </dependency>
    ...
  </dependencies>
</project>
```

If you plan to use [React Arez](arez.md) components you can instead add the `react4j-arez` dependency
which will transitively include the required dependencies. This can be done by instead adding the
following to your `pom.xml`:

```xml
<project>
  ...
  <dependencies>
    ...
    <dependency>
      <groupId>org.realityforge.react4j</groupId>
      <artifactId>react4j-arez</artifactId>
      <version>0.154</version>
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
              <version>0.154</version>
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
GWT module in your `.gwt.xml` file. For React4j applications that use Arez component then it issufficient to add:

```xml
<module>
  ...
  <inherits name='react4j.arez.Arez'/>
  ...
</module>
```

Otherwise you can simply rely on the dom library via:

```xml
<module>
  ...
  <inherits name='react4j.dom.Dom'/>
  ...
</module>
```

In addition you can *also* add the `Dev` module if you want the framework to perform validation
and limited invariant checking. The `Dev` module is very useful during development as it adds a
level of safety and error checking but it should not be used in production environments as it adds
some overhead. The `Dev` module decreases the execution speeds and significantly increases the code
size. In small and medium sized applications, the extra safety afforded by developing with the `Dev`
module always enabled and disabling the `Dev` module for production builds is usually worth the
decreased performance of development builds. The `Dev` module can be added via:

```xml
<module>
  ...
  <inherits name='react4j.ReactDev'/>
  ...
</module>
```

If you are using Arez components you can enable further invariant checking via:

```xml
<module>
  ...
  <inherits name='react4j.arez.ArezDev'/>
  ...
</module>
```
