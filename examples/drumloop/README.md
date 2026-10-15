# Drumloop

A Web Audio drum machine with React4j views and Arez state.

[Try Drumloop](https://react4j.github.io/drumloop/), or run it locally from the repository root:

```sh
tools/examples.sh serve drumloop development
```

Open [localhost:3000](http://localhost:3000). See [CONTRIBUTING.md](../../CONTRIBUTING.md)
for build setup and other commands.

Shift-click a step for a doubled hit. Hold an effect button for momentary playback, or
Shift-click to loop it and click again to stop.

To explore the code:

- [DrumMachine.java](src/main/java/react4j/drumloop/model/DrumMachine.java) owns tempo,
  sequencing and audio playback.
- [DrumMachineView.java](src/main/java/react4j/drumloop/views/DrumMachineView.java) and
  [ReactCache.java](src/main/java/react4j/drumloop/cache/ReactCache.java) use experimental
  React Suspense to wait for sound samples to load.
- [DrumLoopInjector.java](src/main/java/react4j/drumloop/ioc/DrumLoopInjector.java) wires the
  model and views with Sting.

Code uses the repository's [Apache 2.0 license](../../LICENSE). The Righteous font has its
own [license](public/fonts/OFL.txt).
