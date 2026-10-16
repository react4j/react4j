---
title: TodoMVC
---

![TodoMVC](/img/todomvc.svg)

[TodoMVC](https://todomvc.com/) gives each implementation the same task list and appearance,
so you can compare how different approaches handle the same application.

The three examples under `examples/todomvc` use the same page assets and browser tests:

| Variant | State and dependency management | Production | Development |
| --- | --- | --- | --- |
| Raw | Explicit subscriptions and render scheduling | [Run](/todomvc/raw/todomvc/) | [Run](/todomvc/raw/todomvc_dev/) |
| Arez | Reactive state, with services created explicitly | [Run](/todomvc/arez/todomvc/) | [Run](/todomvc/arez/todomvc_dev/) |
| Sting | Arez state with Sting dependency injection | [Run](/todomvc/sting/todomvc/) | [Run](/todomvc/sting/todomvc_dev/) |

Run one locally from the repository root:

```sh
tools/examples.sh serve todomvc-raw development
```

Use `todomvc-arez` or `todomvc-sting` to try the other implementations.
The examples keep tasks in memory; reloading the page clears them.
