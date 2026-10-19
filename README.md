<p align="center"><img src="/assets/icons/React4j.png" alt="React4j" width="120"></p>

# React4j

[<img src="https://img.shields.io/maven-central/v/org.realityforge.react4j/react4j-core.svg?label=latest%20release"/>](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22org.realityforge.react4j%22)
[![codecov](https://codecov.io/gh/react4j/react4j/branch/master/graph/badge.svg)](https://codecov.io/gh/react4j/react4j)
![J2CL compatible](https://img.shields.io/badge/J2CL-compatible-brightgreen.svg)

The goal of this project is to be able to seamlessly use the [react component model](https://reactjs.org/docs/react-component.html)
from Java compiled with J2CL and make use of the ecosystem of react development support tooling such a React's Devtools. It would be nice to
be able to use existing react component libraries and toolkits from Java but this is not an explicit goal.
The project also aims to also develop guards that stop you from using the toolkit incorrectly with no
performance cost in production builds.

React4j is under heavy development and sometimes the documentation does not keep up to date. However the goal of
the toolkit is to be easy to use and this includes clear and concise documentation. If something is unclear
please [report it as a bug](https://github.com/react4j/react4j/issues) because it *is* a bug. If a new user
has a bad time then we need to fix the problem.

For more information about React4j, please see the [Website](https://react4j.github.io/). For the source code
and project support please visit the [GitHub project](https://github.com/react4j/react4j).

# Build

Run `bazel build //tools/release:maven_artifacts` to build the core, DOM and processor archives. Use `tools/check.sh` for the shared local/CI gate and `tools/examples.sh serve` for a local J2CL example. Bazel downloads the pinned Java toolchains and browser dependencies.

Bazel applications use `@react4j//:core`, `@react4j//:dom` and `@react4j//:css` as J2CL dependencies, with `@react4j//:processor` and `@react4j//:css_processor_plugin` for annotation processing. See [Project Setup](docs/project_setup.md) for Java/JSpecify requirements and the [release guide](tools/release/README.md) for archive layout and dependency publication prerequisites.

# Contributing

React4j was released as open source so others could benefit from the project. We are thankful for any
contributions from the community. A [Code of Conduct](CODE_OF_CONDUCT.md) has been put in place and
[CONTRIBUTING.md](CONTRIBUTING.md) describes the pinned Bazel build, tests, formatting, examples and Java documentation site.

# License

React4j is licensed under [Apache License, Version 2.0](LICENSE).

# Credit

* [Stock Software](http://www.stocksoftware.com.au/) for providing significant support in building and maintaining
  React4j, particularly at it's inception.

* This toolkit began as an experiment using [gwt-react](https://github.com/GWTReact/gwt-react) to build
  a React/GWT hybrid application. Before too long we merged and forked the `gwt-react` projects and began to
  evolve this into a product that fit our needs better. Credit goes to Paul Stockley and other contributors
  to the `gwt-react` projects who we based most of our initial work on. Several files within the code-base remain
  as direct copies from the original `gwt-react` project.

* It should go without saying that this toolkit owes it existence to the wonderful [ReactJS](https://reactjs.org/)
  project.
