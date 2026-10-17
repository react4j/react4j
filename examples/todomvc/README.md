# TodoMVC

Three implementations of the same task list: raw React4j, Arez state management, and
Arez with Sting dependency injection.

| Variant | Hosted demo | Local example name |
| --- | --- | --- |
| Raw | [Run](https://react4j.github.io/todomvc/raw/todomvc/) | `todomvc-raw` |
| Arez | [Run](https://react4j.github.io/todomvc/arez/todomvc/) | `todomvc-arez` |
| Sting | [Run](https://react4j.github.io/todomvc/sting/todomvc/) | `todomvc-sting` |

Run an example from the repository root:

```sh
tools/examples.sh serve todomvc-raw development
```

Open [localhost:3000](http://localhost:3000). See [CONTRIBUTING.md](../../CONTRIBUTING.md)
for build setup and other commands.

Tasks are kept in memory and disappear on reload. While editing, Enter saves, Escape
cancels, and an empty title deletes the task.
