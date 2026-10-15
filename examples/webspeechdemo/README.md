# Web Speech Demo

A browser speech synthesis example built with React4j and Arez, inspired by
[eeejay/webspeechdemos](https://github.com/eeejay/webspeechdemos).

Run from the repository root:

```sh
tools/examples.sh serve webspeechdemo development
tools/examples.sh build webspeechdemo production
bazel test //examples/webspeechdemo:browser_tests
bazel test -c opt //examples/webspeechdemo:browser_tests
```

The preview serves at http://localhost:3000. Edit the text, adjust pitch, rate and volume,
select a voice, and use Speak, Pause/Resume and Stop. Reset buttons restore each setting.
The demo highlights word boundaries reported by the speech engine. Voice availability,
boundary events and audible playback depend on the browser and operating system.

The generated site serves the production app at `/webspeechdemo/` and the development app
at `/webspeechdemo_dev/`. Build or preview it with `tools/site.sh build` or `tools/site.sh serve`.

Browser tests load the packaged page and exercise the native speech API with a silent
utterance. Separate deterministic tests control the speech engine and voice inventory while
retaining native utterances, events and DOM ranges. They cover voice changes, settings,
speech lifecycle, highlighting and the unsupported-API path; they do not verify audible output.

Application code uses the repository's [Apache 2.0 license](../../LICENSE).
