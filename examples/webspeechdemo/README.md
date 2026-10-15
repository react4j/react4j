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

To explore the code:

- [Application.java](src/main/java/react4j/webspeechdemo/Application.java) connects the
  speech controls to the model and positions the word highlight using a DOM range.
- [SpeechData.java](src/main/java/react4j/webspeechdemo/SpeechData.java) connects browser
  speech events and voice-list changes to Arez state.

Inspired by [eeejay/webspeechdemos](https://github.com/eeejay/webspeechdemos).
Code uses the repository's [Apache 2.0 license](../../LICENSE).
