# Drumloop

A drum machine built with React4j, Arez, Sting, Web Audio, and experimental React Suspense.

Run from the repository root:

```sh
tools/examples.sh serve drumloop development
tools/examples.sh build drumloop production
bazel test //examples/drumloop:browser_tests
bazel test -c opt //examples/drumloop:browser_tests
```

The preview serves at http://localhost:3000. Use Play/Stop and the BPM input to control the
sequencer. Click a step to toggle it; Shift-click selects a doubled hit. Hold an effect button
for momentary playback, or Shift-click to loop it and click again to stop.

The generated documentation site includes `/drumloop/`, `/drumloop/drumloop/` (production),
and `/drumloop/drumloop_dev/` (development). Build or preview it with `tools/site.sh build`
or `tools/site.sh serve`.

The application uses local sound samples and the Righteous font. The font's license is in
[public/fonts/OFL.txt](public/fonts/OFL.txt); application code uses the repository's
[Apache 2.0 license](../../LICENSE).
