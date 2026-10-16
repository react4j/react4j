---
title: Related projects
---

React4j is a relatively new toolkit but there are some other resources and related projects.
This list will hopefully grow over time. Feel free to submit a pull request to add new projects
to this page.

* [arez](https://github.com/arez/arez) Arez is a simple, efficient and scalable state management library
  powered by a transparent functional reactive programming core. It is the state management library that
  has the best support in the react4j library.
  <span style="font-size: smaller">**[API Docs](https://arez.github.io/api) · [GitHub Project](https://github.com/arez/arez)**</span>

### Sample Projects

There is also a few sample projects that you can look at to see how different types of applications can be
built using React4j.

* [Drumloop](/drumloop/) is a drum machine example included in this repository under
  `examples/drumloop`. It uses React4j, Arez, Sting, Web Audio, and experimental Suspense with a Java
  audio cache. Run `tools/examples.sh serve drumloop development` or try the
  [production app](/drumloop/drumloop/) and [development app](/drumloop/drumloop_dev/).
* [react-sithtracker](https://github.com/react4j/react4j-flux-challenge) A solution for the problem presented
  in the [flux challenge](https://github.com/staltz/flux-challenge) using Arez and React4j.
* [TodoMVC](todomvc.md) includes three implementations under `examples/todomvc`: explicit
  render scheduling, Arez state management, and Arez with Sting dependency injection.
* [Heart Rate Monitor](/heart-rate-monitor/) uses Web Bluetooth, Arez and React4j to display
  readings from a Bluetooth heart rate sensor. It is included under `examples/heart-rate-monitor`.
  Run `tools/examples.sh serve heart-rate-monitor development`, or try the
  [development app](/heart-rate-monitor_dev/).
* [Web Speech Demo](/webspeechdemo/) is included under `examples/webspeechdemo`. It uses Arez
  and React4j to control browser speech synthesis, select voices, and highlight spoken words.
  Run `tools/examples.sh serve webspeechdemo development`, or try the
  [development app](/webspeechdemo_dev/).
