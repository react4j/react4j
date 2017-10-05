# react4j

[![Build Status](https://secure.travis-ci.org/realityforge/react4j.png?branch=master)](http://travis-ci.org/realityforge/react4j)
[<img src="https://img.shields.io/maven-central/v/org.realityforge.react4j/react4j.svg?label=latest%20release"/>](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22org.realityforge.react4j%22%20a%3A%22react4j%22)

The goal of this project is to be able to seamlessly use the [react component model](https://reactjs.org/docs/react-component.html) from GWT and make
use of the ecosystem of react development support tooling such a React's Devtools. It would be nice to
be able to use existing react component libraries and toolkits from Java but this is not an explicit goal.
The project also aims to also develop guards that stop you from using the toolkit incorrectly with no
performance cost in production builds.

## TODO

### High Priority

* Arez should override hook lifecycle methods not the one that the developer overwrite.

### Medium Priorities

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
* Somehow declare props as interfaces in components and have implementation generated?
* In base class have configuration that warns on re-renders that produced duplicate values

### Low Priorities

* build in https://github.com/maicki/why-did-you-update
* Add support for componentDidCatch() (React16)
* Add support for portals (React16)
* Figure out a way to support `getInitialProps()` on components.
  - set "defaultProps" property on constructor function (same way that we set name). All we need to do is detect
    if static property of correct name present on component and if so copy it across as part of annotation processing.
    https://reactjs.org/docs/react-component.html#defaultprops

# Credit

* This toolkit began as an experiment using [gwt-react](https://github.com/GWTReact/gwt-react) to build
  a React/GWT hybrid application. Before too long we merged in all the `gwt-react` projects in and started
  to co-evolve them into something that fit our needs better. Credit goes to Paul Stockley and other contributors
  to the `gwt-react` projects who we based most of our work on. Several files within the code-base remain as direct
  copies from the original `gwt-react` project.

* It should go without saying that this toolkit owes it existence to the wonderful [ReactJS](https://reactjs.org/)
  project.

* The website is derived from the [Edition Template](https://github.com/CloudCannon/edition-jekyll-template)
  by [Cloud Cannon](https://cloudcannon.com/) that is licensed under the [MIT License](https://github.com/CloudCannon/edition-jekyll-template/blob/master/LICENSE).
  They also have amazing [tutorial website](https://learn.cloudcannon.com/) for developing jekyll based websites.
