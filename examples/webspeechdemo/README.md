# Web Speech Demo

A browser speech synthesis example built with React4j and Arez.

[Try Web Speech Demo](https://react4j.github.io/webspeechdemo/), or run it locally from the
repository root:

```sh
tools/examples.sh serve webspeechdemo development
```

Open [localhost:3000](http://localhost:3000). See [CONTRIBUTING.md](../../CONTRIBUTING.md)
for build setup and other commands.

Available voices and word highlighting depend on the browser and operating system. If the
page is blank, check the browser console for `Speech API not present`.

Inspired by [eeejay/webspeechdemos](https://github.com/eeejay/webspeechdemos).

Component styles live in static nested classes in [Application.java](src/main/java/react4j/webspeechdemo/Application.java),
with page and control defaults in [shared.css](src/main/css/shared.css). The editor receives
its speaking modifier directly. The word marker uses a visibility modifier and four custom
properties for its measured position and size.
