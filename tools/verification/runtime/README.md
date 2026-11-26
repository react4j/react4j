# Browser runtime comparison

Use these checks when changing React4j's renderer. The reference is the last implementation that shipped the pinned React and ReactDOM 16.6.0 scripts, commit `18e896de70f9e7ebae0ac6be8e9f5506a1949997`. Both implementations run the same React4j test code in Chromium.

## Behavior

From the repository root on Linux, create a reference checkout in a new directory and run both test configurations.

```sh
work=$(mktemp -d)
tools/verification/runtime/reference.sh "$work/reference"
for mode in fastbuild opt; do
  bazel test -c "$mode" --test_env=CHROME_DEVEL_SANDBOX //:browser_tests
done
```

The script copies the current browser scenarios to the reference. It supplies Closure with the error boundary callback signature and component stack field used by the original binaries. One reference page disables `TextEvent` before loading ReactDOM to exercise its keypress fallback.

The original Java bindings constructed element records, used React's element and owner symbols, created context providers and consumers, and derived generated views from `React.Component`. The DOM bindings called `render`, `unmountComponentAtNode`, `unstable_createRoot`, `createPortal`, and `unstable_batchedUpdates`. The Drumloop application exercises concurrent roots. The TodoMVC applications exercise stateful and Arez-tracked views, including Sting-generated services.

The browser suites cover DOM and component identity, lifecycle and callback order, batching, context, refs, portals, Suspense retries, error recovery, focus and selection, controlled and uncontrolled inputs, and synthetic event propagation. Concurrent-root cases interrupt rendering between components and inspect the DOM and refs before and after the commit. They also exercise delayed fallbacks and repeated clicks in one browser task. Custom-element callbacks and changed refs expose the order of DOM writes, keyed moves, and deletions. The tests retain a removed DOM subtree and check focus and selection after moving a keyed ancestor. Host property cases distinguish HTML, SVG and MathML attribute casing, raw qualified names and namespace aliases. Both builds run the application scenarios. The repository gate compiles generated Arez and Sting components.

## Workloads and browser extension

Build the TodoMVC applications in both checkouts. Run the commands below from the candidate using Node 22 or later. Set `CHROME_DEVEL_SANDBOX` to the installed Chrome sandbox helper on machines that require it.

```sh
targets='//examples/todomvc/raw:raw //examples/todomvc/arez:arez //examples/todomvc/sting:sting'
(cd "$work/reference" && bazel build -c opt $targets && bazel build //examples/todomvc/raw:raw)
bazel build -c opt $targets
bazel build //examples/todomvc/raw:raw
chrome="$(bazel info output_base)/external/+browser_repo+react4j_chrome_linux/chrome-linux64/chrome"
opt=$(bazel info -c opt bazel-bin)
ref_opt=$(cd "$work/reference" && bazel info -c opt bazel-bin)
export REACT4J_BENCHMARK_PAIRS=30
for app in raw arez sting; do
  path="examples/todomvc/$app/$app"
  node tools/verification/runtime/measure.mjs "$chrome" "$ref_opt/$path" "$opt/$path" > "$work/$app.json"
  node tools/verification/runtime/compare.mjs "$work/$app.json"
done
```

Each run starts a fresh browser profile, serves the two applications on separate loopback ports, and alternates their order within each pair. It discards one warm-up pair and takes the median over the requested number of measured pairs (ten by default). Each sample loads an empty list, adds 50 rows using input and Enter events, toggles 25 checkboxes, and dispatches 1,000 navigation keys to the input handler. The two interactive measurements include time until the visible updates finish. The Arez and Sting variants include their scheduled updates. The harness checks every label in order, the final checkboxes and input value, and retained DOM identity.

Heap is the used V8 heap after a forced collection with the list mounted. Bundle sizes sum the scripts loaded by the production document, raw and individually gzipped at level 9. A comparison fails if a candidate median or size exceeds the reference by more than 10%, with a 1 ms minimum allowance for timings. The comparator converts median times to nanoseconds before applying the budget so binary timestamp subtraction preserves the 1 ms limit. Run measurements on an idle machine. To check measurement noise, pass the same production directory for both applications. On a hybrid Linux CPU, prefix the Node command with `taskset -c` and the CPU list from `/sys/devices/cpu_core/cpus` to run both copies of the workload on performance cores.

