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

Component styles live in static nested classes inside each view. [StepButton.java](src/main/java/react4j/drumloop/views/StepButton.java)
includes the doubled-step animation. Global rules live in [shared.css](src/main/css/shared.css). The font face remains a separate
[CSS asset](src/main/assets/css/font.css). The moving indicator passes its position through
the `--indicator-left` custom property.
