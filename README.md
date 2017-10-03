# gwt-react-playground

[![Build Status](https://secure.travis-ci.org/realityforge/gwt-react-playground.png?branch=master)](http://travis-ci.org/realityforge/gwt-react-playground)
[<img src="https://img.shields.io/maven-central/v/org.realityforge.react/react.svg?label=latest%20release"/>](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22org.realityforge.react%22%20a%3A%22react%22)

This project is used to experiment and a develop a GWT interface to React. The goal is to be able to
seamlessly use the react component model from GWT and make use of the ecosystem of react development
support tooling such a React's Devtools. It would be nice to be able to use existing react component
libraries and toolkits from Java but this is not an explicit goal. The project aims to also develop
tooling that stops you from using the toolkit incorrectly with no performance cost in production builds.

## TODO

### High Priority

* Arez should override hook lifecycle methods not the one that the developer overwrite.
* Complete React-Arez extension
* Add invariant checks to make sure people are doing the right thing when interacting with react.
  * Make sure call setState in correct places setInitialState only in constructor.
  * Ensure setState not called after component destroyed
  * Ensure refs is only accessed after component mounted
* subclasses that extend base methods with generics multiple times can result in multiple overrides in generated class

### Must Haves

* Add multiple render methods that return different values. Enhance code generator to ensure that
  the developer only overloads a single variant. Variants include;
  - renderAsElement
  - renderAsString (React16?)
  - renderAsChildren (React16?)
  - renderAsList (React16?)
  - renderAsArray (React16?)
  - Perhaps we go down the same path as Elemental uses for unions?
* Figure out a way to define dom factories in java that are optimized away in production such that
   `DOM.h1().className('foo').tabIndex(3).children("Hello",DOM.span().className('red').children('World'))`
   compiles to `React.createElement('h1', {className: 'foo', tabIndex:3},["Hello",React.createElement('span',{className: 'red'},['World'])])`
   Maybe judicious use of `@ForceInline`? `.children` or `.build` closing the element. Perhaps these
   element factories can be built by looking at html spec and auto-generating? Probably get away from writing build
   at end by overloading methods
* In development mode copy the list of arez dependencies to "state" of the component. Extra checking in
  shouldComponentUpdate to make sure it does not re-run as result of this.

### Low Priorities

* Add support for componentDidCatch() (React16)
* Add support for portals (React16)
* Figure out a way to support `getInitialProps()` on components.

### Really Low Priorities

* Consider introducing an interface for "React" API and swap out with a provider as required.
* Consider enhancing `@ComponentId` so it can be overidden in subclasses and provide if to parent class.
  i.e. Non-enhanced class can use it similarly to `@ComponentName`
* Incorporate lifecycle documentation ala https://hackernoon.com/reactjs-component-lifecycle-methods-a-deep-dive-38275d9d13c0
  directly into javadocs or project docs.
