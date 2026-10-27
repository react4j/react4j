---
title: Introduction
---

React4j is an opinionated abstraction on top of [ReactJS](https://reactjs.org) that allows you to use Java
to build a web application using the React component model. The [React component model](https://reactjs.org/docs/react-component.html)
defines components that are passed `props` by their container, manage their own `state` and can be composed
to make complex UIs.

React4j uses [J2CL](https://github.com/google/j2cl) to compile the Java code to Javascript.

The "Hello World" of React4j that just returns a header is as simple as:

{@file_content: file=react4j/examples/hello_world/HelloWorld.java "start_line= ReactDOM" "end_line=;"}

The Java entry point is exported with `@JsMethod` and invoked by a Closure entry module. Bazel builds the Java sources, annotation processor output and J2CL bundle together. See [Project Setup](project_setup.md) for the build and nullness requirements.

Components compose through `Renderable`: either a native `ReactNode` or a ready `Buildable`.
See [composing ready builders](input-step-enhancers.md#composing-ready-builders) to nest components
without explicit `build()` calls.

{@file_content: file=react4j/examples/hello_world/HelloWorld.java}
<iframe title="Hello World example" src="/examples/hello_world.html"></iframe>
