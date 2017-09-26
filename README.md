# gwt-react-playground

[![Build Status](https://secure.travis-ci.org/realityforge/gwt-react-playground.png?branch=master)](http://travis-ci.org/realityforge/gwt-react-playground)
[<img src="https://img.shields.io/maven-central/v/org.realityforge.react/react.svg?label=latest%20release"/>](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22org.realityforge.react%22%20a%3A%22react%22)

This project is used to experiment and a develop a GWT interface to React. The goal is to be able to
seamlessly use the react component model from GWT and make use of the ecosystem of react development
support tooling such a React's Devtools. It would be nice to be able to use existing react component
libraries and toolkits from Java but this is not an explicit goal. The project aims to also develop
tooling that stops you from using the toolkit incorrectly with no performance cost in production builds.

## TODO

* Develop React-Arez extension
* Consider using annotations and generator to generate components
* Add mechanism for exporting methods with useful displayNames in props
* Overload React methods so can accept arrays
* Add invariant checks to make sure people are doing the right thing when interacting with react.
  (i.e. Make sure call setState in correct places setInitialState only in constructor etc.)
* Figure out a way to bundle in react as part of the build with both the optimized and non-optimized variants.
* Also figure out a way that we can get it shipped as part of gwt resources (so it passes through other linker
  step ala AppCache compression etc)
