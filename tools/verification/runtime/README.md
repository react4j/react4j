# Browser runtime comparison

Use these checks when changing React4j's renderer. The reference is the last implementation that shipped the pinned React and ReactDOM 16.6.0 scripts, commit `18e896de70f9e7ebae0ac6be8e9f5506a1949997`. Both implementations run the same React4j test code in Chromium.

## Behavior

From the repository root on Linux, create a reference checkout in a new directory and run both test configurations.

```sh
work=$(mktemp -d)
tools/verification/runtime/reference.sh "$work/reference"
for mode in fastbuild opt; do
  bazel test -c "$mode" --test_env=CHROME_DEVEL_SANDBOX //dom/src/test/js/react4j/dom:browser_tests
done
```

The script copies the current browser scenarios to the reference. It supplies Closure with the error boundary callback signature and component stack field used by the original binaries. One reference page disables `TextEvent` before loading ReactDOM to exercise its keypress fallback.

The scenarios compare DOM and component identity, lifecycle and callback order, batching, context, refs, portals, Suspense retries, error recovery, focus and selection, controlled and uncontrolled inputs, and synthetic event propagation. The repository gate also compiles generated Arez and Sting components and runs the example applications.

## Workloads and browser extension

Build the TodoMVC applications in both checkouts. Run the commands below from the candidate using Node 22 or later. Set `CHROME_DEVEL_SANDBOX` to the installed Chrome sandbox helper on machines that require it.

```sh
targets='//examples/todomvc/raw:raw //examples/todomvc/arez:arez //examples/todomvc/sting:sting'
(cd "$work/reference" && bazel build -c opt $targets && bazel build //examples/todomvc/raw:raw)
bazel build -c opt $targets
bazel build //examples/todomvc/raw:raw
chrome="$(bazel info execution_root)/external/+browser_repo+react4j_chrome_linux/chrome-linux64/chrome"
opt=$(bazel info -c opt bazel-bin)
ref_opt=$(cd "$work/reference" && bazel info -c opt bazel-bin)
for app in raw arez sting; do
  path="examples/todomvc/$app/$app"
  node tools/verification/runtime/measure.mjs "$chrome" "$ref_opt/$path" "$opt/$path" > "$work/$app.json"
  node tools/verification/runtime/compare.mjs "$work/$app.json"
done
```

Each run starts a fresh browser profile, serves the two applications on separate loopback ports, and alternates their order within each pair. It discards one warm-up pair and takes the median of ten measured pairs. Each sample loads an empty list, adds 50 rows using input and Enter events, toggles 25 checkboxes, and dispatches 1,000 navigation keys to the input handler. The two interactive measurements include time until the visible updates finish. The Arez and Sting variants include their scheduled updates. The harness checks every label in order, the final checkboxes and input value, and retained DOM identity.

Heap is the used V8 heap after a forced collection with the list mounted. Bundle sizes sum the scripts loaded by the production document, raw and individually gzipped at level 9. A comparison fails if a candidate median or size exceeds the reference by more than 10%, with a 1 ms minimum allowance for timings. Run measurements on an idle machine.

Supply the installed React DevTools extension directory containing its manifest to run the extension check against the development applications.

```sh
dev=$(bazel info bazel-bin)
ref_dev=$(cd "$work/reference" && bazel info bazel-bin)
path=examples/todomvc/raw/raw
node tools/verification/runtime/measure.mjs "$chrome" "$ref_dev/$path" "$dev/$path" "$REACT_DEVTOOLS" > "$work/devtools.json"
```

This opens Chrome DevTools, uses the installed extension to inspect a generated component and its named input, checks the component name and dimensions in its highlight overlay, and records component durations during the workload. The run compares both extension profiles' commit counts and fails on browser exceptions. Development enables view names and readable input keys. Browser application tests check that the BUNDLE and ADVANCED maps include the Java source for both the renderer and the application.

## Measured results

Linux x86-64, Intel Core Ultra 7 255H, Chrome for Testing 145.0.7632.117 and Node 26.12.1. Each cell gives the React 16.6 reference followed by the Java renderer. Run zero in the [individual measurements](measurements.csv) is the warm-up.

| Production application | Load (ms) | Add 50 (ms) | Toggle 25 (ms) | 1,000 keys (ms) | Heap (bytes) | JavaScript (bytes) | Gzip (bytes) |
| --- | --- | --- | --- | --- | --- | --- | --- |
| Raw | 29.60 / 22.65 | 19.15 / 20.95 | 3.80 / 4.10 | 5.25 / 5.30 | 1,995,368 / 1,811,142 | 166,367 / 107,265 | 54,943 / 36,009 |
| Arez | 29.70 / 23.90 | 208.50 / 205.30 | 8.00 / 7.10 | 9.50 / 8.35 | 2,587,270 / 2,406,932 | 184,824 / 127,522 | 59,922 / 41,336 |
| Sting | 32.60 / 27.80 | 213.05 / 215.10 | 7.75 / 7.05 | 10.45 / 7.70 | 2,592,172 / 2,412,842 | 185,631 / 128,393 | 60,272 / 41,643 |

All three applications passed the comparison budget. Raw row insertion had the largest median slowdown, 1.80 ms for 50 rows (9.4%). The installed Chrome extension, React Developer Tools 8.0.0, inspected `TodoItem.todo`, highlighted `TodoEntry` and reported 125 commits with measured component durations on both development applications.