Set `REACT_DEVTOOLS` to the installed extension directory containing its manifest. The check runs against both development applications, the default production application, and an optimized production build with readable component and input names.

```sh
dev=$(bazel info bazel-bin)
ref_dev=$(cd "$work/reference" && bazel info bazel-bin)
path=examples/todomvc/raw/raw
bazel build -c opt //tools/verification/runtime:named_production
node tools/verification/runtime/measure.mjs "$chrome" "$ref_dev/$path" "$dev/$path" "$REACT_DEVTOOLS" \
  "$opt/$path" "$opt/tools/verification/runtime/named_production" > "$work/devtools.json"
```

This opens Chrome DevTools, inspects a generated component, checks the highlight overlay, and records component durations during the workload. Each profile must contain a timed update to the component that owns the input, and their commit counts must match. The default production build minimizes view names and input keys. To read them in a production profile, set `react4j.enable_view_names` to `'true'` and `react4j.minimize_input_keys` to `'false'` in the application's Closure defines, as in the [named production target](BUILD.bazel). Browser application tests check that the BUNDLE and ADVANCED maps include the Java source for both the renderer and the application.

## Measured results

Candidate runtime at `2aa7b48f5`. Linux x86-64, Intel Core Ultra 7 255H, Chrome for Testing 145.0.7632.117 and Node 26.12.1. Both runtimes used CPUs 0–5 for the primary comparison. Each cell gives the pinned React 16.6 median followed by the Java renderer median. The sample file also includes the unrestricted comparison on CPUs 0–15, a Raw comparison on efficiency cores 6–13, and Raw controls with identical pinned bundles. Run zero is the discarded warm-up. [Individual samples](measurements.csv)

| Production application | Load (ms) | Add 50 (ms) | Toggle 25 (ms) | 1,000 keys (ms) | Heap (bytes) | JavaScript (bytes) | Gzip (bytes) |
| --- | --- | --- | --- | --- | --- | --- | --- |
| Raw | 27.70 / 28.70 | 22.45 / 23.55 | 3.40 / 3.10 | 5.30 / 4.70 | 1,998,756 / 1,935,274 | 166,367 / 123,795 | 54,943 / 42,218 |
| Arez | 32.25 / 34.50 | 214.50 / 214.20 | 7.45 / 6.70 | 10.30 / 8.90 | 2,587,350 / 2,514,514 | 184,824 / 143,995 | 59,922 / 47,611 |
| Sting | 32.95 / 34.60 | 214.45 / 214.60 | 7.45 / 6.70 | 10.25 / 9.15 | 2,592,212 / 2,519,798 | 185,631 / 144,869 | 60,272 / 47,927 |

All three applications passed every budget on CPUs 0–5 and under unrestricted scheduling. With unrestricted scheduling, adding 50 rows took 19.60 / 19.95 ms for Raw, 196.45 / 205.55 ms for Arez and 206.50 / 208.70 ms for Sting. Raw also passed on efficiency cores, where insertion took 14.90 / 15.40 ms.

The control runs loaded the same pinned Raw bundle on both sides. Every metric passed in the ten-pair run on CPUs 0–5 and the thirty-pair unrestricted run. The ten-pair unrestricted control crossed the keyboard cutoff at 5.70 / 7.05 ms. The samples also retain the earlier unrestricted Raw comparison at `fa7159007`, where insertion crossed the cutoff at 16.20 / 20.10 ms.

React Developer Tools 8.0.0 inspected a generated component, highlighted its host element, and recorded 125 commits in each build below. The table counts commits with a positive duration in the extension export. A commit can appear with zero duration at the browser's timer resolution.

| Profile | Commits with a positive component duration | Updates with a positive TodoEntry duration | Peak TodoEntry duration (ms) |
| --- | ---: | ---: | ---: |
| React 16.6 development | 95 | 30 | 0.6 |
| Java development | 90 | 24 | 0.2 |
| Java production | 68 | 8 | 0.1 |
| Java production with names | 59 | 7 | 0.1 |

The named production build exposed `TodoEntry` and `TodoItem.todo`. On the stock ReactDOM 16.6.0 production script, the same extension could select the component but recorded zero profiled commits.
