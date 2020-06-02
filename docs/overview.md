---
title: Introduction
---

React4j is an opinionated abstraction on top of [ReactJS](https://reactjs.org) that allows you to use Java
to build a web application using the React component model. The [React component model](https://reactjs.org/docs/react-component.html)
defines components that are passed `props` by their container, manage their own `state` and can be composed
to make complex UIs.

React4j uses [GWT](https://github.com/gwtproject/gwt) or [J2CL](https://github.com/google/j2cl) to compile the Java code to Javascript.

The "Hello World" of React4j that just returns a header is as simple as:

{@file_content: file=react4j/examples/hello_world/HelloWorld.java "start_line= ReactDOM" "end_line=;"}

Although there is significantly more ceremony in java. You also need an xml based build descriptor for GWT which
serves a similar purpose as a webpack configuration.

{@file_content: file=react4j/examples/hello_world/HelloWorld.java}
{@file_content: file=react4j/examples/hello_world/HelloWorld.gwt.xml language=xml}
<iframe src="/examples/hello_world.html"></iframe>
